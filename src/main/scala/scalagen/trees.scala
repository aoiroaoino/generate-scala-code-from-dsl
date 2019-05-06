package scalagen

sealed trait Tree extends Product with Serializable

final case class NamedType(name: String, tpe: Type) extends Tree

sealed trait Type extends Tree

sealed trait Intrinsic extends Type
object Intrinsic {
  final case class Boolean() extends Intrinsic
  final case class Int()     extends Intrinsic
  final case class String()  extends Intrinsic
}

final case class Struct(fields: List[Struct.Field]) extends Type
object Struct {
  type Field = (String, Type)
  def apply(fields: Struct.Field*): Struct = new Struct(fields.toList)
}

final case class TypeRef(name: String) extends Type
