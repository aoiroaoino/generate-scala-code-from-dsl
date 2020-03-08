package scalagen
package transversers

object IntToFloat extends (Tree => Tree) {
  override def apply(t: Tree): Tree =
    new Transformer {
      override def transform(tree: Tree): Tree = tree match {
        case Type.Intrinsic.Int() =>
          Type.Float
        case _ =>
          super.transform(tree)
      }
    }.transform(t)
}
