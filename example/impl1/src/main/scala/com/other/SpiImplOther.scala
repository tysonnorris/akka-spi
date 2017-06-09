package com.other

import test.Spi2

/**
  * Created by tnorris on 6/8/17.
  */
class SpiImplOther extends Spi2{
  override val someValue: String = "some value for SpiImplOther"

  override def someValueFromConfig: String = config.getString("spi2.config.key2")
}
