package com.other

import com.app.spi.Spi
import com.app.spi.SpiInjectedModule
import com.app.spi.SpiModule
import com.app.spi.SpiModuleLoader
import com.sun.tools.javac.code.TypeTag
import com.typesafe.config.Config
import scaldi.Injectable.inject
import scaldi.Injector
import scaldi.Module
import test.Spi2

/**
  * Created by tnorris on 6/8/17.
  */
class SpiImplOther(config: Config) extends Spi2{
//  implicit val inj = injector
//  val config = inject [Config]
  println("creating SpiImplOther")
  override val someValue: String = "some value for SpiImplOther"
  override def someValueFromConfig: String = config.getString("spi2.config.key2")
}
//here we build the impl directly in constructor
class OtherSpi2Module() extends SpiInjectedModule[Spi2,SpiImplOther]{
//  implicit val injector = SpiModuleLoader.configInjector

  println(s"injector: ${injector}")

  bind [Spi2] to injected[SpiImplOther]  identifiedBy classOf[SpiImplOther].getName
}

//abstract class SpiInjectedModule extends Module {
//  //bind [Tokens] to injected [TokenRepo]
//
//  bind [Spi2] to injected[SpiImplOther]
//}