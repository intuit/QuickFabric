# Quickfabric Middleware Local Build
---

# To build Quickfabric Middleware locally

Requirements
------------

- [Java](https://www.oracle.com/technetwork/java/javase/overview/java8-2100321.html) 1.8+
- [mvn](https://maven.apache.org/download.cgi) 3.6+

## Build

### Without Unit Tests

```bash 
   mvn clean install -DskipTests 
```

### With Unit Tests

```bash 
   mvn clean install 
```

Corresponding Jar files for commons, emr and schedulers can be found under respective target folders.

You can use a local tomcat or deploy these jars into a host running tomcat to consume these jars.