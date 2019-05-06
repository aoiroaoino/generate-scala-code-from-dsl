package scalagen.template

package object string {
  import ScalaReservedWord._

  def escape(s: String): String = if (isReservedWord(s)) s"`$s`" else s
}
