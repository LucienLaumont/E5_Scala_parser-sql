package sqlparser

@main def runParser(args: String*): Unit = {
  val sql = args.mkString(" ")
  val result = SqlParserImpl.parse(sql)
  result match {
    case Right(plan) =>
      println(s"Execution Plan: $plan")
    case Left(error) =>
      println(s"Error: ${error.message}")
  }
}
