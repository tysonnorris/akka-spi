package com.app.spi

import java.util.ServiceLoader

import akka.actor.ActorSystem
import akka.actor.ExtendedActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import com.typesafe.config.Config
import scaldi.Binding
import scaldi.Identifier
import scaldi.Injectable
import scaldi.Injector
import scaldi.Module
import scaldi.MutableInjector
import scaldi.WireBuilder

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * Created by tnorris on 6/8/17.
  */
trait Spi  extends Extension {
  //cannot assign config on construction since we use ServiceLoader below...
//  var captured:Config = null
//  def config:Config = {
//    captured
//  }
}

abstract class SpiFactoryModule[T: TypeTag](factory:Injector=>T)  extends SpiModule(factory(SpiModuleLoader.configInjector))

//object SpiModule {
//  implicit def injector = SpiModuleLoader.configInjector
//}


class SpiBaseBindings extends Module {
  bind [Config] to SpiModuleLoader.config
}

abstract class SpiModule[Spi: TypeTag](instance:Spi) extends SpiBaseBindings {
  bind [Spi] to instance identifiedBy instance.getClass.getName
}
//abstract class SpiInjectedModule[Spi: TypeTag, SpiImpl<:Spi:TypeTag[SpiImpl]](implicit tag: ClassTag[SpiImpl]) extends SpiModule {
//  //bind [Tokens] to injected [TokenRepo]
//
//
//}
abstract class SpiInjectedModule[Spi: TypeTag, SpiImpl<:Spi: TypeTag](implicit tag:ClassTag[SpiImpl]) extends SpiModule {
  //bind [Spi] to injected[SpiImpl] // identifiedBy classOf[SpiImpl].getName
}

class SpiConfigInjector(actorSystem:ActorSystem) extends Module {
  bind [Config] to actorSystem.settings.config
  SpiModuleLoader.config = actorSystem.settings.config
}

object SpiModuleLoader {

  var config:Config = null
  private var configInj:Injector = null
  def configInjector = configInj
  def setConfigInjector(injector:Injector) = {
    configInj = injector
  }


//  lazy val injector = compose()
  val spiImpl = (ServiceLoader load classOf[SpiModule[_]]).asScala
  //start with an empty injector
  val empty = new MutableInjector {
    override def getBinding(identifiers: List[Identifier]): Option[Binding] = None
    override def getBindings(identifiers: List[Identifier]): List[Binding] = List()
  }

  //using a var here to cache the result; cannot do it on class init due to use of logger
  private var injector:Injector = null

  def initInjector(actorSystem:ActorSystem)(): Injector ={
    if (injector != null){
      injector
    } else {
      println("initing injector...")
      configInj = new SpiConfigInjector(actorSystem)
      injector = spiImpl.foldLeft(empty)((composite, nextModule) => {
        actorSystem.log.info(s"loading module ${nextModule.getClass.getName}")
        composite :: nextModule
      })
      //injector :: configInjector
      injector
    }
  }
}

abstract class SpiProvider[T <: Spi: TypeTag](configKey: String)(implicit tag: ClassTag[T]) extends ExtensionId[T] with ExtensionIdProvider with Injectable {

  override def apply(system: ActorSystem): T = {
    system.registerExtension(this)
  }

  override def createExtension(system: ExtendedActorSystem): T = {
    implicit val logger = system.log
    implicit val injector = SpiModuleLoader.initInjector(system)//should already be inited
    createTyped(system)
  }

  private def createTyped(system: ExtendedActorSystem)(implicit tag: ClassTag[T], injector:Injector): T = {

    val configuredImpl = system.settings.config.getString(configKey)

    if (configuredImpl != null) {
      //val db = inject [T] (identified by 'impl is "configuredImpl")
      val typedResolved =
        inject [T] (identified by configuredImpl)
//      typedResolved.captured = system.settings.config
      system.log.info(s"Resolved spi impl for ${tag.runtimeClass.getName} to ${typedResolved.getClass.getName} using config key '${configKey}'.")
      typedResolved
    } else {
      //no default and no override? return Nothing?
      throw new IllegalStateException(s"no default and no override config key ${configKey} to resolve impl for ${tag.runtimeClass.getName}")
    }
  }

  override def lookup(): ExtensionId[_ <: Extension] = {
    this
  }
}
