package fix

import scalafix.v1._

import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {
  override def fix(implicit doc: SemanticDocument): Patch = {
//    println("Tree.structureLabeled: " + doc.tree.structureLabeled)
    SinkRules(doc.tree)
  }
}

object SinkRules {
  val toMethodMatcher = SymbolMatcher.exact("fs2/Stream#to().")

  def isFs2SinkImported(t: Tree): Boolean =
    t.collect {
      case Importer(Term.Name("fs2"), importees) =>
        importees.collectFirst {
          case Importee.Name(Name("Sink")) => ()
          case _: Importee.Wildcard => ()
        }.isDefined
   }.exists(identity)

  def replaceImport(sinkImportee: Importee)(implicit doc: SemanticDocument): Patch = List(
    Patch.removeImportee(sinkImportee),
    Patch.addGlobalImport(Symbol("fs2/Pipe."))
  ).asPatch

  def apply(t: Tree)(implicit doc: SemanticDocument): Patch = {
    if (isFs2SinkImported(t)) {
      t.collect {
        case toMethodMatcher(t @ Term.Apply(Term.Select(obj, _), args)) =>
          Patch.replaceTree(t, Term.Apply(Term.Select(obj, Term.Name("through")), args).toString)

        case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
          Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")

        case sink @ Importee.Rename(Name("Sink"), rename) =>
          Patch.replaceTree(sink, s"Pipe => $rename")

        case sink @ Importee.Name(Name("Sink")) =>
          replaceImport(sink)

      }.asPatch
    }
    else {
      Patch.empty
    }
  }
}
