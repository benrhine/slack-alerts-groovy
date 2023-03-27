# slack-alerts

This plugin was born out of the need to easily send alert messages from the gradle build. While there are several great 
options available none of them really fit my purpose or had the flexibility to be easily configured AND be able to send 
more than static messages. This plugin supports the complete message structure for the current version of slack plus 
has additional pre-configured tasks that when declared within the DSL allow you to easily send many common alerts.

This plugin has been tested with the now deprecated Slack WebHook api as well as the currently suggested way of using a 
WebHook through a custom Slack Bot. Both configurations and file upload are fully supported.

## Including the plugin

I prefer any project I build to do the heavy lifting for me in the future, so I try to make the configuration as easy and
simple as possible for day to day use. By default, just include the plugin, and you're ready to go.
```groovy
plugins {
    id 'com.benrhine.slack-alerts-groovy' version '0.0.1'
}
```
Granted just including the plugin doesn't really do anything for you, but I've had ones I have tested previously that as 
soon as you declare them they break your config as they require additional elements to be defined that were not specified
in the documentation and are exceptionally challenging to figure out. This plugin should not cause that problem.

### Developer / Legacy plugin include
If you are using an ancient version of gradle or wish to pull this plugin down and extend it yourself
```groovy
buildscript {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    dependencies {
        classpath 'com.benrhine:slack-alerts-groovy:0.0.1'
    }
}

apply plugin: 'com.benrhine.slack-alerts-groovy'
```

## Configuration
This plugin has multiple configuration options available to you and even supports a multi DSL naming scheme. In some instances
it is possible to provide a global configuration option for some values to reduce duplicate declarations (this will be
covered in detail below). 

### Default 
By default, it is assumed you are not configuring the plugin with the global config blocks. In this case defining alert
messages to send is super easy. Start by adding the following to the `build.gradle` file.
```groovy
slackMessages {
    yourMessage1 {
        ...
    }
    yourMessage2 {
        ...
    }
}
```
Note: The above DSL will generate two tasks `sendYourMessage1Alert` and `sendYourMessage2Alert`

Every DSL block declared within the `slackMessages` block is considered a message and a unique task will be generated for
each declaration. To see all the tasks that are generated run the `./gradlew tasks` command and search for the `alerts`
group. Messages created at this point are static only as they are generated on the initial plugin run with the data
available at that time. To be perfectly clear if you are attempting to send an alert with the result from another task or
process that assigns data into a build variable and this happens at any time after the declaration of these alerts the 
data will not be included.

This seemed to be how most of the slack plugins work and I found unacceptable so see my custom [dynamic message]()
sends below.

#### IMPORTANT Note!!!
**It is important to note that due to the nature of generative task creation when / if updating an alert
name in the `slackMessages` block may sometimes cause task not found errors. This occurs because those task names are generated
when the plugin is applied and since one of those names has been updated, the previously generated name is no longer available.
This can be particularly annoying when chaining tasks later on in the build.**

##### Issue Remediation
The easiest way to fix this is to comment out any places you are programmatically calling any of the generated task names,
comment out the `slackMessages` DSL block followed by cleaning the project then refreshing gradle. After which, uncomment
the DSL and any programmatic task calls and refresh gradle once again. This will re-trigger the plugin to regenerate the
task names.

#### Message Configuration
Essentially every DSL field is optional EXCEPT `webHook` and while `text` is optional if you do not provide a value then 
the alert will be empty, and you won't know if it worked or not. Rather than use `text` in most cases I prefer `attachment`
as it gives greater control over the message layout. In order to configure a simple static message you can declare
something similar to the following ...
```groovy
slackMessages {
    applicationBuildStart {
        webHook = "https://hooks.slack.com/services/XXXXXXXXXXXXX" // or if uploading something https://slack.com/api/files.upload
        attachment {
            fallback = "$project.name build starting ..."
            pretext = "$project.name build starting ..."
            color = "warning"
            field {
                title = "Build Info"
                value = "*Version*: $project.version"
                shortValue = true   // Means value displays in half width
            }
            field {
                title = "Git Info"
                value = "*Branch*: $branch\n*Commit*: $commit"
                shortValue = true   // Means value displays in half width
            }
        }
    }
}
```
Reminder again, messages declared this way are static only so if `$branch` is not already available when plugin scans
this block the value will be empty. If you were to configure every `message` value available it would look similar to the
following ...
```groovy
slackMessages {
    applicationBuildStart {
        webHook = "https://hooks.slack.com/services/XXXXXXXXXXXXX" // or if uploading something https://slack.com/api/files.upload
        verificationUrl = "https://..."
        displayLogging = true           // Defaulted to false for clean build display
        uploadFilePath = "path to file"
        uploadFileName = "filename"
        uploadFileType = "zip"
        uploadTitle = "a better title"
        authType = "Bearer"
        environment = "dev"
        token = "oauth"
        payload = "additional field"
        retries = 37
        sleepAmountOne = 1000
        sleepAmountTwo = 500
        channels = "C0XXXXXXXXX"
        iconUrl = ""
        iconEmoji = ""
        text = ""
        username = ""
        threadTs = ""
        mrkdwn = ""
        attachment {
            fallback = "$project.name build starting ..."
            pretext = "$project.name build starting ..."
            color = "warning"
            field {
                title = "Build Info"
                value = "*Version*: $project.version"
                shortValue = true
            }
            field {
                title = "Git Info"
                value = "*Branch*: $branch\n*Commit*: $commit"
                shortValue = true
            }
        }
        blocks {
            
        }
    }
}
```

### Global
As mentioned above there are two ways to configure this plugin, by message or by using the global configuration blocks.
```groovy
slackConfig {
    environment = ""        // (Optional) Define what environment an alert is coming from
    webHook = ""            // (Optional) Define the webHook url
    uploadUrl = ""          // (Optional) Define the upload url
    token = ""              // (Optional) Define the auth token for a slack bot
    channels = ""           // (Optional) Define channel or channels
    payload = ""            // (Optional) Additional payload
    displayLogging = ""     // (Optional) Define if logging is enabled
}
```

#### environment (Optional)
Use this to define what environment the alert is coming from, since the environment is normally available at the very
start of the build it should be easy to programmatically define this value and have it available to any of the alerts.

#### webHook (Optional)
By defining a `webHook` in the config block you no longer have to define it for each individual message as it will 
automatically set for each message. If you define a message with its own `webHook` that value will be preferred and will
NOT be overridden.

#### uploadUrl (Optional)
It is suggested you set this in the config block as Slack has a universal upload url and there is no reason to define it
on a per-message basis.

#### token (Optional)
This would be your bot token, if this is not defined then it is not possible to upload files into slack

#### channels (Optional)
This allows you to set a default channel that everything will be sent to if you want it to be different that what was 
defined when the `webHook` and or bot was configured

#### payload (Optional)
Just an extra field for right now

#### displayLogging (Optional)
Also suggested you set this in the config block. While this can be configured on a per-message basis it is easier to do
globally.

## Dynamically Generated Alerts
Having the ability to send static messages can be great, it is much more likely you want the ability to programmatically
build your alert messages. Unfortunately, in order to accomplish this you will likely need to fork this plugin if the
following does not cover your use cases. While these names are dynamically generated they work by checking if a task name
`contains` a value to allow some flexibility when naming your dsl blocks. Thus, you can add values before or
after the following parts but the following parts must be part of the dsl message name in order for the pre-defined
generative tasks to be created.
#### List of what contains checks for
- unitTest
- intTest
- loadTest
- authenticatedSmokeTest
- unauthenticatedSmokeTest
- validationSmokeTest
- applicationHealthCheck
- applicationInfo

### sendUnitTestAlert (generated Gradle task name)
Used to send an alert that unit tests have passed

```groovy
unitTestSuccess {
    channels = "C04RVG40RTP"								// Currently only used for upload
    uploadFileName = "unitJacoco.zip"						// Only used for upload
    attachment {
        fallback = "Unit tests successfully completed."
        pretext = "Unit tests successfully completed."
        color = unitTestBuildColor
        field {
            title = "Application"
            value = project.name
            shortValue = true
        }
        field {
            title = "Version"
            value = project.version
            shortValue = true
        }
        field {
            title = "Branch"
            value = "$branch"
            shortValue = true
        }
        field {
            title = "Commit"
            value = "$commit"
            shortValue = true
        }
    }
    attachment {
        color = unitTestBuildColor
        field {
            title = "Results"
            value = "N / A"
            shortValue = false
        }
    }
}
```

### sendUnitTestResult (generated Gradle task name)
Upload the results of the unit test run. This requires the above dsl for [sendUnitTestAlert](#sendunittestalert--generated-gradle-task-name-)
to be defined AND to include the `channels` and `uploadFileName` values. If no `channels` value is set nothing
will be sent, and you will receive the following message "No channel(s) set, nothing will be sent". If no `uploadFileName`
is provided an exception will be thrown with the message "Provided file path does not exist or is a directory".

### sendIntTestAlert (generated Gradle task name)
Used to send an alert that integration tests have passed

```groovy
intTestSuccess {
    channels = "C04RVG40RTP"
    uploadFileName = "intJacoco.zip"
    attachment {
        fallback = "Integration tests successfully completed."
        pretext = "Integration tests successfully completed."
        color = "$intTestBuildColor"
        field {
            title = "Application"
            value = project.name
            shortValue = true
        }
        field {
            title = "Version"
            value = project.version
            shortValue = true
        }
        field {
            title = "Branch"
            value = "$branch"
            shortValue = true
        }
        field {
            title = "Commit"
            value = "$commit"
            shortValue = true
        }
    }
    attachment {
        color = "$intTestBuildColor"
        field {
            title = "Results"
            value = "Something text"
        }
    }
}
```

### sendIntTestResult (generated Gradle task name)
Upload the results of the int test run. This requires the above dsl for [sendIntTestAlert](#sendinttestalert--generated-gradle-task-name-)
to be defined AND to include the `channels` and `uploadFileName` values. If no `channels` value is set nothing
will be sent, and you will receive the following message "No channel(s) set, nothing will be sent". If no `uploadFileName`
is provided an exception will be thrown with the message "Provided file path does not exist or is a directory".

### sendLoadTestAlert (generated Gradle task name)
Used to send an alert that load tests have passed

### sendAuthenticatedSmokeTestAlert (generated Gradle task name)
Used to send an alert that authenticated smoke tests have passed

### sendValidationSmokeTestAlert (generated Gradle task name)
Used to send an alert that validation smoke tests have passed

### sendUnauthenticatedSmokeTestAlert (generated Gradle task name)
Used to send an alert that unauthenticated smoke tests have passed

### sendApplicationHealthCheckAlert (generated Gradle task name)
Used to send an alert on if the application started up correctly or not, checks both the http status
and if the result contains the word "UP".

```groovy
applicationHealthCheck {
    verificationUrl = "http://localhost:7001/actuator/health"
    attachment {
        color = "danger"
        field {
            title = "Application Status: N / A"
            value = "N / A"
            shortValue = false
        }
    }
    attachment {
        color = "danger"
        field {
            title = "QA Status: N / A"
            value = "N / A"
            shortValue = false
        }
    }
}
```

### sendApplicationInfoAlert (generated Gradle task name)
Used to send an alert on the application information. I normally use this in conjunction with a call to
[sendApplicationHealthCheckAlert](#sendapplicationhealthcheckalert--generated-gradle-task-name-) and call this immediately after.

```groovy
applicationInfo {
    verificationUrl = "http://localhost:7001/actuator/info"
    attachment {
        color = "danger"
        field {
            title = "Application Info: N / A"
            value = "N / A"
            shortValue = false
        }
        field {
            title = "Build Info"
            value = "N / A"
            shortValue = true
        }
        field {
            title = "Git Info"
            value = "N / A"
            shortValue = true
        }
    }
}
```

## Known Issues
I don't know for certain that this is an issue but when attempting to chain the alerts for integration testing
```groovy
integrationTest.finalizedBy sendIntTestAlert
sendIntTestAlert.finalizedBy sendIntTestResults
```
The first alert sends fine and I can see on the console that the results are posted and return a 200 but never are seen
in the Slack channel. This is set up exactly like the unit tests which work flawlessly, so I don't understand. To add
to the confusion if `sendIntTestResults` is called independently then it uploads the file exactly as expected. I thought
this may be a timing issue and tried inducing some waits but that did not change the behavior. 

In short, call `sendIntTestResults` independently, and you will be fine - if you can get around the chaining issue or see
a mistake I made please let me know.

## My Complete DSL
This is the full Slack DSL as I have it defined in my example projects

### Global Config
```groovy
slackConfig {
	webHook = System.env.SLACK_WEBHOOK
	uploadUrl = System.env.SLACK_UPLOAD
	displayLogging = true
	token  = System.env.SLACK_TOKEN
}
```

### Messages
```groovy
slackMessages {
	applicationBuildStart {
		attachment {
			fallback = "$project.name build starting ..."
			pretext = "$project.name build starting ..."
			color = "good"  // Default to 'good' as build is just starting
			field {
				title = "Build Info"
				value = "*Version*: $project.version"
				shortValue = true
			}
			field {
				title = "Git Info"
				value = "*Branch*: $branch\n*Commit*: $commit"
				shortValue = true
			}
		}
	}
	unitTest {
		channels = "C04RVG40RTP"								// (Required) Currently only used for upload
		uploadFileName = "unitJacoco.zip"						// (Required) Only used for upload
		uploadFileType = "zip"									// (Optional) Only used for upload
		uploadTitle = "JaCoCo Coverage Report"					// (Optional) Only used for upload
		attachment {
			fallback = "Unit tests successfully completed."
			pretext = "Unit tests successfully completed."
			color = buildColor
			field {
				title = "Application"
				value = project.name
				shortValue = true
			}
			field {
				title = "Version"
				value = project.version
				shortValue = true
			}
			field {
				title = "Branch"
				value = "$branch"
				shortValue = true
			}
			field {
				title = "Commit"
				value = "$commit"
				shortValue = true
			}
		}
		attachment {
			color = buildColor
			field {
				title = "Results"
				value = "N / A"
				shortValue = false
			}
		}
	}
	intTest {
		channels = "C04RVG40RTP"								// (Required) Currently only used for upload
		uploadFileName = "intJacoco.zip"						// (Required) Only used for upload
		uploadFileType = "zip"									// (Optional) Only used for upload
		uploadTitle = "JaCoCo Coverage Report"					// (Optional) Only used for upload
		attachment {
			fallback = "Integration tests successfully completed."
			pretext = "Integration tests successfully completed."
			color = buildColor
			field {
				title = "Application"
				value = project.name
				shortValue = true
			}
			field {
				title = "Version"
				value = project.version
				shortValue = true
			}
			field {
				title = "Branch"
				value = "$branch"
				shortValue = true
			}
			field {
				title = "Commit"
				value = "$commit"
				shortValue = true
			}
		}
		attachment {
			color = buildColor
			field {
				title = "Results"
				value = "Something text"
			}
		}
	}
	loadTest {
		attachment {
			fallback = "Load tests successfully completed."
			pretext = "Load tests successfully completed."
			color = buildColor
			field {
				title = "Application"
				value = project.name
				shortValue = true
			}
			field {
				title = "Version"
				value = project.version
				shortValue = true
			}
			field {
				title = "Branch"
				value = "$branch"
				shortValue = true
			}
			field {
				title = "Commit"
				value = "$commit"
				shortValue = true
			}
		}
		attachment {
			color = buildColor
			field {
				title = "Results"
				value = "N / A"
				shortValue = false
			}
		}
	}
	authenticatedSmokeTest {
		attachment {
			fallback = "Authenticated smoke tests successfully completed."
			pretext = "Authenticated smoke tests successfully completed."
			color = buildColor
			field {
				title = "Application"
				value = project.name
				shortValue = true
			}
			field {
				title = "Version"
				value = project.version
				shortValue = true
			}
			field {
				title = "Branch"
				value = "$branch"
				shortValue = true
			}
			field {
				title = "Commit"
				value = "$commit"
				shortValue = true
			}
		}
		attachment {
			color = buildColor
			field {
				title = "Results"
				value = "N / A"
				shortValue = false
			}
		}
	}
	unauthenticatedSmokeTest {
		attachment {
			fallback = "Unauthenticated smoke tests successfully completed."
			pretext = "Unauthenticated smoke tests successfully completed."
			color = buildColor
			field {
				title = "Application"
				value = project.name
				shortValue = true
			}
			field {
				title = "Version"
				value = project.version
				shortValue = true
			}
			field {
				title = "Branch"
				value = "$branch"
				shortValue = true
			}
			field {
				title = "Commit"
				value = "$commit"
				shortValue = true
			}
		}
		attachment {
			color = buildColor
			field {
				title = "Results"
				value = "N / A"
				shortValue = false
			}
		}
	}
	validationSmokeTest {
		attachment {
			fallback = "Validation smoke tests successfully completed."
			pretext = "Validation smoke tests successfully completed."
			color = buildColor
			field {
				title = "Application"
				value = project.name
				shortValue = true
			}
			field {
				title = "Version"
				value = project.version
				shortValue = true
			}
			field {
				title = "Branch"
				value = "$branch"
				shortValue = true
			}
			field {
				title = "Commit"
				value = "$commit"
				shortValue = true
			}
		}
		attachment {
			color = buildColor
			field {
				title = "Results"
				value = "N / A"
				shortValue = false
			}
		}
	}
	applicationBuildComplete {
		attachment {
			fallback = "$project.name build complete ..."
			pretext = "$project.name build complete ..."
			color = "warning"
			field {
				title = "Build Info"
				value = "*Version*: $project.version"
				shortValue = true
			}
			field {
				title = "Git Info"
				value = "*Branch*: $branch\n*Commit*: $commit"
				shortValue = true
			}
		}
	}
	applicationHealthCheck {
		verificationUrl = "http://localhost:7001/actuator/health"
		attachment {
			color = "danger"
			field {
				title = "Application Status: N / A"
				value = "N / A"
				shortValue = false
			}
		}
		attachment {
			color = "danger"
			field {
				title = "QA Status: N / A"
				value = "N / A"
				shortValue = false
			}
		}
	}
	applicationInfo {
		verificationUrl = "http://localhost:7001/actuator/info"
		attachment {
			color = "danger"
			field {
				title = "Application Info: N / A"
				value = "N / A"
				shortValue = false
			}
			field {
				title = "Build Info"
				value = "N / A"
				shortValue = true
			}
			field {
				title = "Git Info"
				value = "N / A"
				shortValue = true
			}
		}
	}
}
```


##### Resources
- https://api.slack.com/docs/messages

