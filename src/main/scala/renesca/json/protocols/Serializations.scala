package renesca.json.protocols

import org.json4s._
import org.json4s.{CustomKeySerializer, CustomSerializer, _}
import renesca.json.Request
import renesca.parameter.{ArrayParameterValue, BooleanArrayPropertyValue, BooleanPropertyValue, DoubleArrayPropertyValue, DoublePropertyValue, LongArrayPropertyValue, LongPropertyValue, MapParameterValue, NullPropertyValue, ParameterValue, PropertyKey, PropertyValue, StringArrayPropertyValue, StringPropertyValue, _}
import org.json4s._
import org.json4s._

//: ----------------------------------------------------------------------------------
//: Copyright © 2016 Philip Andrew https://github.com/PhilAndrew  All Rights Reserved.
//: ----------------------------------------------------------------------------------

class PropertyKeySerializer() extends CustomKeySerializer[PropertyKey](format => ( {
  case s:String => PropertyKey(s)
  case json      => { println("Can not deserialize property key of type $json"); null }
}, {
  case p: PropertyKey =>  {
    p.name
  }
}))

object ParameterValueSerializer {
  def parameterValueMatch(p: ParameterValue): JValue = {
    p match {
      case LongArrayPropertyValue(that) => {
        println("@#@# why? 1")
        null
      }
      case DoubleArrayPropertyValue(that) => {
        println("@#@# why? 1")
        null
      }
      case StringArrayPropertyValue(that) => {
        println("@#@# why? 1")
        null
      }
      case BooleanArrayPropertyValue(that) => {
        println("@#@# why? 1")
        null
      }
      case MapParameterValue(keyValuePairs) => {
        println("Map parameter value ####")
        new JObject(keyValuePairs.map { case (key, value) => (key.name, {
          value match {
            case LongPropertyValue(v) => new JLong(v)
            case StringPropertyValue(v) => new JString(v)
            case DoublePropertyValue(v) => new JDouble(v)
            case BooleanPropertyValue(v) => new JBool(v)
            case NullPropertyValue => JNull
            case that: ArrayParameterValue => {
              println("@#@# why?")
              null
            }
            case _ => {
              println("@#@# UNSURE !!!!!")
              null
            }
          }
        })
        }.toList)
      }
      case _ => {
        println("@#@# why")
        null
      }
    }
  }
}

class PropertyValueSerializer() extends CustomSerializer[PropertyValue](format => ( {
  case JString(s) => { StringPropertyValue(s) }
  case JDouble(s) => { DoublePropertyValue(s) }

  case JDecimal(s) => { LongPropertyValue(s.toLong) }
  case JLong(s) => { LongPropertyValue(s.toLong) }
  case JInt(s) => { LongPropertyValue(s.toLong) }

  case JBool(s)   => { BooleanPropertyValue(s) }
  case JObject(s) => { println("@#@# !!## JObject")
    null; }
  case JArray(s) => { println("@#@# !!## JArray")
    null }
  case it => {
    println("@#@# property value serializer 1###")
    println(it.toString)
    println("" + it.getClass.toString)
    null }
}, {


  case LongPropertyValue(v) => JLong(v)
  case StringPropertyValue(v) => JString(v)
  case DoublePropertyValue(v) => JDouble(v)
  case BooleanPropertyValue(v) => JBool(v)
  case NullPropertyValue => JNull
  case that: ArrayParameterValue => {
    println("@#@# why?")
    null
  }
  case it => {
    println(it.toString)
    println(it.getClass.toString)
    println("@#@# UNSURE !!!!!")
    null
  }

})) {

}

class ParameterValueSerializer() extends CustomSerializer[ParameterValue](format => ( {
  case JString(s) => { StringPropertyValue(s) }
  case JDouble(s) => { DoublePropertyValue(s) }
  case JDecimal(s) => { LongPropertyValue(s.toLong) }
  case JLong(s) => { LongPropertyValue(s.toLong) }
  case JInt(s) => { LongPropertyValue(s.toLong) }
  case JBool(s)   => { BooleanPropertyValue(s) }
  case JObject(s) => { println("!!## JObject")
    null; }
  case JArray(s) => { println("!!## JArray")
    null }
  case _ => { println("!!## Unknown"); null }
}, {
  case Request(s) => { null }
}))

object ParameterMapSerializer {
  def parameterValueOf(it: ParameterValue) = {
    it match {
      case MapParameterValue(v) => new JObject(v.toList.map((f) => { (f._1.name, {
        f._2 match {
          case LongPropertyValue(v) => JLong(v)
          case StringPropertyValue(v) => JString(v)
          case DoublePropertyValue(v) => JDouble(v)
          case BooleanPropertyValue(v) => JBool(v)
          case NullPropertyValue => JNull
          case BooleanArrayPropertyValue(v) => {
            println("@#@# why ??????")
            null
          }
          case DoubleArrayPropertyValue(v) => {
            println("@#@# why ??????")
            null
          }
          case LongArrayPropertyValue(v) => {
            println("@#@# why ??????")
            null
          }
          case StringArrayPropertyValue(v) => {
            println("@#@# why ??????")
            null
          }
          case MapParameterValue(v) => {
            println("@#@# why ??????")
            null
          }
          case _ => {
            println("@#@# why?")
            null
          }
        }
      })}))
      case LongPropertyValue(v) => new JLong(v)
      case StringPropertyValue(v) => new JString(v)
      case DoublePropertyValue(v) => new JDouble(v)
      case BooleanPropertyValue(v) => new JBool(v)
      case NullPropertyValue => JNull
      case BooleanArrayPropertyValue(v) => {
        println("@#@# why ??????")
        null
      }
      case DoubleArrayPropertyValue(v) => {
        println("@#@# why ??????")
        null
      }
      case LongArrayPropertyValue(v) => {
        println("@#@# why ??????")
        null
      }
      case StringArrayPropertyValue(v) => {
        println("@#@# why ??????")
        null
      }
      case MapParameterValue(v) => {
        println("@#@# why ??????")
        null
      }
      case _ => {
        println("@#@# why?")
        null
      }
    }
  }
}

class ParameterMapSerializer() extends CustomSerializer[ParameterMap](format => ( {
  case _ => { println("Attempt to do parameter map 1"); null }
}, {
  case p: ParameterMap => { new JObject(p.toList.map( (f) => { (f._1.name, ParameterMapSerializer.parameterValueOf(f._2))})) }
}))



