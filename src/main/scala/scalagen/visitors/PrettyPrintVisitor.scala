package scalagen
package visitors

import scala.util.chaining._

class PrettyPrintVisitor(indentSpaceSize: Int) extends Visitor {

  private[this] var indentDepth = 0
  private def indent(): String  = " " * indentSpaceSize * indentDepth

  private[this] val buf: StringBuffer = new StringBuffer()

  def result: String = buf.toString.tap { _ =>
    indentDepth = 0
    buf.setLength(0)
  }

  override def visit(t: Type.Name): Unit = buf.append(t.value)

  override def visit(t: Type.Struct): Unit = {
    buf.append("{\n")
    indentDepth += 1
    val len = t.fields.length
    val a   = t.fields.toArray
    for (i <- t.fields.indices) {
      buf.append(indent())
      accept(a(i))
      if (i != len - 1) {
        buf.append(",")
      }
      buf.append("\n")
    }
    indentDepth -= 1
    buf.append(indent())
    buf.append("}")
  }

  override def visit(t: Type.Intrinsic.Boolean): Unit = buf.append("Boolean")
  override def visit(t: Type.Intrinsic.String): Unit  = buf.append("String")
  override def visit(t: Type.Intrinsic.Int): Unit     = buf.append("Int")
  override def visit(t: Type.Intrinsic.Float): Unit   = buf.append("Float")

  override def visit(t: Defn.Type): Unit = {
    buf.append("type ")
    accept(t.name)
    buf.append(" = ")
    accept(t.typ)
    buf.append("\n")
  }

  override def visit(t: Term.Name): Unit = buf.append(t.value)

  override def visit(t: Term.Field): Unit = {
    accept(t.name)
    buf.append(": ")
    accept(t.typ)
  }
}
