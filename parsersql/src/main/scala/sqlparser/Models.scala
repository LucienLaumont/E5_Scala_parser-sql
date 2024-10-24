package sqlparser

sealed trait SqlParsingError {
  def message: String
}

case class SyntaxError(message: String) extends SqlParsingError

sealed trait ExecutionPlan

case class SelectPlan(
  fields: Seq[String],
  table: String,
  where: Option[Expression] = None
) extends ExecutionPlan

sealed trait Expression

case class Equals(field: String, value: String) extends Expression
