package scalagen.gen.scalameta

import scala.meta.{Stat, Type}

sealed abstract class ScalametaTemplate {
  def stat: Stat
  final def asScalaCode: String = stat.toString
}

object ScalametaTemplate {
  type Field = (String, Type)

  final case class CaseClass(name: String, ctorFields: Seq[Field]) extends ScalametaTemplate {
    override def stat: Stat = ???
  }
}
