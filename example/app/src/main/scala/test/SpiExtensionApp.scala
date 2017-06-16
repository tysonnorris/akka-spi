package test

import akka.actor.ActorSystem
/**
  * Created by tnorris on 6/7/17.
  */
object SpiExtensionApp {
  def main(args: Array[String]): Unit = {

    val system = ActorSystem("spi-extension-test")

    val spiInstance1:Spi1 = Spi1(system)
    val spiInstance2:Spi2 = Spi2(system)
    val spiInstance3:Spi2 = Spi2(system)

    println("spi instance embedded value:" + spiInstance2.someValue)
    println("spi instance value from config:" + spiInstance2.someValueFromConfig)
  }
}