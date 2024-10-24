package sqlparser

import fastparse._
import fastparse.ScalaWhitespace._

object SqlParserImpl extends SqlParser {

  def parse(sqlString: String): Either[SqlParsingError, ExecutionPlan] = {
    fastparse.parse(sqlString, p => selectStmt(using p)) match {
      case Parsed.Success(value, _) =>
        Right(value)
      case f: Parsed.Failure =>
        Left(SyntaxError(f.trace().longMsg))
    }
  }

  private def selectStmt(using P[Any]): P[SelectPlan] = P(
    KW("SELECT") ~ fields ~ KW("FROM") ~ identifier ~ End
  ).map {
    case (fields, table) =>
      SelectPlan(fields, table)
  }

  private def fields(using P[Any]): P[Seq[String]] = P(
    ("*".!).map((_: String) => Seq("*")) | fieldList
  )

  private def fieldList(using P[Any]): P[Seq[String]] = P(
    identifier.rep(1, sep = ","./)
  )

  private def identifier(using P[Any]): P[String] = P(
    CharsWhileIn("a-zA-Z0-9_").!
  )

  private def KW(str: String)(using P[Any]): P[Unit] = P(IgnoreCase(str))
}
