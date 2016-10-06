package renesca

// QueryHandler ensures that we have the same query-interface in Transaction and in DbService.
// Both must implement queryService and handleError.
// The query-interface consists of the methods:
//  queryGraph  - submit one query [with parameters] and return a Graph
//  queryTable  - submit one query [with parameters] and return a row set
//  queryGraphs - submit multiple queries [with parameters] and return a Graph for each result
//  queryTables - submit multiple queries [with parameters] and return a row set for each result
//  query       - submit one or multiple queries [with parameters] as a side effect (returns Unit)
//  persist     - save a modified graph to the database

import renesca.graph._
import renesca.parameter._
import renesca.schema.{AbstractRelation, HyperRelation}
import renesca.table.Table

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Query {
  implicit def stringToQuery(statement: String): Query = Query(statement)
}

case class Query(statement: String, parameters: ParameterMap = Map.empty)

trait QueryInterface {
  def queryWholeGraph: Future[Graph]
  def queryGraph(query: Query): Future[Graph]
  def queryTable(query: Query): Future[Table]
  def queryGraphAndTable(query: Query): Future[(Graph, Table)]
  def queryGraphs(queries: Query*): Future[Seq[Graph]]
  def queryTables(queries: Query*): Future[Seq[Table]]
  def queryGraphsAndTables(queries: Query*): Future[Seq[(Graph, Table)]]
  def query(queries: Query*): Future[List[json.Result]]

  def queryGraph(statement: String, parameters: ParameterMap = Map.empty): Future[Graph] = queryGraph(Query(statement, parameters))
  def queryTable(statement: String, parameters: ParameterMap = Map.empty): Future[Table] = queryTable(Query(statement, parameters))
  def queryGraphAndTable(statement: String, parameters: ParameterMap = Map.empty): Future[(Graph, Table)] = queryGraphAndTable(Query(statement, parameters))
  def query(statement: String, parameters: ParameterMap = Map.empty): Future[List[json.Result]] = query(Query(statement, parameters))

  def persistChanges(graph: Graph): Future[Option[String]]

  def persistChanges(schemaGraph: schema.Graph): Future[Option[String]] = {
    validateSchemaGraph(schemaGraph) match {
      case Some(err) => Future.failed(new Exception(err))
      case None => persistChanges(schemaGraph.graph)
    }
  }

  def persistChanges(item: Item, items: Item*): Future[Option[String]] = {
    val allItems = item :: items.toList
    val graph = Graph(allItems.collect { case n: Node => n }, allItems.collect { case r: Relation => r })
    persistChanges(graph)
  }

  def persistChanges(item: schema.Item, items: schema.Item*): Future[Option[String]] = {
    val allItems = item :: items.toList
    validateSchemaItems(allItems) match {
      case Some(err) => Future.failed(new Exception(err))
      case None =>
        val schemaGraph = new schema.Graph {
          // the schema graph methods will never be called,
          // but we use the implementation of the add function,
          // which also adds the path of hyperrelations to the graph.
          override def nodes: Seq[_ <: schema.Node] = ???
          override def hyperRelations: Seq[_ <: HyperRelation[_, _, _, _, _]] = ???
          override def relations: Seq[_ <: schema.Relation[_, _]] = ???
          override def abstractRelations: Seq[_ <: AbstractRelation[_, _]] = ???
          val graph: Graph = Graph.empty
        }

        schemaGraph.add(allItems: _*)
        persistChanges(schemaGraph.graph)
    }
  }

  def validateSchemaGraph(schemaGraph: schema.Graph): Option[String] = {
    val changes = schemaGraph.graph.changes.collect { case c: GraphItemChange => c.item }.toSet
    val items = (schemaGraph.nodes ++ schemaGraph.relations).filter(changes contains _.rawItem)
    validateSchemaItems(items)
  }

  def validateSchemaItems(items: Iterable[schema.Item]): Option[String] = {
    val validations = items.map { item =>
      item.validate match {
        case Some(err) => Some(s"Validation for item '${item.rawItem}' failed: $err")
        case None => None
      }
    }.flatten

    if (validations.isEmpty)
      None
    else
      Some(validations.mkString(","))
  }
}

trait QueryHandler extends QueryInterface {
  val builder = new QueryBuilder

  override def queryWholeGraph: Future[Graph] = queryGraph("match (n) optional match (n)-[r]-() return n,r")
  override def queryGraph(query: Query): Future[Graph] = queryGraphs(query).map(_.head)
  override def queryTable(query: Query): Future[Table] = queryTables(query).map(_.head)
  override def queryGraphAndTable(query: Query): Future[(Graph, Table)] = queryGraphsAndTables(query).map(_.head)

  override def queryGraphs(queries: Query*): Future[Seq[Graph]] = {
    for (results <- executeQueries(queries, List("graph"))) yield extractGraphs(results)
  }

  override def queryTables(queries: Query*): Future[Seq[Table]] = {
    for (results <- executeQueries(queries, List("row"))) yield extractTables(results)
  }

  override def queryGraphsAndTables(queries: Query*): Future[Seq[(Graph, Table)]] = {
    for (results <- executeQueries(queries, List("row", "graph"))) yield extractGraphs(results) zip extractTables(results)
  }

  def query(queries: Query*): Future[List[json.Result]] = { executeQueries(queries, Nil) }

  //TODO: persist changes should ONLY work on transactions!
  def persistChanges(graph: Graph): Future[Option[String]] = {
    builder.generateQueries(graph.changes) match {
      case Left(msg) => Future.successful(Some(msg)) // @todo PHILIP Future.successful ??? is that right
      case Right(queries) =>
        for (failure <- builder.applyQueries(queries, queryGraphsAndTables)) yield {
          if (failure.isEmpty)
            graph.clearChanges()

          failure
        }
    }
  }

  protected def executeQueries(queries: Seq[Query], resultDataContents: List[String]): Future[List[json.Result]] = {
    val jsonRequest = buildJsonRequest(queries, resultDataContents)
    for (jsonResponse <- queryService(jsonRequest);
         e <- handleError(exceptionFromErrors(jsonResponse))) yield jsonResponse.results
  }

  protected def extractGraphs(results: Seq[json.Result]): Seq[Graph] = {
    println("### extractGraphs")
    println(results.toString())
    val allJsonGraphs: Seq[List[json.Graph]] = results.map(_.data.flatMap(_.graph))
    allJsonGraphs.map(_.map(json.GraphFactory(_)).fold(Graph.empty)(_ merge _))
  }

  protected def extractTables(results: Seq[json.Result]): Seq[Table] = {
    results.map(r => json.TableFactory(r))
  }

  protected def buildJsonRequest(queries: Seq[Query], resultDataContents: List[String]): json.Request = {
    json.Request(queries.map(query => json.Statement(query, resultDataContents)).toList)
  }

  protected def exceptionFromErrors(jsonResponse: json.Response): Option[RuntimeException] = {
    jsonResponse.errors match {
      case Nil => None
      case errors =>
        val message = errors.map {
          case json.Error(code, msg) => s"$code\n$msg"
        }.mkString("\n", "\n\n", "\n")
        Some(new RuntimeException(message))
    }
  }

  protected def queryService(jsonRequest: json.Request): Future[json.Response]
  protected def handleError(exceptions: Option[Exception]): Future[Unit]
}

class Transaction extends QueryHandler { thisTransaction =>

  var restService: RestService = null //TODO: inject

  var id: Option[TransactionId] = None

  private var valid = true
  def isValid = valid
  def invalidate() { valid = false }
  private def throwIfNotValid() {
    if (!valid)
      throw new RuntimeException("Transaction is not valid anymore.")
  }

  override protected def queryService(jsonRequest: json.Request): Future[json.Response] = {
    throwIfNotValid()
    id match {
      case Some(transactionId) => restService.resumeTransaction(transactionId, jsonRequest)
      case None =>
        restService.openTransaction(jsonRequest).map{t =>
          val (transactionId, jsonResponse) = t
          id = Some(transactionId)
          jsonResponse
        }
    }
  }

  protected def handleError(exceptions: Option[Exception]): Future[Unit] = {
    if (exceptions.isDefined) {
      rollback()
      Future.failed(exceptions.get)
    } else Future.successful(Unit)
  }

  def rollback(): Future[Unit] = {
    val future = id match {
      case Some(transactionId) => restService.rollbackTransaction(transactionId)
      case None => Future.successful(Unit)
    }
    for (f <- future) yield invalidate()
  }

  val commit = new CommitTransaction

  class CommitTransaction extends QueryHandler {
    // Important:
    // queryService can be called only once, because it commits and invalidates the transaction.
    // This means that methods like persistChanges which are firing multiple queries and thus calling querySerice
    // multiple times need to be modified or wrapped.

    def apply(): Future[_] = {
      throwIfNotValid()

      if (id.isDefined) {
        val transactionId = id.get
        for (jsonResponse <- restService.commitTransaction(transactionId);
             e <- handleError(exceptionFromErrors(jsonResponse))) yield invalidate()
      } else
      Future.successful(Unit)
    }

    override protected def queryService(jsonRequest: json.Request): Future[json.Response] = {
      // TODO: share code with this.Transaction.queryService
      throwIfNotValid()
      val jsonResponse = id match {
        case Some(transactionId) => restService.commitTransaction(transactionId, jsonRequest)
        case None => restService.singleRequest(jsonRequest)
      }

      invalidate()
      jsonResponse
    }

    override def persistChanges(graph: Graph): Future[Option[String]] = {
      //TODO: this solution can send one REST request more than needed, as the commit request does not do any changes
      val errorOpt = thisTransaction.persistChanges(graph)
      for (e <- errorOpt) yield {
        e.map(error => {
          rollback()
          Some(error)
        }).getOrElse({
          apply() // commit
          None
        })
      }
    }

    override protected def handleError(exceptions: Option[Exception]) = thisTransaction.handleError(exceptions)
  }

  override def toString = s"Transaction(id=$id, valid:$isValid, $restService)"
}

class DbService extends QueryHandler {
  var restService: RestService = null //TODO: inject

  protected def handleError(exceptions: Option[Exception]): Future[Unit] = {
    if (exceptions.isDefined)
      Future.failed(exceptions.get)
    else
      Future.successful(Unit)

    //for (exception <- exceptions)
    //  throw exception
  }

  override protected def queryService(jsonRequest: json.Request): Future[json.Response] = {
    restService.singleRequest(jsonRequest)
  }

  def newTransaction() = {
    val tx = new Transaction
    tx.restService = restService
    tx
  }

  def transaction[T](code: Transaction => T): T = {
    val tx = newTransaction
    val result = try {
      code(tx)
    } catch {
      case e: Exception =>
        tx.rollback()
        throw e
    }

    // if there was a request and transacion is not done yet
    if (tx.id.isDefined && tx.isValid) tx.commit()

    result
  }
}
