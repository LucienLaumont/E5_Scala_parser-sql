package sqlparser

sealed trait SqlParsingError {
  def message: String
}

case class SyntaxError(message: String) extends SqlParsingError

sealed trait ExecutionPlan

case class SelectPlan(
  fields: Seq[String],
  table: String,
  where: Option[Expression] = None,
  range: Option[Range] = None
) extends ExecutionPlan

case class Range(start: Int, count: Int)

sealed trait Expression

// Expressions de comparaison
case class Equals(field: String, value: String) extends Expression
case class NotEquals(field: String, value: String) extends Expression
case class GreaterThan(field: String, value: String) extends Expression
case class GreaterThanOrEqual(field: String, value: String) extends Expression
case class LessThan(field: String, value: String) extends Expression
case class LessThanOrEqual(field: String, value: String) extends Expression

// Expressions logiques
case class And(left: Expression, right: Expression) extends Expression
case class Or(left: Expression, right: Expression) extends Expression