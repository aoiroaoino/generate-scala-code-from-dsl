package scalagen

import scalagen.Type.Intrinsic
import scalagen.transversers.{CheckIfNestedStructExists, ExtractNestedStruct, NestedStructToTypeName, Traverser}
import scalagen.visitors.Visitor

import scala.collection.mutable.ListBuffer

object GenerateScalaCode {
  import scalagen.util.ScalaReservedWord._

  /**
    * Tree(s) を Scala のソースコード(文字列)に変換する処理。
    * 一回の走査で Scala のソースコードに出力するのではなく複数回に分けて走査して展開ルールを適用していくことで、
    * 実装の複雑さを軽減しつつ機能の追加に強い(後続の処理を変更せずに済む)生成処理を実現することができる。
    * 当たり前だが、木を走査する回数が増えるほどパフォーマンス上のペナルティがあるので注意は必要。
    *
    * @param trees DSL をパースして得られた AST(s)
    * @return コンパイル可能(なはず)の Scala ソースコード(文字列)
    */
  def run(trees: List[Tree]): String = {
    // ネストした Struct から新たに型の定義を生成する
    val additionalDefnTypes = ExtractNestedStruct.run(trees)

    // 元の気に追加で型の定義を加え、ネストした Struct をフィールド名を capitalize した型名(型の参照)に置き換える
    val processedTrees = NestedStructToTypeName.run(trees ::: additionalDefnTypes)

    // Tree の中にネストした Struct が含まれていないか確認（ここまでの加工が十分テストされていれば不要）
    CheckIfNestedStructExists.run(processedTrees)

    // 展開しやすくなった木(Defn.Type) から case class や type alias を組み立てて出力
    defnTypeToScalaCodes(processedTrees).mkString("\n")
  }

  def run(tree: Tree): String = run(tree :: Nil)

  private def defnTypeToScalaCodes(trees: List[Tree]): List[String] = {
    val buf = new ListBuffer[String]
    new Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        case Defn.Type(name, Type.Struct(fields)) =>
          val ctor = fields.map(f => escape(f.name.value) + ": " + typeToScalaType(f.typ)).mkString("(", ", ", ")")
          buf += s"final case class ${escape(name.value)}$ctor"
        case Defn.Type(name, typ) => // Intrinsic or Type.Name
          buf += s"type ${escape(name.value)} = ${typeToScalaType(typ)}"
        case _ =>
          super.traverse(tree)
      }
    }.traverse(trees)
    buf.toList
  }

  // accept を呼ばなければそれ以上は探索しないので、ノードに対して一対一のような変換をする場合は Visitor の方が便利ではある
  private def typeToScalaType(tree: Tree): String = {
    val v = new Visitor {
      private[this] val buf = new StringBuffer()
      def result: String    = buf.toString

      override def visit(t: Type.Name): Unit         = buf.append(t.value)
      override def visit(t: Intrinsic.Boolean): Unit = buf.append("Boolean")
      override def visit(t: Intrinsic.String): Unit  = buf.append("String")
      override def visit(t: Intrinsic.Int): Unit     = buf.append("Int")
      override def visit(t: Intrinsic.Float): Unit   = buf.append("Float")

      override def visit(t: Type.Struct): Unit = err(t)
      override def visit(t: Defn.Type): Unit   = err(t)
      override def visit(t: Term.Name): Unit   = err(t)
      override def visit(t: Term.Field): Unit  = err(t)

      private def err(t: Tree) = sys.error(s"unexpected tree pattern: $t")
    }
    tree.accept(v)
    v.result
  }
}
