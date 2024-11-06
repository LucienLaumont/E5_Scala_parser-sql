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
    KW("SELECT") ~ fields ~ KW("FROM") ~ identifier ~ where.? ~ range.? ~ End
  ).map {
    case (fields, table, whereClause, rangeClause) =>
      SelectPlan(fields, table, whereClause, rangeClause)
  }
  private def fields(using P[Any]): P[Seq[String]] = P(
    ("*".!).map((_: String) => Seq("*")) | fieldList
  )

  private def fieldList(using P[Any]): P[Seq[String]] = P(
    identifier.rep(1, sep = ","./)
  )

  private def where(using P[Any]): P[Expression] = P(
    KW("WHERE") ~ condition
  )

  private def condition(using P[Any]): P[Expression] = P(
    comparisonCondition ~ (logicalOperator ~ comparisonCondition).rep
  ).map {
    case (first, rest) =>
      rest.foldLeft(first) {
        case (left, ("AND", right)) => And(left, right)
        case (left, ("OR", right))  => Or(left, right)
      }
  }

  private def range(using P[Any]): P[Range] = P(
    KW("RANGE") ~ intValue ~ "," ~ intValue
  ).map {
    case (start, count) => Range(start, count)
  }

  private def intValue(using P[Any]): P[Int] = P(
    CharsWhileIn("0-9").!.map(_.toInt)
  )
  private def comparisonCondition(using P[Any]): P[Expression] = P(
    identifier ~ comparisonOperator ~ value
  ).map {
    case (field, ">=", value) => GreaterThanOrEqual(field, value)
    case (field, ">", value)  => GreaterThan(field, value)
    case (field, "<=", value) => LessThanOrEqual(field, value)
    case (field, "<", value)  => LessThan(field, value)
    case (field, "=", value)  => Equals(field, value)
    case (field, "!=", value) => NotEquals(field, value)
  }

  private def comparisonOperator(using P[Any]): P[String] = P(
    ">=".! | "<=".! | ">".! | "<".! | "=".! | "!=".!
  )

  private def logicalOperator(using P[Any]): P[String] = P(
    KW("AND").map(_ => "AND") | KW("OR").map(_ => "OR")
  )

  private def identifier(using P[Any]): P[String] = P(
    CharsWhileIn("a-zA-Z0-9_").!
  )

  private def value(using P[Any]): P[String] = P(
    "'" ~ CharsWhileIn("a-zA-Z0-9_").! ~ "'" |
    CharsWhileIn("a-zA-Z0-9_").!
  )

  private def KW(str: String)(using P[Any]): P[Unit] = P(IgnoreCase(str))

  private def limit(using P[Any]): P[Int] = P(
    KW("LIMIT") ~ CharsWhileIn("0-9").!.map(_.toInt)
  )

}


