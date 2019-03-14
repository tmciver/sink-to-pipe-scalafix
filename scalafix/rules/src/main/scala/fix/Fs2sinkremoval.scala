package fix

import scalafix.v1._

import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {
  override def fix(implicit doc: SemanticDocument): Patch = {
    println("Tree.structureLabeled: " + doc.tree.structureLabeled)
    SinkRules(doc.tree).asPatch
  }
}

object SinkRules {
  val toMethodMatcher = SymbolMatcher.exact("fs2/Stream#to().")

  def apply(t: Tree)(implicit doc: SemanticDocument): List[Patch] = {
    t.collect {
      case toMethodMatcher(t @ Term.Apply(Term.Select(obj, _), args)) =>
        Patch.replaceTree(t, Term.Apply(Term.Select(obj, Term.Name("through")), args).toString)

      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")

      case sink @ Importee.Rename(Name("Sink"), rename) =>
        Patch.replaceTree(sink, s"Pipe => $rename")

      case sink @ Importee.Name(Name("Sink")) =>
        List(
          Patch.removeImportee(sink),
          // Adds `fs2.Pipe` if `fs2._` is not present
          Patch.addGlobalImport(Symbol("fs2/Pipe."))
        ).asPatch
    }
  }
}
