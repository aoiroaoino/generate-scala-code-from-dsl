package scalagen.transversers

import scalagen.{Defn, Term, Tree, Type}

import scala.collection.mutable.ListBuffer

object ExtractNestedStruct {

  def run(tree: Tree): List[Defn.Type] = run(tree :: Nil)

  def run(trees: List[Tree]): List[Defn.Type] = {
    val buf = new ListBuffer[Defn.Type]()
    new Traverser {
      override def traverse(tree: Tree): Unit = tree match {
        case Term.Field(name, struct @ Type.Struct(_)) =>
          buf += Defn.Type(Type.Name(name.value.capitalize), struct)
          super.traverse(struct)
        case _ =>
          super.traverse(tree)
      }
    }.traverse(trees)
    buf.toList
  }
}
