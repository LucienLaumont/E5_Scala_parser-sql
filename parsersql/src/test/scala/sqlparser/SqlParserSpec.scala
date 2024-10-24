package sqlparser

import munit.FunSuite

class SqlParserSpec extends FunSuite {

  test("Parse simple SELECT * FROM table") {
    val sql = "SELECT * FROM users"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users")))
  }

  test("Parse SELECT fields FROM table") {
    val sql = "SELECT id, name FROM users"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("id", "name"), "users")))
  }
}
