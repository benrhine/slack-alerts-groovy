package com.benrhine.plugins.v1

import com.benrhine.plugins.v1.task.ActuatorHealthCheckAlertTask
import com.benrhine.plugins.v1.task.ActuatorInfoAlertTask
import com.benrhine.plugins.v1.task.SendDynamicAlertTask
import com.benrhine.plugins.v1.task.SendPreDefinedAlertTask
import com.benrhine.plugins.v1.task.PostFileTask
import com.benrhine.plugins.v1.model.Message
import org.gradle.api.Plugin
import org.gradle.api.Project

/** --------------------------------------------------------------------------------------------------------------------
 * SlackAlertsPlugin: Main plugin class.
 * ------------------------------------------------------------------------------------------------------------------ */
class SlackAlertsPlugin implements Plugin<Project> {
    // Declared DSL block titles
    static final String SLACK_CONFIG  = "slackConfig"
    static final String SLACK_MESSAGES = "slackMessages"

    /**
     * apply: Applies plugin to the implementing project.
     *
     * @param project
     */
    @Override
    void apply(final Project project) {
        // I have struggled to find any real documentation on how this line works - essentially it seems to be saying
        // you can have a wrapper DSL around a certain type of object
        final messages = project.container(Message)
        // Declare the plugin config DSL
        final config = project.extensions.create(SLACK_CONFIG, SlackAlertsPluginExtension)
        // Rather than create, add the messages container DSL
        project.extensions.add(SLACK_MESSAGES, messages)
        // Iterate over each message that is declared within the DSL and operate on it
        messages.configureEach { message ->
            // Generate tasks from DSL just as they are - no dynamic values. This will generate a predefined
            // task and message for each "message" blocks defined within the "slackMessages" container
            project.tasks.register("sendPredefined${message.name.capitalize()}Alert", SendPreDefinedAlertTask) { task ->
                messageToSend = configToTask(config, message)

                group 'alerts'
                description "Publish ${message.name} alert"
            }
            // Alerts for unit tests
            if (message.name.contains("unitTest")) {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
                project.tasks.register("send${message.name.capitalize()}Results", PostFileTask.class) { task ->
                    if (config.displayLogging && !message.displayLogging) {
                        displayLogging = config.displayLogging
                    } else {
                        displayLogging = message.displayLogging
                    }
                    if (config.uploadUrl) {
                        webHook = config.uploadUrl
                    } else {
                        println "Upload url was not set - this call will fail"
                    }
                    if (config.token) {
                        token = config.token
                    } else {
                        println "Auth token was not set - this call will fail"
                    }
                    if (message.uploadFilePath) {
                        uploadFilePath = message.uploadFilePath
                    }
                    if (message.uploadFileName) {
                        uploadFileName = message.uploadFileName
                    }
                    if (message.uploadFileType) {
                        uploadFileType = message.uploadFileType
                    }
                    if (message.uploadTitle) {
                        uploadTitle = message.uploadTitle
                    }
                    if (message.channels) {
                        channels = message.channels
                    }

                    group 'alerts'
                    description "Publish ${message.name} JaCoCo Coverage Results"
                }
            }
            // Alerts for integration tests
            if (message.name.contains("intTest")) {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
                project.tasks.register("send${message.name.capitalize()}Results", PostFileTask.class) { task ->
                    if (config.displayLogging && !message.displayLogging) {
                        message.displayLogging = config.displayLogging
                    } else {
                        displayLogging = message.displayLogging
                    }
                    if (config.uploadUrl) {
                        webHook = config.uploadUrl
                    } else {
                        println "Upload url was not set - this call will fail"
                    }
                    if (config.token) {
                        token = config.token
                    } else {
                        println "Auth token was not set - this call will fail"
                    }
                    if (message.uploadFilePath) {
                        uploadFilePath = message.uploadFilePath
                    }
                    if (message.uploadFileName) {
                        uploadFileName = message.uploadFileName
                    }
                    if (message.uploadFileType) {
                        uploadFileType = message.uploadFileType
                    }
                    if (message.uploadTitle) {
                        uploadTitle = message.uploadTitle
                    }
                    if (message.channels) {
                        channels = message.channels
                    }

                    group 'alerts'
                    description "Publish ${message.name} JaCoCo Coverage Results"
                }
            }
            // Alerts for load tests
            if (message.name.contains("loadTest")) {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
            }
            // Alerts for authenticated smoke tests
            if (message.name.contains("authenticatedSmokeTest")) {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
            }
            // Alerts for unauthenticated smoke tests
            if (message.name.contains("unauthenticatedSmokeTest")) {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
            }
            // Alerts for validation smoke tests
            if (message.name.contains("validationSmokeTest")) {
                project.tasks.register("send${message.name.capitalize()}Alert", SendDynamicAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
            }
            // Alerts for application health status
            if (message.name.contains("applicationHealthCheck")) {
                project.tasks.register("send${message.name.capitalize()}Alert", ActuatorHealthCheckAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
            }
            // Alerts for application info
            if (message.name.contains("applicationInfo")) {
                project.tasks.register("send${message.name.capitalize()}Alert", ActuatorInfoAlertTask.class) { task ->
                    messageToSend = configToTask(config, message)

                    group 'alerts'
                    description "Publish dynamic ${message.name} alert"
                }
            }
        }
    }

    /**
     * configToTask: Maps values from the global config to the task config.
     *
     * @param config
     * @param message
     * @return
     */
    private static Message configToTask(final config, final message) {
        if (config.displayLogging && !message.displayLogging) {
            message.displayLogging = config.displayLogging
        }
        if (config.webHook && !message.webHook) {
            message.webHook = config.webHook
        }
        return message
    }

}