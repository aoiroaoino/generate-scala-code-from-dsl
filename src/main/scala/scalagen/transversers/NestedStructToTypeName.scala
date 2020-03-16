package scalagen
package transversers

object NestedStructToTypeName {
  def run(tree: Tree): Tree              = transformer.transform(tree)
  def run(trees: List[Tree]): List[Tree] = transformer.transform(trees)

  private val transformer =
    new Transformer {
      override def transform(tree: Tree): Tree = tree match {
        case Term.Field(name, Type.Struct(_)) =>
          Term.Field(name, Type.Name(name.value.capitalize))
        case _ =>
          super.transform(tree)
      }
    }
}
