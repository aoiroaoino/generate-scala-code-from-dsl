package scalagen

import scalagen.transversers.{IntToFloat, Transformer}

import scala.util.chaining._

object Main {
  import visitors._

  val example = new {

    // type AccountId = Int
    val accountId: Tree = Defn.Type(Type.Name("AccountId"), Type.Int)

    // type Account = {
    //   id: AccountId,
    //   name: String
    // }
    val account: Tree =
      Defn.Type(
        Type.Name("Account"),
        Type.Struct(
          Term.Field(Term.Name("id"), Type.Name("AccountId")),
          Term.Field(Term.Name("name"), Type.String)
        )
      )

    // type User = {
    //   name: String,
    //   age: Int,
    //   address: {
    //     postcode: String,
    //     city: {
    //       name: String,
    //     },
    //     pos: {
    //       lan: Float,
    //       lon: Float
    //     }
    //   },
    //   isActive: Boolean
    // }
    val user: Tree =
      Defn.Type(
        Type.Name("User"),
        Type.Struct(
          Term.Field(Term.Name("name"), Type.String),
          Term.Field(Term.Name("age"), Type.Int),
          Term.Field(
            Term.Name("address"),
            Type.Struct(
              Term.Field(Term.Name("postcode"), Type.String),
              Term.Field(
                Term.Name("city"),
                Type.Struct(
                  Term.Field(Term.Name("name"), Type.String)
                )
              ),
              Term.Field(
                Term.Name("pos"),
                Type.Struct(
                  Term.Field(Term.Name("lat"), Type.Float),
                  Term.Field(Term.Name("lon"), Type.Float)
                )
              )
            )
          ),
          Term.Field(Term.Name("isActive"), Type.Boolean)
        )
      )

    val trees = List(accountId, account, user)
  }

  def main(args: Array[String]): Unit = {

    // ==============================
    // === Visitor
    // ===
    section("PrintVisitor") {
      comment("まずは雑に print してみる。綺麗にはならないですね。") {
        val v = new PrintVisitor
        example.trees.foreach(_.accept(v))
      }
    }
    section("PrettyPrintVisitor") {
      comment("AST を走査しながら各ノードで文字列を組み立てる。AST から DSL を復元できた。") {
        val v = new PrettyPrintVisitor(indentSpaceSize = 2)
        example.trees.foreach(_.accept(v))
        println(v.result)
      }
      comment("頑張れば簡易的な formatter が作れるかも?") {
        val v = new PrettyPrintVisitor(indentSpaceSize = 8)
        example.trees.foreach(_.accept(v))
        println(v.result)
      }
    }
    section("CollectTermNameVisitor") {
      comment("AST に含まれる Term.Name を全て列挙する。必要な情報を集める、といった使い方も可能。") {
        val v = new CollectTermNameVisitor
        example.trees.foreach(_.accept(v))
        println(v.result)
      }
    }
    section("DirectTranslationVisitor") {
      val v = new DirectTranslationVisitor
      comment("一見問題なく展開できているように見える") {
        example.account.accept(v)
        println(v.result)
      }
      comment("しかし、シンプルに実装しただけでは型エイリアスへの展開が考慮できない") {
        example.accountId.accept(v)
        println(v.result)
      }
      comment("また、DSL は匿名 Struct を許容するが、これをトップレベルの case class としてうまく切り出すことが難しい") {
        example.user.accept(v)
        println(v.result)
      }
    }
    section("ModelDrivenTranslationVisitor") {
      val v = new ModelDrivenTranslationVisitor
      comment("Functional Visitor + ScalaSource Model でいい感じに出力できるようにはなる") {
        example.trees.foreach(_.accept(v))
        println(v.result)
      }
    }

    // ==============================
    // === Traverser and Transformer
    // ===
    section("CollectTermNameTraverser") {}

    section("IntToFloatTransformer") {
      comment("Int 型を Float 型に変換する") {
        comment("before: ") {
          println(example.accountId)
        }
        comment("after: ") {
          val r = IntToFloat(example.accountId)
          println(r)
        }
      }
    }

    // ==============================
    // === Generate Scala codes
    // ===
    section("GenerateScalaCode") {
      comment("いい感じに Scala のコードを生成することが出来ました") {
        GenerateScalaCode.run(example.trees).tap(println)
      }
    }
  }

  private def section(s: String)(proc: => Unit): Unit =
    s"""
       |/**
       | * $s
       | */
       |""".stripMargin.pipe(println).tap(_ => proc)

  private def comment(s: String)(proc: => Unit): Unit = println(s"// $s").tap(_ => proc)
}
