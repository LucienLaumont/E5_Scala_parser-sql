package sqlparser

import munit.FunSuite

class SqlParserSpec extends FunSuite {

  // Tests existants
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

  // Tests pour les comparaisons dans WHERE

  test("Parse SELECT with WHERE clause using Equals") {
    val sql = "SELECT * FROM users WHERE age = 30"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(Equals("age", "30")))))
  }

  test("Parse SELECT with string condition in WHERE clause") {
    val sql = "SELECT * FROM users WHERE status = 'active'"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(Equals("status", "active")))))
  }

  test("Parse SELECT with WHERE clause using NotEquals") {
    val sql = "SELECT * FROM users WHERE age != 30"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(NotEquals("age", "30")))))
  }

  test("Parse SELECT with WHERE clause using GreaterThan") {
    val sql = "SELECT * FROM users WHERE age > 30"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(GreaterThan("age", "30")))))
  }

  test("Parse SELECT with WHERE clause using GreaterThanOrEqual") {
    val sql = "SELECT * FROM users WHERE age >= 30"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(GreaterThanOrEqual("age", "30")))))
  }

  test("Parse SELECT with WHERE clause using LessThan") {
    val sql = "SELECT * FROM users WHERE age < 30"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(LessThan("age", "30")))))
  }

  test("Parse SELECT with WHERE clause using LessThanOrEqual") {
    val sql = "SELECT * FROM users WHERE age <= 30"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(LessThanOrEqual("age", "30")))))
  }

  // Test pour la combinaison de conditions

  test("Parse SELECT with complex WHERE clause using AND") {
    val sql = "SELECT * FROM users WHERE age >= 18 AND status = 'active'"
    val result = SqlParserImpl.parse(sql)
    assertEquals(
      result,
      Right(
        SelectPlan(
          Seq("*"),
          "users",
          Some(
            And(
              GreaterThanOrEqual("age", "18"),
              Equals("status", "active")
            )
          )
        )
      )
    )
  }

  test("Parse SELECT with complex WHERE clause using OR") {
    val sql = "SELECT * FROM users WHERE age < 18 OR status = 'inactive'"
    val result = SqlParserImpl.parse(sql)
    assertEquals(
      result,
      Right(
        SelectPlan(
          Seq("*"),
          "users",
          Some(
            Or(
              LessThan("age", "18"),
              Equals("status", "inactive")
            )
          )
        )
      )
    )
  }

  test("Parse SELECT with WHERE clause using AND and OR") {
    val sql = "SELECT * FROM users WHERE age > 18 AND status = 'active' OR status = 'inactive'"
    val result = SqlParserImpl.parse(sql)
    assertEquals(
      result,
      Right(
        SelectPlan(
          Seq("*"),
          "users",
          Some(
            Or(
              And(
                GreaterThan("age", "18"),
                Equals("status", "active")
              ),
              Equals("status", "inactive")
            )
          )
        )
      )
    )
  }

  test("Parse SELECT with WHERE clause using NOT") {
    val sql = "SELECT * FROM users WHERE NOT age > 18"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(Not(GreaterThan("age", "18"))))))
  }


  test("Parse SELECT with WHERE clause using NOT, AND, and OR") {
    val sql = "SELECT * FROM users WHERE NOT age > 18 AND status = 'active' OR status = 'inactive'"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(Or(And(Not(GreaterThan("age", "18")), Equals("status", "active")),Equals("status", "inactive"))))))
  }


  // Tests pour la clause RANGE

  test("Parse SELECT with RANGE clause") {
    val sql = "SELECT * FROM my_table RANGE 10, 50"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "my_table", None, None, Some(Range(10, 50)))))
  }

  test("Parse SELECT with WHERE and RANGE clauses") {
    val sql = "SELECT * FROM my_table WHERE age > 21 RANGE 5, 20"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "my_table", Some(GreaterThan("age", "21")), None, Some(Range(5, 20)))))
  }

  test("Parse SELECT with RANGE clause and multiple fields") {
    val sql = "SELECT id, name FROM my_table RANGE 0, 10"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("id", "name"), "my_table", None, None, Some(Range(0, 10)))))
  }

  test("Parse SELECT with LIMIT clause") {
    val sql = "SELECT * FROM users LIMIT 10"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", None, None, Some(Range(0, 10)))))
  }

  test("Parse SELECT with WHERE and LIMIT clauses") {
    val sql = "SELECT * FROM users WHERE age > 21 LIMIT 5"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", Some(GreaterThan("age", "21")), None, Some(Range(0, 5)))))
  }

  // test clause ORDER BY

  test("parse SELECT with ORDER BY in default (ascending) order") {
    val sql = "SELECT * FROM users ORDER BY age"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", None, Some(OrderBy("age", Ascending())), None)))
  }

  test("parse SELECT with ORDER BY in ascending order explicitly") {
    val sql = "SELECT * FROM users ORDER BY age ASC"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", None, Some(OrderBy("age", Ascending())), None)))
  }

  test("parse SELECT with ORDER BY in descending order") {
    val sql = "SELECT * FROM users ORDER BY age DESC"
    val result = SqlParserImpl.parse(sql)
    assertEquals(result, Right(SelectPlan(Seq("*"), "users", None, Some(OrderBy("age", Descending())), None)))
  }

  test("parse SELECT with WHERE and ORDER BY clause") {
    val sql = "SELECT * FROM users WHERE age > 30 ORDER BY name DESC LIMIT 10"
    val result = SqlParserImpl.parse(sql)
    assertEquals(
      result,
      Right(SelectPlan(Seq("*"), "users", Some(GreaterThan("age", "30")), Some(OrderBy("name", Descending())), Some(Range(0, 10))))
    )
  }

  // erreur de syntaxe
  test("Parse invalid SQL with missing FROM clause") {
    val sql = "SELECT * WHERE age > 30"
    val result = SqlParserImpl.parse(sql)
    assert(result.isLeft, "Expected parsing to fail for missing FROM clause")
  }

  test("Parse invalid SQL with unknown keyword") {
    val sql = "SELEC * FROM users"
    val result = SqlParserImpl.parse(sql)
    assert(result.isLeft, "Expected parsing to fail for incorrect SELECT keyword")
  }
  
}