package fix

import scalafix.v1._
import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    // println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    val sinkMatcher = SymbolMatcher.exact("fs2/Sink.")

    doc.tree.collect {

      // Patch to replace Sink type annotation with Pipe type annotation
      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")

      // Case to fix fs2.Sink import
      case sinkMatcher(sink @ Importee.Name(name)) =>
        Patch.replaceTree(sink, "Pipe")

      case _ => Patch.empty
    }.asPatch
  }
}
