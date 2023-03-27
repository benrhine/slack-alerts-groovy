package com.benrhine.plugins.v1.task

import com.benrhine.plugins.v1.model.Message
import com.benrhine.plugins.v1.task.base.AlertBaseTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/** --------------------------------------------------------------------------------------------------------------------
 * SlackPostDynamicMessage: Allows for statically defined messages from the DSL to be updated.
 * ------------------------------------------------------------------------------------------------------------------ */
class SendDynamicAlertTask extends AlertBaseTask {
    // Message passed in from main plugin class
    @Internal Message messageToSend

    /**
     * sendAlert: Execute the update and sending of an alert.
     *
     * @return void
     */
    @TaskAction
    def sendAlert() {
        // Get the result of tests to send
        updateTestResults()
        // Post message to service
        postMessage(messageToSend)
    }

    /**
     * updateTestResults: Update the result area of matching messages.
     *
     * @return void
     */
    def updateTestResults() {
        if (messageToSend.name.contains("unitTest") || messageToSend.name.contains("intTest") ||
                messageToSend.name.contains("loadTest") || messageToSend.name.contains("authenticatedSmokeTest") ||
                messageToSend.name.contains("unauthenticatedSmokeTest") || messageToSend.name.contains("validationSmokeTest")) {
            messageToSend.attachments.each { attachment ->
                attachment.color = project.buildColor

                attachment.fields.findAll { field ->
                    if (field.title == "Results") {
                        if (project.testResults.size == 1) {
                            field.value = project.testResults.get(0)
                        } else {
                            field.value = project.testResults
                        }
                    }
                }
            }
        }

        if (messageToSend.name.contains("applicationBuildComplete")) {
            messageToSend.attachments.each { attachment ->
                attachment.color = project.buildColor
            }
        }
    }
}
