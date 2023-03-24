package com.benrhine.plugins.v1

import com.benrhine.plugins.v1.task.SlackPostDynamicMessage
import com.benrhine.plugins.v1.task.SlackTask
import com.benrhine.plugins.v1.task.PostFileTask
import com.benrhine.plugins.v1.model.Message
import org.gradle.api.Plugin
import org.gradle.api.Project

class SlackAlertsPlugin implements Plugin<Project> {

    @Override
    void apply(final Project project) {
        final messages = project.container(Message)

        final config = project.extensions.create("slackConfig", SlackAlertsPluginExtension)


        project.extensions.add("slackMessages", messages)

        messages.configureEach { message ->
            project.tasks.register("post${message.name.capitalize()}ToSlack", SlackTask) { task ->
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
            if (message.name == "unitTestSuccess") {
                project.tasks.register("postDynamic${message.name.capitalize()}ToSlack", SlackPostDynamicMessage.class) { task ->
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
            }
            if (message.name == "intTestSuccess") {
                project.tasks.register("postDynamic${message.name.capitalize()}ToSlack", SlackPostDynamicMessage.class) { task ->
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
            }
        }

        project.tasks.register("uploadResults", PostFileTask)

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