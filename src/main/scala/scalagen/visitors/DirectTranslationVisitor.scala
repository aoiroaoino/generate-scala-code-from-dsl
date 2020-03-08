package scalagen.visitors
import scalagen.{Defn, Term, Type}
import scalagen.Type.Intrinsic

import scala.util.chaining._

// 匿名の Struct から field 名を用いて Defn.Type(...) として展開したいが、
// 走査する順序と文字列構築の流れが蜜結合している為、うまく展開できない。
// 複数の buffer を用意したり、対応するモデルを用意することで回避可能であるが、
// DSL に機能が追加され、展開ルールが増えるごとに複雑度は増してくる。
class DirectTranslationVisitor extends Visitor {
  private[this] val buf = new StringBuffer()

  def result: String = buf.toString.tap(_ => buf.setLength(0))

  override def visit(t: Type.Name): Unit = buf.append(t.value)

  // Primary Constructor とみなして展開する
  override def visit(t: Type.Struct): Unit = {
    buf.append("(")
    val len = t.fields.length
    val a   = t.fields.toArray
    for (i <- t.fields.indices) {
      accept(a(i))
      if (i != len - 1) {
        buf.append(", ")
      }
    }
    buf.append(")")
  }

  override def visit(t: Intrinsic.Boolean): Unit = buf.append("Boolean")
  override def visit(t: Intrinsic.String): Unit  = buf.append("String")
  override def visit(t: Intrinsic.Int): Unit     = buf.append("Int")
  override def visit(t: Intrinsic.Float): Unit   = buf.append("Float")

  override def visit(t: Defn.Type): Unit = {
    buf.append("final case class ")
    accept(t.name)
    // 組み込み型(Type.Intrinsic)や型名(Type.Name)だった場合は型エイリアスに展開したいが、
    // Type が Struct かそれ以外かで分岐させる必要が出てくる。
    accept(t.typ)
    buf.append("\n")
  }

  override def visit(t: Term.Name): Unit = buf.append(t.value)

  override def visit(t: Term.Field): Unit = {
    accept(t.name)
    buf.append(": ")
    accept(t.typ) // Defn.Type と同様で、ネストした Struct の展開が難しい。
  }
}
