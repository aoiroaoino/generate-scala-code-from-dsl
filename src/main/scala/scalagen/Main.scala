package scalagen

object Main {

  // type AccountID = Int
  // type Account = {
  //   id: AccountID,
  //   name: String
  // }
  val sample1: Seq[NamedType] = Seq(
    NamedType("AccountID", Intrinsic.Int()),
    NamedType(
      "Account",
      Struct(
        "id"   -> TypeRef("AccountID"),
        "name" -> Intrinsic.String()
      )
    )
  )

  // type User = {
  //   name: String,
  //   age: Int,
  //   address: {
  //     postcode: String,
  //     city: String
  //   },
  //   isActive: Boolean
  // }
  val sample2: Seq[NamedType] = Seq(
    NamedType(
      "User",
      Struct(
        "name" -> Intrinsic.String(),
        "age"  -> Intrinsic.Int(),
        "address" -> Struct(
          "postcode" -> Intrinsic.String(),
          "city"     -> Intrinsic.String()
        ),
        "isActive" -> Intrinsic.Boolean()
      )
    )
  )

  val sample3 = sample1 ++ sample2

  def main(args: Array[String]): Unit = {

    {
      println("====== SyntaxDirectedTranslator")
      val v = new SyntaxDirectedVisitor()
      println("=== sample1:")
      sample1.foreach(v.visit)
      println("=== sample2:")
      sample2.foreach(v.visit)
    }

    {
      println("===== ModelDrivenTranslator")
      val v = new ModelDrivenTranslator()
      println("=== sample3:")
      sample3.foreach(v.visit)
      v.run().foreach(println)
    }
  }
}
