# scala-obsidian-plugin

A sample Obsidian plugin in Scala.js, with ScalablyTyped bindings for Obsidian's API.

## Usage / development

1. Follow the [plugin development guide](https://docs.obsidian.md/Plugins/Getting+started/Build+a+plugin) to put this repository in a vault's plugins
2. Run `sbt ~fastLinkJS` (or `fullLinkJS`, for minimized builds) - in this setup, that'll put the output file in the root of the project, letting Obsidian see it.
3. Enable the plugin
4. Work on the code!

## Limitations / caveats

- This setup hasn't been made for bundlers, so you may have trouble using e.g. additional dependencies. YMMV, if you get it to work nicely with Vite feel free to open a Pull Request.
- Distribution is for you to handle. This is merely a showcase provind that it's possible to use Scala.js for these plugins.
