package scalagen
package visitors

import scala.collection.mutable.ArrayBuffer
import scala.util.chaining._

class CollectTermNameVisitor extends Visitor {
  private[this] val buf: ArrayBuffer[String] = ArrayBuffer.empty

  def result: List[String] = buf.toList.tap(_ => buf.clear())

  override def visit(t: Type.Name): Unit = ()

  override def visit(t: Type.Struct): Unit = t.fields.foreach(accept)

  override def visit(t: Type.Intrinsic.Boolean): Unit = ()
  override def visit(t: Type.Intrinsic.String): Unit  = ()
  override def visit(t: Type.Intrinsic.Int): Unit     = ()
  override def visit(t: Type.Intrinsic.Float): Unit   = ()

  override def visit(t: Defn.Type): Unit = accept(t.typ)

  override def visit(t: Term.Name): Unit = buf += t.value

  override def visit(t: Term.Field): Unit = { accept(t.name); accept(t.typ) }
}
