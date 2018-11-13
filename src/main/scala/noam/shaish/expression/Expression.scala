package noam.shaish.expression

import noam.shaish.json._

sealed trait Expression

case class Number(value: Int) extends Expression
case class Plus(lhs: Expression, rhs: Expression) extends Expression
case class Minus(lhs: Expression, rhs: Expression) extends Expression

object ExpressionEvaluator {
  def value(expression: Expression): Int = expression match {
    case Number(value) => value
    case Plus(lhs, rhs) => value(lhs) + value(rhs)
    case Minus(lhs, rhs) => value(lhs) - value(rhs)
  }
}

object ExpressionConverter extends JsonConverter[Expression] {
  override def convertToJson(value: Expression): JsonValue = value match {
    case Number(nValue) => JsonNumber(nValue)
    case Plus(lhs, rhs) => JsonObject(Map(
      "op" -> JsonString("+"),
      "lhs" -> convertToJson(lhs),
      "rhs" -> convertToJson(rhs)
    ))
    case Minus(lhs, rhs) => JsonObject(Map(
      "op" -> JsonString("-"),
      "lhs" -> convertToJson(lhs),
      "rhs" -> convertToJson(rhs)
    ))
  }
}


