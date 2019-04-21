package scalagen.gen

package object string {
  import ScalaReservedWord._

  def escape(s: String): String = if (isReservedWord(s)) s"`$s`" else s
}
