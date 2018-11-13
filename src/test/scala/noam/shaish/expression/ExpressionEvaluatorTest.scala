package noam.shaish.expression

import org.scalatest.{FlatSpec, Matchers}

class ExpressionEvaluatorTest extends FlatSpec with Matchers {
  behavior of "value"

  it should "evaluate the number 1" in {
    ExpressionEvaluator.value(Number(1)) should be(1)
  }

  it should "evaluate 1 + 1" in {
    ExpressionEvaluator.value(Plus(Number(1), Number(1))) should be(2)
  }

  it should "evaluate 1 - 1" in {
    ExpressionEvaluator.value(Minus(Number(1), Number(1))) should be(0)
  }

  it should "evaluate (2 - 1) + (1 - (2 + 1)" in {
    ExpressionEvaluator
      .value(Plus(Minus(Number(2), Number(1)), Minus(Number(1), Plus(Number(2), Number(1))))) should be(-1)
  }
}
