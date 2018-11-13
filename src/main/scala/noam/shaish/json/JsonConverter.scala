package noam.shaish.json

trait JsonConverter[A] {
  def convertToJson(value: A): JsonValue
}
