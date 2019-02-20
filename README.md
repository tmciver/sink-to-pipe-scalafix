# FS2 Sink-to-Pipe Scalafix

## Before Running the Fix

Add the Scalafix plugin by adding the following line to your
`project/plugins.sbt` file:

    addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.9.4")

## Running the Fix

Start an SBT session in the project in which you want to apply the fix and then
run the following commands:

    sbt:project-name> scalafixEnable
    sbt:project-name> scalafix github:tmciver/sink-to-pipe-scalafix/Fs2sinkremoval

If all went well, SBT will report that the command ran successfully.

## Cleanup

At the moment this scalafix rule adds extraneous imports of `fs2.Pipe` to all
Scala source files.  To remove them you should run the scalafix rule
`RemoveUnused` which removes unused imports and terms from source files.  You
can read about that rule
[here](https://scalacenter.github.io/scalafix/docs/rules/RemoveUnused.html) but
the gist is that you must first enable the `warn-unused` compiler option by
adding the following to your build file:

    scalacOptions += "-Ywarn-unused"
    
Additionally you need to _disable_ the `fatal-warnings` option, if you have it
enabled.  Once that is done, run the following command in the SBT session:

    scalafix RemoveUnused
