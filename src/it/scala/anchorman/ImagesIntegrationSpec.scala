package anchorman

import anchorman.core._
import anchorman.syntax._

class ImagesIntegrationSpec extends IntegrationSpec {
  def name = "images"
  def doc = document(
    para(
      image("http://placehold.it/100x100"),
      image("http://placehold.it/100x100"),
      image("http://placehold.it/100x100")
    ).align(TextAlign.Center),
    para(
      image("http://placehold.it/150x150"),
      image("http://placehold.it/150x150"),
      image("http://placehold.it/150x150")
    ).align(TextAlign.Center),
    para(
      image("http://placehold.it/200x200"),
      image("http://placehold.it/200x200"),
      image("http://placehold.it/200x200")
    ).align(TextAlign.Center)
  )
}