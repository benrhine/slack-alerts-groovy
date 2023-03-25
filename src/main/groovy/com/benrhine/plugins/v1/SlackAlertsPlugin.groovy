package com.benrhine.plugins.v1

import com.benrhine.plugins.v1.task.SendDynamicAlertTask
import com.benrhine.plugins.v1.task.SendPreDefinedAlertTask
import com.benrhine.plugins.v1.task.PostFileTask
import com.benrhine.plugins.v1.model.Message
import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackAlertsPlugin implements Plugin<Project> {

    static final String ALERT_CONFIG  = "alertConfig"
    static final String ALERT_MESSAGES = "alertMessages"
    static final String SLACK_CONFIG  = "slackConfig"
    static final String SLACK_MESSAGES = "slackMessages"

    @Override
    void apply(final Project project) {
        final messages = project.container(Message)

        final config = project.extensions.create(ALERT_CONFIG, SlackAlertsPluginExtension)
        final slackConfig = project.extensions.create(SLACK_CONFIG, SlackAlertsPluginExtension)


        project.extensions.add(ALERT_MESSAGES, messages)

        messages.configureEach { message ->
            project.tasks.register("sendPredefined${message.name.capitalize()}Alert", SendPreDefinedAlertTask) { task ->
                displayLogging = config.displayLogging
                if (config.webHook && !message.webHook) {
                    message.webHook = config.webHook
                }
                if (config.uploadUrl) {
                    message.uploadUrl = config.uploadUrl
                }
                messageToSend = message

                group 'slack'
                description "Publish ${message.name} message to slack"
            }
            // Alerts for unit tests
            if (message.name == "unitTestSuccess") {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.webHook && !message.webHook) {
                        message.webHook = config.webHook
                    }
                    if (config.uploadUrl) {
                        message.uploadUrl = config.uploadUrl
                    }
                    messageName   = message.name
                    messageToSend = message

                    group 'slack'
                    description "Publish dynamic ${message.name} message to slack"
                }
                project.tasks.register("send${message.name.capitalize()}Results", PostFileTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.uploadUrl) {
                        uploadUrl = config.uploadUrl
                    }
                    if (config.token) {
                        token = config.token
                    }
                    if (message.uploadFilePath) {
                        uploadFilePath = message.uploadFilePath
                    }
                    if (message.uploadFileName) {
                        uploadFileName = message.uploadFileName
                    }
                    if (message.channels) {
                        channels = message.channels
                    }

                    group 'slack'
                    description "Publish ${message.name} JaCoCo Coverage Results"
                }
            }
            // Alerts for integration tests
            if (message.name == "intTestSuccess") {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.webHook && !message.webHook) {
                        message.webHook = config.webHook
                    }
                    if (config.uploadUrl) {
                        message.uploadUrl = config.uploadUrl
                    }
                    messageName   = message.name
                    messageToSend = message

                    group 'slack'
                    description "Publish dynamic ${message.name} message to slack"
                }
                project.tasks.register("send${message.name.capitalize()}Results", PostFileTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.uploadUrl) {
                        uploadUrl = config.uploadUrl
                    }
                    if (config.token) {
                        token = config.token
                    }
                    if (message.uploadFilePath) {
                        uploadFilePath = message.uploadFilePath
                    }
                    if (message.uploadFileName) {
                        uploadFileName = message.uploadFileName
                    }
                    if (message.channels) {
                        channels = message.channels
                    }

                    group 'slack'
                    description "Publish ${message.name} JaCoCo Coverage Results"
                }
            }
            // Alerts for load tests
            if (message.name == "loadTestSuccess") {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.webHook && !message.webHook) {
                        message.webHook = config.webHook
                    }
                    if (config.uploadUrl) {
                        message.uploadUrl = config.uploadUrl
                    }
                    messageName = message.name
                    messageToSend = message

                    group 'slack'
                    description "Publish dynamic ${message.name} message to slack"
                }
            }
            // Alerts for authenticated smoke tests
            if (message.name == "authenticatedSmokeTestSuccess") {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.webHook && !message.webHook) {
                        message.webHook = config.webHook
                    }
                    if (config.uploadUrl) {
                        message.uploadUrl = config.uploadUrl
                    }
                    messageName = message.name
                    messageToSend = message

                    group 'slack'
                    description "Publish dynamic ${message.name} message to slack"
                }
            }
            // Alerts for unauthenticated smoke tests
            if (message.name == "unauthenticatedSmokeTestSuccess") {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.webHook && !message.webHook) {
                        message.webHook = config.webHook
                    }
                    if (config.uploadUrl) {
                        message.uploadUrl = config.uploadUrl
                    }
                    messageName = message.name
                    messageToSend = message

                    group 'slack'
                    description "Publish dynamic ${message.name} message to slack"
                }
            }
            // Alerts for validation smoke tests
            if (message.name == "validationSmokeTestSuccess") {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    displayLogging = config.displayLogging
                    if (config.webHook && !message.webHook) {
                        message.webHook = config.webHook
                    }
                    if (config.uploadUrl) {
                        message.uploadUrl = config.uploadUrl
                    }
                    messageName = message.name
                    messageToSend = message

                    group 'slack'
                    description "Publish dynamic ${message.name} message to slack"
                }
            }
        }

//        project.gradle.afterProject {
//
//            final slack = project.extensions.getByName("slack")
//            slack.each {
//                println it.name
//                println it.payload
//            }
//
//        }

//        project.tasks.register("dynamicPost", SlackPostDynamicMessage.class)
    }

}