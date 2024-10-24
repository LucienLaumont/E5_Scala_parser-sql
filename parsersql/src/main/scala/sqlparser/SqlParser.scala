
package sqlparser

trait SqlParser {
  def parse(sqlString: String): Either[SqlParsingError, ExecutionPlan]
}
