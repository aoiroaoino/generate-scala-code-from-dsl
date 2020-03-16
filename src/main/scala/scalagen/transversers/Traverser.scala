package scalagen.transversers

import scalagen.Tree

class Traverser {
  def traverse(trees: List[Tree]): Unit = trees.foreach(traverse)
  def traverse(tree: Tree): Unit        = tree.children.foreach(traverse)
}
