package test

import com.app.spi.Spi
import com.app.spi.SpiProvider

/**
  * Created by tnorris on 6/9/17.
  */
trait Spi1 extends Spi {
  val someValue:String
  def someValueFromConfig:String
}



object Spi1 extends SpiProvider[Spi1](configKey="spi1.impl")


