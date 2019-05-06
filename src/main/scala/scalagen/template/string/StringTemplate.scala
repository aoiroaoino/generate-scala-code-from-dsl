package scalagen.template.string

sealed abstract class StringTemplate {
  def toScalaCode: String
}

object StringTemplate {

  type Field = (String, String)

  final case class CaseClass(name: String, ctorFields: Seq[Field]) extends StringTemplate {
    override def toScalaCode: String = {
      val fields = ctorFields.map { case (n, t) => s"${escape(n)}: $t" }.mkString(", ")
      s"final case class ${escape(name)}($fields)"
    }
  }
}
