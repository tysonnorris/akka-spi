package com.other

import com.app.spi.SpiModule
import test.Spi2

/**
  * Created by tnorris on 6/8/17.
  */
class SpiImplOther extends Spi2{
  override val someValue: String = "some value for SpiImplOther"
  override def someValueFromConfig: String = config.getString("spi2.config.key2")
}
//here we build the impl directly in constructor
class OtherSpi2Module extends SpiModule[Spi2](new SpiImplOther)