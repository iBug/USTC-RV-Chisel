package ustcrv

import java.nio.file.Paths

object Main extends App {
  println("USTC-RV-Chisel:test at " + Paths.get(".").toAbsolutePath)

  new ustcrv.core.PCSpec execute
}
