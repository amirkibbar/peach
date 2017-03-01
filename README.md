# redis-gradle-plugin

A redis gradle plugin

[ ![Build Status](https://travis-ci.org/amirkibbar/peach.svg?branch=master) ](https://travis-ci.org/amirkibbar/peach)
[ ![Download](https://api.bintray.com/packages/amirk/maven/gradle-redis-plugin/images/download.svg) ](https://bintray.com/amirk/maven/gradle-redis-plugin/_latestVersion)

# Using

Plugin setup with gradle >= 2.1:

```gradle
    
    plugins {
      id "ajk.gradle.redis" version "0.0.6"
    }
``` 

Plugin setup with gradle <= 2.1:
```gradle

    buildscript {
        repositories {
            jcenter()
            maven { url "http://dl.bintray.com/amirk/maven" }
        }
        dependencies {
            classpath("ajk.gradle.redis:gradle-redis-plugin:0.0.6")
        }
    }
    
    apply plugin: 'ajk.gradle.redis'
```

# Starting and stopping Redis during integration tests

**Important**: This plugin assumes Docker is installed on the local machine. Please make sure to install Docker and
allow the user running it to issue the `docker` command without requiring `sudo`.

```gradle

    task integrationTests(type: Test) {
        reports {
            html.destination "$buildDir/reports/integration-tests"
        }

        include "**/*IT.*"

        doFirst {
            startRedis {
				redisVersion = "latest"
                port = 6379
				dataDir = file("$buildDir/redis")
            }
        }
    
        doLast {
            stopRedis {
                port = 6379
				dataDir = file("$buildDir/redis")
            }
        }
    }
    
    gradle.taskGraph.afterTask { Task task, TaskState taskState ->
        if (task.name == "integrationTests") {
            stopRedis {
                port = 6379
                dataDir = file("$buildDir/redis")
            }
        }
    }

    test {
        include '**/*Test.*'
        exclude '**/*IT.*'
    }
```

The above example shows a task called integrationTests which runs all the tests in the project with the IT suffix. The
reports for these tests are placed in the buildDir/reports/integration-tests directory - just to separate them from
regular tests. But the important part here is in the doFirst and doLast.

In the doFirst Redis is started. All the value sin the example above are the default values, so if these values work
for you they can be omitted:

```gradle

    doFirst {
        startRedis {}
    }
```

In the doLast Redis is stopped. Note that Redis is also stopped in the gradle.taskGraph.afterTask section - this is to
catch any crashes during the integration tests and make sure that Redis is stopped in the build clean-up phase.

Lastly the regular test task is configured to exclude the tests with the IT suffix - we only wanted to run these in the
integration tests phase, not with the regular tests.

# References

- [Redis Docker image](https://hub.docker.com/_/redis/)