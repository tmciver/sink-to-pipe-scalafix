package fix

import scalafix.v1._
import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // println("Tree.syntax: " + doc.tree.syntax)
    //println("Tree.structure: " + doc.tree.structure)
    // println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    doc.tree.collect {

      // Patch to replace Sink type annotation with Pipe type annotation
      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")

      // Case to fix fs2.Sink import
      case imp@Importer(Term.Name("fs2"), importees) =>
        val sinkImportee = Importee.Name(scala.meta.Name.Indeterminate("Sink"))
        val pipeImportee = Importee.Name(scala.meta.Name.Indeterminate("Pipe"))
        val renamedImports = importees.map({
          //case `sinkImportee` => pipeImportee  // why doesn't this match work?
          case Importee.Name(scala.meta.Name.Indeterminate(name)) => { println(s"Name: $name"); if (name == "Sink") pipeImportee else sinkImportee }
          case x => x
        })
        val importerRenamed = Importer(Term.Name("fs2"), renamedImports)
        Patch.replaceTree(imp, importerRenamed.toString)

      case _ => Patch.empty
    }.asPatch
  }
}
