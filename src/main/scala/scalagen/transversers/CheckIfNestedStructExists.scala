package scalagen.transversers

import scalagen.{Term, Tree, Type}

object CheckIfNestedStructExists {

  def run(tree: Tree): Unit = traverser.traverse(tree)

  def run(trees: List[Tree]): Unit = traverser.traverse(trees)

  private val traverser =
    new Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        case Term.Field(_, Type.Struct(_)) =>
          throw new Exception("Nested Struct exists")
        case _ =>
          super.traverse(tree)
      }
    }
}
