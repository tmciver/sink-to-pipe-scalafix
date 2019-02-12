package fix

import scalafix.v1._
import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    // println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    val sinkMatcher = SymbolMatcher.exact("fs2/Sink.")
    val toMethodMatcher = SymbolMatcher.exact("fs2/Stream#to().")

    val mainPatch = doc.tree.collect {

      // Patch to replace Sink type annotation with Pipe type annotation
      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")

      // Case to fix fs2.Sink import
      // case sinkMatcher(sink @ Importee.Name(name)) =>
      //   Patch.replaceTree(sink, "Pipe")

      // Case to change calls to Stream.to to Stream.through
      case toMethodMatcher(t @ Term.Apply(Term.Select(obj, _), args)) =>
        Patch.replaceTree(t, Term.Apply(Term.Select(obj, Term.Name("through")), args).toString)

      case _ => Patch.empty
    }.asPatch

    val sinkImportee = Importee.Name(scala.meta.Name.Indeterminate("Sink"))
    val importPatch = Patch.removeImportee(sinkImportee) + Patch.addGlobalImport(importer"fs2.Pipe")

    mainPatch + importPatch
  }
}
