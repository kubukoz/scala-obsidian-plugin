import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js
import typings.obsidian.mod as obsidian
import obsidian.Notice


@JSExportTopLevel("default")
class MyPlugin(app: obsidian.App, manifest: obsidian.PluginManifest) extends obsidian.Plugin(app, manifest) {
  override def onload(): Unit = {
    new Notice("Loaded a Scala plugin!")

    this.addRibbonIcon(
      "dice",
      "Scala plugin",
      _ => new Notice("Welcome from the Scala Plugin button!")
    ): Unit
  }
}
