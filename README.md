# akka-spi

An Akka based SPI (Service Provider Interface) utility.

# Features

* minimal dependencies for SPI implementations and SPI hosting applications
* easy to add
  * new SPIs
  * new SPI implementations
* access to default and application defined configuration values 
  
  
# Usage

### host app
* depend on 'spi' artifact
* define new SPI interface
```scala
package com
trait SomeSpi extends SpiImplementer {
  //interface for SomeSpi here:
  val someValue:String
  def someValueFromConfig:String
}

object SomeSpi extends SpiProvider[SomeSpi](configKey="SomeSpi.impl"){
}
```

* define a default impl
```scala
package com.impl
class DefaultSomeSpi extends SomeSpi {
  val someValue:String="This is the default"
  def someValueFromConfig = config.getString("somespi.conf.key1")
}
```    

* add default configs to `reference.conf`
```properties
#this key drives the impl (note configKey="SomeSpi.impl" from above)
SomeSpi.impl = com.impl.DefaultSomeSpi
#this key drives the config for the impl (note config.getString("somespi.conf.key1")
somespi.conf.key1 = value1
```

* add ServiceLoader conf `META-INF/services/com.SomeSpi`
```text
com.impl.DefaultSomeSpi
```  

# Adding (and enabling) a new SPI impl

* create a new artifact `SomeSpiAlternate`
* needs to depend on artifact where `SomeSpi` defined
* define SomeSpiAlternate
```scala
package alternate
class SomeSpiAlternate extends SomeSpi{
  override val someValue: String = "some value for SomeSpiAlternate"

  override def someValueFromConfig: String = config.getString("alternate.config.key")
}
```
* add ServiceLoader conf `META-INF/services/com.SomeSpi`
```text
alternate.SomeSpiAlternate
```  
* enable the application override in `application.conf`
```properties
SomeSpi.impl = alternate.SomeSpiAlternate
```
* add alternate config (as required by SomeSpiAlternate) to `application.conf`
```properties
alternate.config.key = This is the alternate
```

#Example

See the `example` directory:
* `example/app` - the `SpiExtensionApp` application and default impl of `Spi2` SPI
* `example/app-spi` - the SPI interfaces that the application defines
* `example/impl1` - an externally defined impl of an SPI
* `example/impl2` - another externally defined impl of an SPI

###Current config

The current config leverages the `impl2` version of Spi2, so when you run the app you see the log:
```text
Resolved spi impl for test.Spi2 to com.alternate.DifferentImpl using config key 'spi2.impl'.
```

###Changeing the impl

To change to `impl1` version:
* change the `spi2.impl` in `application.conf` to `com.other.SpiImplOther`

Then on run the log will indicate:
```text
Resolved spi impl for test.Spi2 to com.other.SpiImplOther using config key 'spi2.impl'.
```

### Use the default impl

To use the default impl `DefaultSpi2`:

* delete the `spi2.impl` key in `application.conf`

Then on run the log will indicate:
```text
Resolved spi impl for test.Spi2 to test.DefaultSpi2 using config key 'spi2.impl'.
``` 


