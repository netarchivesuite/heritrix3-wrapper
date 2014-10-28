[![Build Status](https://travis-ci.org/netarchivesuite/heritrix3-wrapper.svg)](https://travis-ci.org/netarchivesuite/heritrix3-wrapper)

heritrix3-wrapper
=================

Small wrapper to unzip, start/stop and communicate with Heritrix 3.

## Features ##

* Unzip and preserve unix permissions.
* Start H3 and capture exit value and output/error streams.
* Perform H3 REST API calls and convert result to Java objects using JAXB.

## REST API supported and wrapped ##

### Engine ###

* Exit Java process.
* Garbage Collect.
* Re-scan job directory.
* Create new job.
* Add job directory.

All of these commands return an engine xml result with is converted to Java objects, with the exception of "Exit Java process" which kills the H3 JVM immediate if all the right conditions are met.

### Job ###

* Status.
* Copy.
* Build job configuration.
* Teardown.
* Launch.
* Pause.
* Unpause.
* Checkpoint.
* Terminate.

### Script ###

...
