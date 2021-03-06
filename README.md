## Susel Metadata Gradle Plugin
A gradle plugin to run the [Susel](https://github.com/udaychandra/susel) tool.

This gradle plugin creates the META-INF/susel.metadata file representing the metadata about the service providers in a given Java module.
The metadata is later used by Susel during runtime to load and cache service providers used by an application.

#### Basic Usage 
###### Prerequisites
* This plugin requires the Java gradle [plugin](https://docs.gradle.org/current/userguide/java_plugin.html) to be applied to your project
* Requires the sourceCompatibility to be set to "11" (JDK 11)
* It is recommended to apply a jigsaw aware plugin like the experimental [jigsaw](https://github.com/gradle/gradle-java-modules) plugin or the more advanced [chainsaw](https://github.com/zyxist/chainsaw) plugin

Apply the Susel plugin to your build script.
```groovy
plugins {
    id 'io.github.udaychandra.susel' version '0.1.2'
}
```

When you run the build task, the plugin automatically reads the "module-info.java" file and generates the metadata about all the service providers that a given module provides.

## Development
This is a community project. All contributions are welcome.

To start contributing, do the following:
* Install JDK 11
* Fork or clone the source code
* Run the build using the gradle wrapper
```bash
gradlew clean build publishToMavenLocal
```

## License
Apache License 2.0
