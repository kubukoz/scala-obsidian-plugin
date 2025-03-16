import org.typelevel.sbt.tpolecat.DevMode

lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := "3.6.4",
    tpolecatOptionsMode := DevMode
  )
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaJSLinkerConfig ~= {
    _.withModuleKind(ModuleKind.CommonJSModule)
  })
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    externalNpm := {
      import sys.process._
      "yarn".!
      (ThisBuild / baseDirectory).value
    }
  )
