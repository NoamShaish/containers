package noam.shaish.json

import noam.shaish.expression.{Expression, Minus, Number, Plus}

trait Json[A] {
  def from(value: A): JsonValue
}

object Json {
  implicit val expressionJson = new Json[Expression] {
    override def from(value: Expression): JsonValue = value match {
      case Number(nValue) => JsonNumber(nValue)
      case Plus(lhs, rhs) => JsonObject(Map(
        "op" -> JsonString("+"),
        "lhs" -> from(lhs),
        "rhs" -> from(rhs)
      ))
      case Minus(lhs, rhs) => JsonObject(Map(
        "op" -> JsonString("-"),
        "lhs" -> from(lhs),
        "rhs" -> from(rhs)
      ))
    }
  }
}