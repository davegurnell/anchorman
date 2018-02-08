package anchorman

import anchorman.core._
import anchorman.syntax._

class ImagesIntegrationSpec extends IntegrationSpec {
  def name = "play-images"
  def doc = document(
    para(
      image("http://via.placeholder.com/100x100/ff0000/ffffff", "http://via.placeholder.com/50x50/ffffff/ff0000"),
      image("http://via.placeholder.com/100x100/00ff00/ffffff", "http://via.placeholder.com/75x75/ffffff/00ff00"),
      image("http://via.placeholder.com/100x100/00ffff/ffffff", "http://via.placeholder.com/50x50/ffffff/00ffff")
    ).align(TextAlign.Center),
    para(
      image("http://via.placeholder.com/150x150/ff0000/ffffff", "http://via.placeholder.com/100x100/ffffff/ff0000"),
      image("http://via.placeholder.com/150x150/00ff00/ffffff", "http://via.placeholder.com/125x125/ffffff/00ff00"),
      image("http://via.placeholder.com/150x150/00ffff/ffffff", "http://via.placeholder.com/100x100/ffffff/00ffff")
    ).align(TextAlign.Center),
    para(
      image("http://via.placeholder.com/200x200/ff0000/ffffff"),
      image("http://via.placeholder.com/200x200/00ff00/ffffff"),
      image("http://via.placeholder.com/200x200/00ffff/ffffff")
    ).align(TextAlign.Center),
    para(
      image("http://example.com/missing.png"),
      image("http://example.com/missing.jpg")
    ).align(TextAlign.Center)
  )
}
