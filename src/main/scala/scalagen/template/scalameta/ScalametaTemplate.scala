package scalagen.template.scalameta

import scala.meta._

sealed abstract class ScalametaTemplate {
  def stat: Stat
  final def asScalaCode: String = stat.toString
}

object ScalametaTemplate {
  type Field = (String, Type.Name)

  final case class CaseClass(name: String, ctorFields: Seq[Field]) extends ScalametaTemplate {
    override def stat: Stat = Defn.Class(
      List(Mod.Final(), Mod.Case()),
      Type.Name(name),
      Nil,
      Ctor.Primary(
        Nil,
        Name.Anonymous(),
        List(ctorFields.map { case (n, t) => Term.Param(Nil, Term.Name(n), Some(t), None) }.toList)
      ),
      Template(Nil, Nil, Self(Name.Anonymous(), None), Nil)
    )
  }
}
