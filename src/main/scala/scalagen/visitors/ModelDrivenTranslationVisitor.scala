package scalagen
package visitors

import scalagen.util.ScalaReservedWord.escape

import scala.collection.mutable.ArrayBuffer
import scala.util.chaining._

class ModelDrivenTranslationVisitor extends FunctionalVisitor[String] {
  import ModelDrivenTranslationVisitor._

  private[this] val buf = new ScalaSource

  def result: String = buf.asString.tap(_ => buf.clear())

  override def visit(t: Type.Name): String   = t.value
  override def visit(t: Type.Struct): String = t.fields.map(_.accept(this)).mkString(", ")

  override def visit(t: Type.Intrinsic.Boolean): String = "Boolean"
  override def visit(t: Type.Intrinsic.String): String  = "String"
  override def visit(t: Type.Intrinsic.Int): String     = "Int"
  override def visit(t: Type.Intrinsic.Float): String   = "Float"

  override def visit(t: Defn.Type): String = {
    t.typ match {
      case ts: Type.Struct =>
        val cc = ScalaSource.CaseClass(t.name.value, ts.accept(this))
        buf.addCaseClass(cc)
        cc.asString
      case _ =>
        val ta = ScalaSource.TypeAlias(t.name.value, t.typ.accept(this))
        buf.addTypeAlias(ta)
        ta.asString
    }
  }

  override def visit(t: Term.Name): String = escape(t.value)

  override def visit(t: Term.Field): String = {
    val typeStr = t.typ match {
      case ts: Type.Struct =>
        val typeName = t.name.accept(this).capitalize
        // ネストした Struct を case class として追加する
        ScalaSource.CaseClass(typeName, ts.accept(this)).tap(buf.addCaseClass)
        typeName
      case _ =>
        t.typ.accept(this)
    }
    s"${t.name.accept(this)}: $typeStr"
  }
}

object ModelDrivenTranslationVisitor {

  final class ScalaSource {
    import ScalaSource._

    private[this] val caseClasses = ArrayBuffer.empty[CaseClass]
    private[this] val typeAliases = ArrayBuffer.empty[TypeAlias]

    def addTypeAlias(typeAlias: TypeAlias): Unit = typeAliases += typeAlias
    def addCaseClass(caseClass: CaseClass): Unit = caseClasses += caseClass

    def asString: String =
      typeAliases.map(_.asString).mkString("\n") + "\n" + caseClasses.map(_.asString).mkString("\n")

    def clear(): Unit = {
      typeAliases.clear()
      caseClasses.clear()
    }
  }
  object ScalaSource {
    final case class CaseClass(name: String, ctorFields: String) {
      require(ctorFields.nonEmpty)
      def asString: String = {
        s"final case class $name($ctorFields)"
      }
    }
    final case class TypeAlias(name: String, typ: String) {
      def asString: String = s"type $name = $typ"
    }
  }
}
