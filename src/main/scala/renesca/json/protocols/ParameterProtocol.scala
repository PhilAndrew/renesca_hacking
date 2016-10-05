package renesca.json.protocols

import renesca.parameter.{LongPropertyValue, PropertyKey}

object ParameterProtocol {

  object JsonPropertyKeyFormat {
    def toJson(obj: PropertyKey): String = {
      obj.name
    }

    def fromJson(str: String): PropertyKey = {
      PropertyKey(str)
    }
  }
}
