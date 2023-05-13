import scala.annotation.targetName

sealed trait Calculation
final case class Success(result: Double) extends Calculation
final case class Failure(reason: String) extends Calculation


sealed trait Expression {
  private def eval: Calculation = this match

    case Addition(left, right) => (left.eval, right.eval) match
      case (Success(result_left), Success(result_right)) => Success(result_left + result_right)
      case _ => Failure("Non success")

    case Subtraction(left, right) => (left.eval, right.eval) match
      case (Success(result_left), Success(result_right)) => Success(result_left - result_right)
      case _ => Failure("Non success")

    case Number(value) => Success(value)

    case Division(left, right) => (left.eval, right.eval) match
      case (Success(result_left), Success(result_right)) => divide(result_left, result_right) match
        case Finite(value) => Success(value)
        case Infinite => Failure("Infinite")
      case _ => Failure("Non success")
}
final case class Addition(left: Expression, right: Expression) extends Expression
final case class Subtraction(left: Expression, right: Expression) extends Expression
final case class Number(value: Double) extends Expression
final case class Division(left: Expression, right: Expression) extends Expression


object Calculator {
  @targetName("Sum")
  def +(calculation: Calculation, value: Double): Calculation = calculation match
    case Success(result) => Success(result = result + value)
    case Failure(reason) => Failure(reason = reason)

  @targetName("Rest")
  def -(calculation: Calculation, value: Double): Calculation = calculation match
    case Success(result) => Success(result = result - value)
    case Failure(reason) => Failure(reason = reason)

  @targetName("Division")
  def /(calculation: Calculation, value: Int): Calculation = calculation match
    case Success(result) => if value == 0 then Failure("Division by zero") else Success(result = result / value)
    case Failure(reason) => Failure(reason = reason)
}


def calculator_test(): Unit = {
  assert(Calculator.+(Success(1), 1) == Success(2))
  assert(Calculator.-(Success(1), 1) == Success(0))
  assert(Calculator.+(Failure("Badness"), 1) == Failure("Badness"))

  assert(Calculator./(Success(4), 2) == Success(2))
  assert(Calculator./(Success(4), 0) == Failure("Division by zero"))
  assert(Calculator./(Failure("Badness"), 0) == Failure("Badness"))
}