package test

import com.app.spi.SpiModule
import com.typesafe.config.Config

/**
  * Created by tnorris on 6/8/17.
  */


class DefaultSpi2(config:Config) extends Spi2 {
  val someValue:String="This is a test"
  def someValueFromConfig = config.getString("spi2.conf.key1")
}

class DefaultSpi2Module extends SpiModule