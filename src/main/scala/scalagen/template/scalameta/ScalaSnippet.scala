package scalagen.template.scalameta

sealed abstract class ScalaSnippet {
  def asScalaCode: String
}

object ScalaSnippet {}
