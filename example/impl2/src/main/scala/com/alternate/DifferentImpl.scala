package com.alternate

import com.app.spi.SpiFactoryModule
import com.app.spi.SpiModule
import com.app.spi.SpiModuleLoader
import com.typesafe.config.Config
import test.Spi2
import scaldi.Injectable._
import scaldi.Injector



/**
  * Created by tnorris on 6/8/17.
  */
class DifferentImpl(config:Config) extends Spi2 {

  override val someValue: String = "this is impl2"
  override def someValueFromConfig: String = config.getString("spi2.conf.different.key2")
}

//here we use a factory to build the impl
object DifferImplFactory {


  def create(injector:Injector) = {
//    implicit val injector:Injector = SpiModuleLoader.configInjector

    implicit val inj = injector
    val config = inject [Config]
    new DifferentImpl(config)
  }
}
class DifferentSpi2Module extends SpiFactoryModule[Spi2](DifferImplFactory.create)
