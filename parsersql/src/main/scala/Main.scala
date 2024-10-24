package parsersql

import fastparse._
import NoWhitespace._

@main def runParser(args: String*): Unit = {
  def number(using ctx: P[Any]): P[Int] = P(CharsWhileIn("0-9").!.map(_.toInt))

  def expr(using ctx: P[Any]): P[Int] = P(term ~ End)

  def term(using ctx: P[Any]): P[Int] = P(factor ~ ((CharIn("+\\-").! ~/ factor).rep)).map {
    case (base, ops) =>
      ops.foldLeft(base) {
        case (left, ("+", right)) => left + right
        case (left, ("-", right)) => left - right
      }
  }

  def factor(using ctx: P[Any]): P[Int] = P(number | "(" ~/ expr ~ ")")

  val input = if (args.nonEmpty) args.mkString else "1+2+3"
  parse(input, p => expr(using p)) match {
    case Parsed.Success(value, _) =>
      println(s"Résultat : $value")
    case f: Parsed.Failure =>
      println(s"Échec du parsing : ${f.msg}")
  }
}
