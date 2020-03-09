package scalagen
package visitors

class PrintVisitor extends Visitor {

  override def visit(t: Type.Name): Unit = print(t.value)

  override def visit(t: Type.Struct): Unit = {
    print("{ ")
    t.fields.foreach { field =>
      field.accept(this)
      print(", ")
    }
    print(" }")
  }

  override def visit(t: Type.Intrinsic.Boolean): Unit = print("Boolean")
  override def visit(t: Type.Intrinsic.String): Unit  = print("String")
  override def visit(t: Type.Intrinsic.Int): Unit     = print("Int")
  override def visit(t: Type.Intrinsic.Float): Unit   = print("Float")

  override def visit(t: Defn.Type): Unit = {
    print("type ")
    t.name.accept(this)
    print(" = ")
    t.typ.accept(this)
    println()
  }

  override def visit(t: Term.Name): Unit = print(t.value)

  override def visit(t: Term.Field): Unit = {
    t.name.accept(this)
    print(": ")
    t.typ.accept(this)
  }
}
