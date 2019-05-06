package scalagen

abstract class Visitor {

  def visit(node: Tree): Unit = node match {
    case t: NamedType => visit(t)
    case t: TypeRef   => visit(t)
    case t: Struct    => visit(t)
    case t: Intrinsic => visit(t)
  }

  protected def visit(t: NamedType): Unit
  protected def visit(t: Intrinsic): Unit
  protected def visit(t: Struct): Unit
  protected def visit(t: TypeRef): Unit
}

// === impls

class PrintlnVisitor() extends Visitor {
  protected override def visit(t: NamedType): Unit = println(t)
  protected override def visit(t: Intrinsic): Unit = println(t)
  protected override def visit(t: Struct): Unit    = println(t)
  protected override def visit(t: TypeRef): Unit   = println(t)
}

class PrettyPrintVisitor() extends Visitor {
  private final val Indent = "  "
  private[this] var depth  = 0
  private[this] val buffer = new StringBuffer()

  override def visit(node: Tree): Unit = {
    buffer.setLength(0)
    super.visit(node)
    print(buffer.toString)
  }

  protected override def visit(t: NamedType): Unit = {
    buffer.append(s"type ${t.name} = ")
    super.visit(t.tpe)
    buffer.append("\n")
  }

  protected override def visit(t: Intrinsic): Unit = t match {
    case Intrinsic.Boolean() => buffer.append("Boolean")
    case Intrinsic.Int()     => buffer.append("Int")
    case Intrinsic.String()  => buffer.append("String")
  }

  protected override def visit(t: Struct): Unit = {
    var first = true
    buffer.append("{")
    buffer.append("\n")
    depth += 1
    for ((name, tpe) <- t.fields) {
      if (first) { first = false } else buffer.append(",\n")
      buffer.append(Indent * depth)
      buffer.append(s"$name: ")
      super.visit(tpe)
    }
    depth -= 1
    buffer.append("\n")
    buffer.append(Indent * depth)
    buffer.append("}")
  }

  protected override def visit(t: TypeRef): Unit = buffer.append(t.name)
}

// 匿名の Struct から field 名を用いてトップレベルで NamedType として展開させたいが、
// 走査の流れと文字列構築の流れが結合している為、うまく展開できない。
// 入力構文の走査の流れと文字列への変換の流れが結合してしまっている為、
// 入れ子構造の展開が難しかったり、順序の並び替えやグルーピングに複数回の走査が必要になったりする。
class SyntaxDirectedVisitor extends Visitor {
  private final val Indent = "  "
  private[this] val buffer = new StringBuffer()

  override def visit(node: Tree): Unit = {
    buffer.setLength(0)
    super.visit(node)
    print(buffer.toString)
  }

  protected override def visit(t: NamedType): Unit = t match {
    case NamedType(name, tpe: Struct) =>
      buffer.append(s"final case class $name")
      if (tpe.fields.isEmpty) buffer.append("()") else super.visit(tpe: Type)
      buffer.append("\n")
    case NamedType(name, tpe) =>
      buffer.append(s"type $name = ")
      super.visit(tpe)
      buffer.append("\n")
  }

  protected override def visit(t: Intrinsic): Unit = t match {
    case Intrinsic.Boolean() => buffer.append("Boolean")
    case Intrinsic.Int()     => buffer.append("Int")
    case Intrinsic.String()  => buffer.append("String")
  }

  // ネストした Struct の展開がうまくできない。
  protected override def visit(t: Struct): Unit = {
    var first = true
    buffer.append("(\n")
    for ((name, tpe) <- t.fields) {
      if (first) { first = false } else buffer.append(",\n")
      buffer.append(Indent)
      buffer.append(s"$name: ")
      super.visit(tpe)
    }
    buffer.append("\n)")
  }

  protected override def visit(t: TypeRef): Unit = buffer.append(t.name)
}

final class ScalaFile() {
  import collection.mutable

  private[this] var rootPackage = ""
  private[this] val typeAliases = mutable.ArrayBuffer.empty[String]
  private[this] val classes     = mutable.ArrayBuffer.empty[String]

  def setRootPackage(s: String): Unit = rootPackage = s

  def addTypeAlias(s: String): Unit = typeAliases += s
  def addClass(s: String): Unit     = classes += s

  def asString(): String = {
    val b = new StringBuilder()
    b append rootPackage
    b append "\n"
    typeAliases.toList.foreach(s => b.append(s + "\n"))
    b append "\n"
    classes.toList.foreach(s => b.append(s + "\n"))
    b.toString()
  }

  def clear(): Unit = {
    rootPackage = ""
    typeAliases.clear()
    classes.clear()
  }
}

class ModelDrivenTranslator {
  private val file = new ScalaFile()
  file.setRootPackage("package com.example\n")

  def visit(node: Tree): String = node match {
    case t: NamedType => visit(t)
    case t: TypeRef   => visit(t)
    case t: Struct    => visit(t)
    case t: Intrinsic => visit(t)
  }

  def run(): String = {
    val s = file.asString()
    file.clear()
    s
  }

  protected def visit(t: NamedType): String = t match {
    case NamedType(name, tpe: Struct) =>
      val caseClass = s"final case class $name${this.visit(tpe)}"
      file.addClass(caseClass)
      caseClass
    case NamedType(name, tpe) =>
      val typeAlias = s"type $name = ${this.visit(tpe)}"
      file.addTypeAlias(typeAlias)
      typeAlias
  }
  protected def visit(t: Intrinsic): String = t match {
    case Intrinsic.Boolean() => "Boolean"
    case Intrinsic.Int()     => "Int"
    case Intrinsic.String()  => "String"
  }

  protected def visit(t: Struct): String =
    if (t.fields.isEmpty) "()"
    else
      t.fields
        .map {
          case (name, tpe: Struct) =>
            val typeName = name.capitalize
            this.visit(NamedType(typeName, tpe))
            s"  $name: $typeName"
          case (name, tpe) =>
            s"  $name: ${this.visit(tpe)}"
        }
        .mkString("(\n", ",\n", "\n)")

  protected def visit(t: TypeRef): String = t.name
}
