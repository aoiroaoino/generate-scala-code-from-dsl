package scalagen

import scalagen.visitors.{FunctionalVisitor, Visitor}

sealed trait Tree extends Visitor.Accepter with FunctionalVisitor.Accepter with Product with Serializable {
  def children: List[Tree]
}

sealed trait Type extends Tree

object Type {

  final case class Name(value: String) extends Type {
    override def children: List[Tree] = Nil

    override def accept(visitor: Visitor): Unit              = visitor.visit(this)
    override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
  }

  final case class Struct(fields: List[Term.Field]) extends Type {
    require(fields.nonEmpty)
    override def children: List[Tree] = fields

    override def accept(visitor: Visitor): Unit              = visitor.visit(this)
    override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
  }
  object Struct {
    def apply(fields: Term.Field*): Struct = new Struct(fields.toList)
  }

  trait Intrinsic extends Type {
    override def children: List[Tree] = Nil
  }
  object Intrinsic {
    final case class Boolean() extends Intrinsic {
      override def accept(visitor: Visitor): Unit              = visitor.visit(this)
      override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
    }
    final case class String() extends Intrinsic {
      override def accept(visitor: Visitor): Unit              = visitor.visit(this)
      override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
    }
    final case class Int() extends Intrinsic {
      override def accept(visitor: Visitor): Unit              = visitor.visit(this)
      override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
    }
    final case class Float() extends Intrinsic {
      override def accept(visitor: Visitor): Unit              = visitor.visit(this)
      override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
    }
  }
  def Boolean: Intrinsic.Boolean = Intrinsic.Boolean()
  def String: Intrinsic.String   = Intrinsic.String()
  def Int: Intrinsic.Int         = Intrinsic.Int()
  def Float: Intrinsic.Float     = Intrinsic.Float()
}

sealed trait Defn extends Tree

object Defn {
  final case class Type(name: scalagen.Type.Name, typ: scalagen.Type) extends Defn {
    override def children: List[Tree] = List(name, typ)

    override def accept(visitor: Visitor): Unit              = visitor.visit(this)
    override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
  }
}

sealed abstract class Term extends Tree

object Term {
  final case class Name(value: String) extends Term {
    override def children: List[Tree] = Nil

    override def accept(visitor: Visitor): Unit              = visitor.visit(this)
    override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
  }
  final case class Field(name: Term.Name, typ: Type) extends Term {
    override def children: List[Tree] = List(name, typ)

    override def accept(visitor: Visitor): Unit              = visitor.visit(this)
    override def accept[A](visitor: FunctionalVisitor[A]): A = visitor.visit(this)
  }
}
