import cats.effect.IO
import cats.effect.kernel.Resource
import cats.effect.kernel.Resource.ExitCase
import cats.effect.unsafe.IORuntime
import io.circe.Codec
import org.http4s.Request
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.dom.FetchClientBuilder
import org.http4s.implicits.*
import org.scalajs.dom.HTMLElement
import typings.obsidian.mod as obsidian

import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation.JSExportTopLevel

import obsidian.Notice
import obsidian.ItemView
import obsidian.WorkspaceLeaf
import obsidian.ViewState

// note that we have to specify a full constructor, otherwise the plugin will not load.
// apparently, in such cases the `app` and `manifest` fields are missing or left undefined, which messes with the lifecycle.
@JSExportTopLevel("default")
class MyPlugin(app: obsidian.App, manifest: obsidian.PluginManifest)
    extends obsidian.Plugin(app, manifest) {
  override def onload(): Unit = {
    new Notice("Loaded a Scala plugin!")
    this.registerView(ExampleView.viewType, (leaf) => new ExampleView(leaf))

    this.addRibbonIcon(
      "dice",
      "Activate Scala View",
      _ => {
        activateView().unsafeRunAndForget()(IORuntime.global)
      }
    ): Unit
  }

  def activateView(): IO[Unit] = IO
    .defer {
      val workspace = app.workspace
      val leaves = workspace
        .getLeavesOfType(ExampleView.viewType)
        .asInstanceOf[js.Array[WorkspaceLeaf]]

      leaves
        .match {
          case leaves if leaves.length > 0 => IO { leaves(0) }
          case _ =>
            for {
              leaf <- IO { workspace.getRightLeaf(false) }
              _ <- IO.fromPromise(
                IO(
                  leaf.setViewState(
                    ViewState(ExampleView.viewType).setActive(true)
                  )
                )
              )
            } yield leaf
        }
        .flatMap { leaf =>
          IO.fromPromise { IO(workspace.revealLeaf(leaf)) }
        }
    }
}

case class Repo(name: String) derives Codec.AsObject

class ExampleView(leaf: WorkspaceLeaf) extends ItemView(leaf) {
  override def getViewType(): String = ExampleView.viewType
  override def getDisplayText(): String = "example scala view"

  def view: Resource[IO, fs2.dom.HtmlElement[IO]] = {

    import calico.html.io.given
    import calico.html.io.*
    import scala.concurrent.duration.*

    val now = fs2.Stream
      .fixedRateStartImmediately[IO](1.second)
      .evalMap(_ => IO.realTimeDate)
      .holdOptionResource
      .map(_.map { date =>
        date match {
          case None    => ""
          case Some(d) => d.toLocaleTimeString()
        }
      })

    val reposSigResource = fs2.Stream
      .eval(
        FetchClientBuilder[IO].resource
          .use { client =>
            client
              .run(
                Request(uri = uri"https://api.github.com/users/typelevel/repos")
              )
              .use { response =>
                response.as[List[Repo]]
              }
          }
      )
      .holdResource(Nil)
      .map { sig =>
        ul(
          children <-- sig.map { _.map { item => li(item.name) } }
        )

      }

    div(
      "Welcome to Calico in an Obsidian plugin! The time is now: ",
      now,
      reposSigResource
    )

  }

  private var finalizer: ExitCase => IO[Unit] = _ => IO.unit

  override def onOpen(): Promise[Unit] = {
    IO {
      this.containerEl
        .asInstanceOf[js.Dynamic]
        .children
        .asInstanceOf[js.Array[typings.obsidian.mod.global.HTMLElement]]
        .apply(1)
        .asInstanceOf[fs2.dom.Node[IO]]
    }.toResource.flatMap { root =>
      import calico.syntax.*
      view.renderInto(root)
    }
  }.allocatedCase
    .map(_._2)
    .flatMap { finalizer => IO { this.finalizer = finalizer } }
    .unsafeToPromise()(IORuntime.global)

  override def onClose(): Promise[Unit] = IO(this.finalizer)
    .flatMap(_.apply(ExitCase.Succeeded))
    .unsafeToPromise()(IORuntime.global)
}

object ExampleView {
  val viewType = "example-view"
}
