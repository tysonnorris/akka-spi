package com.alternate

import com.app.spi.SpiFactoryModule
import test.Spi2

/**
  * Created by tnorris on 6/8/17.
  */
class DifferentImpl extends Spi2 {
  override val someValue: String = "this is impl2"
  override def someValueFromConfig: String = config.getString("spi2.conf.different.key2")
}

//here we use a factory to build the impl
object DifferImplFactory {
  def create() = new DifferentImpl
}
class DifferentSpi2Module extends SpiFactoryModule[Spi2](DifferImplFactory.create)