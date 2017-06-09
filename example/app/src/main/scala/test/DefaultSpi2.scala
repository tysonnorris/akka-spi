package test

/**
  * Created by tnorris on 6/8/17.
  */


class DefaultSpi2 extends Spi2 {
  val someValue:String="This is a test"
  def someValueFromConfig = config.getString("spi2.conf.key1")
}