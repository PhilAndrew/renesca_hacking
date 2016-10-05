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

class PropertyValueSerializer() extends CustomSerializer[PropertyValue](format => ( {
  case JString(s) => StringPropertyValue(s)
  case JDouble(s) => DoublePropertyValue(s)

  case JDecimal(s) => LongPropertyValue(s.toLong)
  case JLong(s) => LongPropertyValue(s.toLong)
  case JInt(s) => LongPropertyValue(s.toLong)

  case JBool(s)   => BooleanPropertyValue(s)
}, {
  case _ => null
})) {
}

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
        }
      })}))
      case LongPropertyValue(v) => JLong(v)
      case StringPropertyValue(v) => JString(v)
      case DoublePropertyValue(v) => JDouble(v)
      case BooleanPropertyValue(v) => JBool(v)
      case NullPropertyValue => JNull
    }
  }
}

class ParameterMapSerializer() extends CustomSerializer[ParameterMap](format => ( {
  case _ => { null }
}, {
  case p: ParameterMap => { new JObject(p.toList.map( (f) => { (f._1.name, ParameterMapSerializer.parameterValueOf(f._2))})) }
}))
