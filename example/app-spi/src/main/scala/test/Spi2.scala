package test

import com.app.spi.Spi
import com.app.spi.SpiProvider

/**
  * Created by tnorris on 6/9/17.
  */
trait Spi2 extends Spi {
  val someValue:String
  def someValueFromConfig:String
}



object Spi2 extends SpiProvider[Spi2](configKey="spi2.impl")
