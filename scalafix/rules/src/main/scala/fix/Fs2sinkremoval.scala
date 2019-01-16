package fix

import scalafix.v1._
import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    // println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    //val sinkType = SymbolMatcher.normalized("fs2/Sink/")
    val sinkType = Type.Name("Sink")
    doc.tree.collect {
      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        //println(name.pos.formatMessage("info", "Here is the Sink type."))
        println(s"Found the Sink type and it has the following type arguments: F = $f, A = $a")
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")
    }.asPatch

    //Patch.empty
  }

}
