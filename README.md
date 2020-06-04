[![Build Status](https://travis-ci.org/netarchivesuite/heritrix3-wrapper.svg)](https://travis-ci.org/netarchivesuite/heritrix3-wrapper)
[![codecov](https://codecov.io/gh/netarchivesuite/heritrix3-wrapper/branch/master/graph/badge.svg)](https://codecov.io/gh/netarchivesuite/heritrix3-wrapper)

heritrix3-wrapper
=================

Small wrapper to unzip, start/stop and communicate with Heritrix 3.

## Features ##

* Unzip and preserve unix permissions.
* Start H3 and capture exit value and output/error streams.
* Perform H3 REST API calls and convert result to Java objects using JAXB.
* XML validation of response data.
* anypath methods including support for header method request and/or byte range request.

## REST API supported and wrapped ##

### EngineResource ###

* Exit Java process.
* Garbage Collect.
* Re-scan job directory.
* Create new job.
* Add job directory.

All of these commands return an engine xml result which is converted to Java objects, with the exception of "Exit Java process" which kills the H3 JVM immediate if all the right conditions are met.

### JobResource ###

* Status.
* Copy.
* Build job configuration.
* Teardown.
* Launch.
* Pause.
* Unpause.
* Checkpoint.
* Terminate.

All of these commands return a job xml result which is converted to Java objects.

### ScriptResource ###

* Script

XML result convert to Java objects.

### Build and Deploy ###

Deploy snapshot
mvn -DskipTests -Dmaven.javadoc.skip=true clean deploy

Release
mvn -Darguments='-DskipTests' release:prepare
mvn -Darguments='-DskipTests -Dmaven.javadoc.skip=true' release:perform