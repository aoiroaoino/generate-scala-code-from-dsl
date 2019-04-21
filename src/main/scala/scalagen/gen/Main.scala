package scalagen.gen

import scalagen.dsl.{NamedType, Type}

object Main {

  // type User = {
  //   name: String
  //   age: Int
  //   address: {
  //     postcode: String,
  //     city: String
  //   }
  //   isActive: Boolean
  // }
  val sample: NamedType =
    NamedType(
      "User",
      Type.Struct(
        Seq(
          "name" -> Type.String,
          "age"  -> Type.Int,
          "address" -> Type.Struct(
            Seq(
              "postcode" -> Type.String,
              "city"     -> Type.String
            )
          ),
          "isActive" -> Type.Boolean
        )
      )
    )

  def main(args: Array[String]): Unit = {
//    val converter: Converter = new ConverterImplByStringTemplate()
    val converter: Converter = new ConverterImplByScalametaTemplate()

    converter.convert(sample)
  }
}
