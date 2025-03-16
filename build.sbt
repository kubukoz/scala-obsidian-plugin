import org.typelevel.sbt.tpolecat.DevMode

lazy val root = project
  .in(file("."))
  .settings(
    scalaVersion := "3.6.4",
    tpolecatOptionsMode := DevMode
  )
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.CommonJSModule)
    },
    (Compile / fastLinkJS) := {
      val result = (Compile / fastLinkJS).value
      IO.copyDirectory(
        (Compile / fastLinkJS / scalaJSLinkerOutputDirectory).value,
        (baseDirectory.value)
      )
      result
    },
    (Compile / fullLinkJS) := {
      val result = (Compile / fullLinkJS).value
      IO.copyDirectory(
        (Compile / fullLinkJS / scalaJSLinkerOutputDirectory).value,
        (baseDirectory.value)
      )
      result
    }
  )
  .enablePlugins(ScalablyTypedConverterExternalNpmPlugin)
  .settings(
    externalNpm := {
      import sys.process._
      "yarn".!
      (ThisBuild / baseDirectory).value
    }
  )
