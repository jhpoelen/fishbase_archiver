package com.github.jhpoelen

import org.scalatest._

class FishbaseTaxonCache$Test extends FlatSpec with Matchers  {

  "imageName" should "be transformed in image url" in {
    SeaLifeBase.imageNameToUrl("bla") should be(None)
    SeaLifeBase.imageNameToUrl("bla.jpg") should be(Some("http://sealifebase.org/images/thumbnails/jpg/tn_bla.jpg"))
  }

  "fishbase imageName" should "be transformed in image url" in {
    FishBase.imageNameToUrl("bla") should be(None)
    FishBase.imageNameToUrl("bla.jpg") should be(Some("http://fishbase.org/images/thumbnails/jpg/tn_bla.jpg"))
  }

}