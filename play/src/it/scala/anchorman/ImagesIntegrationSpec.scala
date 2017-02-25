package anchorman

import anchorman.core._
import anchorman.syntax._

class ImagesIntegrationSpec extends IntegrationSpec {
  def name = "play-images"
  def doc = document(
    para(
      image("http://placehold.it/100x100", "http://placehold.it/10x10"),
      image("http://placehold.it/100x100", "http://placehold.it/20x20"),
      image("http://placehold.it/100x100", "http://placehold.it/30x30")
    ).align(TextAlign.Center),
    para(
      image("http://placehold.it/150x150", "http://placehold.it/40x40"),
      image("http://placehold.it/150x150", "http://placehold.it/50x50"),
      image("http://placehold.it/150x150", "http://placehold.it/60x60")
    ).align(TextAlign.Center),
    para(
      image("http://placehold.it/200x200"),
      image("http://placehold.it/200x200"),
      image("http://placehold.it/200x200")
    ).align(TextAlign.Center),
    para(
      image("http://example.com/missing.png"),
      image("http://example.com/missing.jpg")
    ).align(TextAlign.Center)
  )
}
