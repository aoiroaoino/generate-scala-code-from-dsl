package scalagen.transversers

import scalagen.{Defn, Term, Tree, Type}

class Transformer {

  def transform(trees: List[Tree]): List[Tree] = trees.map(transform)

  def transform(tree: Tree): Tree = tree match {
    case t @ Type.Struct(fields) =>
      t.copy(fields = transform(fields).asInstanceOf[List[Term.Field]])
    case t: Type.Name =>
      t
    case t: Type.Intrinsic =>
      t
    case t @ Defn.Type(name, typ) =>
      t.copy(
        name = transform(name).asInstanceOf[Type.Name],
        typ = transformType(typ)
      )
    case t @ Term.Field(_, typ) =>
      t.copy(typ = transformType(typ))
    case t: Term.Name =>
      t
    case t =>
      sys.error(s"unexpected error. tree: $t")
  }

  private def transformType(typ: Type): Type = transform(typ).asInstanceOf[Type]
}
