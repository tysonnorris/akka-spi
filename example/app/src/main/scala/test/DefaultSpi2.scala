package test

import com.app.spi.SpiModule

/**
  * Created by tnorris on 6/8/17.
  */


class DefaultSpi2 extends Spi2 {
  val someValue:String="This is a test"
  def someValueFromConfig = config.getString("spi2.conf.key1")
}

class DefaultSpi2Module extends SpiModule {
  bind [Spi2] to new DefaultSpi2 identifiedBy "test.DefaultSpi2"
}