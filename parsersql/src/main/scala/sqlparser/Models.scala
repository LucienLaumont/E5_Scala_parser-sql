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
  orderBy: Option[OrderBy] = None,
  range: Option[Range] = None
) extends ExecutionPlan

sealed trait Expression

// Expression de tri
case class OrderBy(column: String, direction: SortDirection = Ascending())
sealed trait SortDirection
case class Ascending() extends SortDirection
case class Descending() extends SortDirection

// Expression de range
case class Range(start: Int, count: Int)

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
case class Not(expr: Expression) extends Expression
