package test

import com.app.spi.SpiModule

/**
  * Created by tnorris on 6/16/17.
  */
class DefaultSpi1 extends Spi1 {
  override val someValue: String = "this is spi1"

  override def someValueFromConfig: String = "none"
}
class DefaultSpi1Module extends SpiModule[Spi1](new DefaultSpi1)