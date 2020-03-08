package scalagen.visitors

import scalagen.{Defn, Term, Tree, Type}

abstract class Visitor {

  def accept(tree: Tree): Unit = tree match {
    case t: Type.Name              => visit(t)
    case t: Type.Struct            => visit(t)
    case t: Type.Intrinsic.Boolean => visit(t)
    case t: Type.Intrinsic.String  => visit(t)
    case t: Type.Intrinsic.Int     => visit(t)
    case t: Type.Intrinsic.Float   => visit(t)
    case t: Defn.Type              => visit(t)
    case t: Term.Name              => visit(t)
    case t: Term.Field             => visit(t)
    case t                         => sys.error(s"unexpected error. tree: $t")
  }

  // Type
  def visit(t: Type.Name): Unit
  def visit(t: Type.Struct): Unit
  def visit(t: Type.Intrinsic.Boolean): Unit
  def visit(t: Type.Intrinsic.String): Unit
  def visit(t: Type.Intrinsic.Int): Unit
  def visit(t: Type.Intrinsic.Float): Unit
  // Defn
  def visit(t: Defn.Type): Unit
  // Term
  def visit(t: Term.Name): Unit
  def visit(t: Term.Field): Unit
}
