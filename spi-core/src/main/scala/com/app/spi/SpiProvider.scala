package com.app.spi

import java.util.ServiceLoader

import akka.actor.ActorSystem
import akka.actor.ExtendedActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import akka.event.LoggingAdapter
import com.typesafe.config.Config
import scaldi.Binding
import scaldi.Identifier
import scaldi.Injectable
import scaldi.Injector
import scaldi.Module
import scaldi.MutableInjector

import scala.collection.JavaConverters._
import scala.reflect.ClassTag
import scala.reflect.runtime.universe._

/**
  * Created by tnorris on 6/8/17.
  */
trait Spi  extends Extension {
  //cannot assign config on construction since we use ServiceLoader below...
  var captured:Config = null
  def config:Config = {
    captured
  }
}

abstract class SpiFactoryModule[T: TypeTag](factory:()=>T)  extends SpiModule(factory())

abstract class SpiModule[T: TypeTag](instance:T) extends Module {
  //bind [UserService] to new SimpleUserService
  bind [T] to instance identifiedBy instance.getClass.getName
}

private object SpiModuleLoader {


//  lazy val injector = compose()
  val spiImpl = (ServiceLoader load classOf[SpiModule[_]]).asScala
  //start with an empty injector
  val empty = new MutableInjector {
    override def getBinding(identifiers: List[Identifier]): Option[Binding] = None
    override def getBindings(identifiers: List[Identifier]): List[Binding] = List()
  }

  //using a var here to cache the result; cannot do it on class init due to use of logger
  private var injector:Injector = null
  def buildInjector()(implicit logger:LoggingAdapter): Injector ={
    if (injector != null){
      injector
    } else {

      injector = spiImpl.foldLeft(empty)((composite, nextModule) => {
        logger.info(s"loading module ${nextModule.getClass.getName}")
        composite :: nextModule
      })
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
    implicit val injector = SpiModuleLoader.buildInjector()
    createTyped(system)
  }

  private def createTyped(system: ExtendedActorSystem)(implicit tag: ClassTag[T], injector:Injector): T = {

    val configuredImpl = system.settings.config.getString(configKey)

    if (configuredImpl != null) {
      //val db = inject [T] (identified by 'impl is "configuredImpl")
      val typedResolved =
        inject [T] (identified by configuredImpl)
      typedResolved.captured = system.settings.config
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
