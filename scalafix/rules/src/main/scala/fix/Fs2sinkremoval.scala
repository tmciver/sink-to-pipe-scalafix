package fix

import scalafix.v1._
import scala.meta._

class Fs2sinkremoval extends SemanticRule("Fs2sinkremoval") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    // println("Tree.syntax: " + doc.tree.syntax)
    println("Tree.structure: " + doc.tree.structure)
    // println("Tree.structureLabeled: " + doc.tree.structureLabeled)

    // Patch to replace Sink type annotation with Pipe type annotation
    val typePatch = doc.tree.collect {
      case sink @ Type.Apply(Type.Name("Sink"), List(f, a)) =>
        println(s"Found the Sink type and it has the following type arguments: F = $f, A = $a")
        Patch.replaceTree(sink, s"Pipe[$f, $a, Unit]")
      // case q"import ..$importersnel" => {
      //   println(importersnel)
      //   Patch.empty
      // }
      case imp@Importer(Term.Name("fs2"), importees) =>
        val sinkImportee = Importee.Name(scala.meta.Name.Indeterminate("Sink"))
        val pipeImportee = Importee.Name(scala.meta.Name.Indeterminate("Pipe"))
        val renamedImports = importees.map({
          //case `sinkImportee` => pipeImportee
          case Importee.Name(scala.meta.Name.Indeterminate(name)) => { println(s"Name: $name"); if (name == "Sink") pipeImportee else sinkImportee }
          case x => x
        })
        val importerRenamed = Importer(Term.Name("fs2"), renamedImports)
        //val importees = importers.flatmap(_.importees)
        println(s"Importees: $importees")
        println(s"Renamed imports: $importerRenamed")
        Patch.replaceTree(imp, importerRenamed.toString)

      case _ => Patch.empty
    }.asPatch

    typePatch

    // Patch to change fs2.Sink import to fs2.Pipe
    //val importPatch = Patch.renameSymbol(Symbol("fs2/Sink."), "fs2.Pipe")

    //typePatch + importPatch
  }
}
