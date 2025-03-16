import scala.scalajs.js.annotation.JSExportTopLevel
import scala.scalajs.js
import typings.obsidian.mod as obsidian
import obsidian.Notice


// note that we have to specify a full constructor, otherwise the plugin will not load.
// apparently, in such cases the `app` and `manifest` fields are missing or left undefined, which messes with the lifecycle.
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
