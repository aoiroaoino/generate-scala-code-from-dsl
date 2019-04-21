package scalagen.dsl

final case class NamedType(name: String, tpe: Type)

sealed abstract class Type

object Type {

  final case class Ref(tpe: Type) extends Type

  type Field = (String, Type)
  final case class Struct(fields: Seq[Field]) extends Type

  sealed trait Intrinsic
  case object Boolean extends Type with Intrinsic
  case object Int     extends Type with Intrinsic
  case object String  extends Type with Intrinsic
}
