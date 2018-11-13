package noam.shaish.json

sealed trait JsonValue
case class JsonObject(entries: Map[String, JsonValue]) extends JsonValue
case class JsonArray(entries: Seq[JsonValue]) extends JsonValue
case class JsonString(value: String) extends JsonValue
case class JsonNumber(value: Long) extends JsonValue
case class JsonBoolean(value: Boolean) extends JsonValue
case object JsonNull extends JsonValue

object JsonWriter {
  def write(value: JsonValue): String = value match {
    case JsonObject(entries) =>
      val serializedEntries = for((key, value) <- entries) yield key + ":" + write(value)
      "{" + serializedEntries.mkString(", ") + "}"
    case JsonArray(entries) =>
      val serializedEntries = entries.map(write)
      "[" + serializedEntries.mkString(", ") + "]"
    case JsonString(sValue) => "\"" + sValue + "\""
    case JsonNumber(nValue) => nValue.toString
    case JsonBoolean(bValue) => bValue.toString
    case JsonNull => "null"
  }
}
