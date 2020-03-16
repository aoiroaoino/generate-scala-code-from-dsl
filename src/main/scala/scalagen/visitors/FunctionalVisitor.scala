package scalagen
package visitors

abstract class FunctionalVisitor[A] {
  // Type
  def visit(t: Type.Name): A
  def visit(t: Type.Struct): A
  def visit(t: Type.Intrinsic.Boolean): A
  def visit(t: Type.Intrinsic.String): A
  def visit(t: Type.Intrinsic.Int): A
  def visit(t: Type.Intrinsic.Float): A
  // Defn
  def visit(t: Defn.Type): A
  // Term
  def visit(t: Term.Name): A
  def visit(t: Term.Field): A
}

object FunctionalVisitor {
  trait Accepter {
    def accept[A](visitor: FunctionalVisitor[A]): A
  }
}
