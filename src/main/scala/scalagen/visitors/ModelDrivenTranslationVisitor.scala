package scalagen.visitors

class ModelDrivenTranslationVisitor {}

//
//final class ScalaFile() {
//  import collection.mutable
//
//  private[this] var rootPackage = ""
//  private[this] val typeAliases = mutable.ArrayBuffer.empty[String]
//  private[this] val classes     = mutable.ArrayBuffer.empty[String]
//
//  def setRootPackage(s: String): Unit = rootPackage = s
//
//  def addTypeAlias(s: String): Unit = typeAliases += s
//  def addClass(s: String): Unit     = classes += s
//
//  def asString(): String = {
//    val b = new StringBuilder()
//    b append rootPackage
//    b append "\n"
//    typeAliases.toList.foreach(s => b.append(s + "\n"))
//    b append "\n"
//    classes.toList.foreach(s => b.append(s + "\n"))
//    b.toString()
//  }
//
//  def clear(): Unit = {
//    rootPackage = ""
//    typeAliases.clear()
//    classes.clear()
//  }
//}

//class ModelDrivenTranslator {
//  private val modelFile = new ScalaFile()
//  modelFile.setRootPackage("package com.example.foo")
//  private val packageFile = new ScalaFile()
//  packageFile.setRootPackage("package com.example")
//
//  def visit(node: Tree): String = node match {
//    case t: NamedType => visit(t)
//    case t: TypeRef   => visit(t)
//    case t: Struct    => visit(t)
//    case t: Intrinsic => visit(t)
//  }
//
//  def run(): List[String] = {
//    val s = modelFile.asString()
//    val t = packageFile.asString()
//    modelFile.clear()
//    packageFile.clear()
//    List(s, t)
//  }
//
//  protected def visit(t: NamedType): String = t match {
//    case NamedType(name, tpe: Struct) =>
//      val caseClass = s"final case class $name${this.visit(tpe)}"
//      modelFile.addClass(caseClass)
//      caseClass
//    case NamedType(name, tpe) =>
//      val typeAlias = s"type $name = ${this.visit(tpe)}"
//      packageFile.addTypeAlias(typeAlias)
//      typeAlias
//  }
//  protected def visit(t: Intrinsic): String = t match {
//    case Intrinsic.Boolean() => "Boolean"
//    case Intrinsic.Int()     => "Int"
//    case Intrinsic.String()  => "String"
//  }
//
//  protected def visit(t: Struct): String =
//    if (t.fields.isEmpty) "()"
//    else
//      t.fields
//        .map {
//          case (name, tpe: Struct) =>
//            val typeName = name.capitalize
//            this.visit(NamedType(typeName, tpe))
//            s"  $name: $typeName"
//          case (name, tpe) =>
//            s"  $name: ${this.visit(tpe)}"
//        }
//        .mkString("(\n", ",\n", "\n)")
//
//  protected def visit(t: TypeRef): String = t.name
//}
