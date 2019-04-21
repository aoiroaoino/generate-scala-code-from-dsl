package scalagen.gen

import scalagen.dsl.{NamedType, Type}

abstract class Converter {
  def convert(namedTypes: Seq[NamedType]): String
}

class ConverterImplByStringTemplate() extends Converter {

  private[gen] def convertSimpleType: PartialFunction[Type, String] = {
    case Type.Int => "Int"
    case Type.
  }

  override def convert(namedTypes: Seq[NamedType]): String = ???
}

class ConverterImplByScalametaTemplate() extends Converter {
  override def convert(namedTypes: Seq[NamedType]): String = ???
}
