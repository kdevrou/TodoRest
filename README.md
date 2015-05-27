#TodoRest

## Introduction

A simple todo rest api inspired by [Google Tasks](https://mail.google.com/tasks)

A running demo is available on Heroku at https://thawing-cove-3881.herokuapp.com/

## Dependencies

* Java >= 1.7

## Local Development Environment

Before starting make sure that JAVA_HOME is set properly.

To clone the project run the following, replacing <path> with your local directory.
```
git clone https://github.com/kdevrou/TodoRest.git <path>
```

The project is laid out in the following way.
* app/controllers - the classes here handle processing incomming requests
* app/models - these include the definition of the data and how to serialize it to json
* app/views - client for rest api (in progress)
* conf/evolutions/default - scripts to migrate the development database
* conf/application.conf - application config settings
* conf/routes - routing of URLs to controller methods
* public - static project assets
* build.sbt - defines dependencies

### IDE
More detailed instructions can be found in the Play [documentation](https://www.playframework.com/documentation/2.3.x/IDE).

#### IntelliJ Idea
To create IntelliJ Idea project files run the following in the project directory.
```
activator idea
```
(the current version build for Idea v13. Idea 14 will open the project and convert the project the first time that it is opened)

#### Eclipse
Simply run the following in the project directory.
```
activator eclipse
```
And then open the project through eclipse.

### Development server
To start run
```
activator run
```
This will start a server on [http://localhost:9000](http://localhost:9000) which will recompile and restart automatically on any source file change.

