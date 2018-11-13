package noam.shaish.json

import org.scalatest.{FlatSpec, Matchers}

class JsonWriterTest extends FlatSpec with Matchers {
  behavior of "write"

  it should "write json null" in {
    JsonWriter.write(JsonNull) should be("null")
  }

  it should "write json number 10" in {
    JsonWriter.write(JsonNumber(10)) should be("10")
  }

  it should "write json string \"hello\"" in {
    JsonWriter.write(JsonString("hello")) should be("\"hello\"")
  }

  it should "write json array [1, \"a\", null]" in {
    JsonWriter.write(JsonArray(Seq(JsonNumber(1), JsonString("a"), JsonNull))) should be("[1, \"a\", null]")
  }

  it should "write json object {name:\"Jon\", age:30, work:null, hobbies:[\"soccer\"]}" in {
    JsonWriter.write(JsonObject(Map(
      "name" -> JsonString("Jon"),
      "age" -> JsonNumber(30),
      "work" -> JsonNull,
      "hobbies" -> JsonArray(Seq(JsonString("soccer")))
    ))) should be("{name:\"Jon\", age:30, work:null, hobbies:[\"soccer\"]}")
  }
}
