package fix

import scalafix.v1._
import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    // println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    val sinkSym = SymbolMatcher.exact("fs2/Sink.")
    doc.tree.collect {
      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        println(s"Found the Sink type and it has the following type arguments: F = $f, A = $a")
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")
      case sym @ sinkSym(name) =>
        println(s"Found Sink symbol with name $name")
        //println(s"sym is: $sym")
        val info = sym.symbol.info
        println(s"Sink symbol info: $info")
        Patch.replaceTree(sym, "fs2.Pipe")
    }.asPatch
  }
}
