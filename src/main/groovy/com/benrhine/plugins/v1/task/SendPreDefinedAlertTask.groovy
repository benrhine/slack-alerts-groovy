package com.benrhine.plugins.v1.task

import com.benrhine.plugins.v1.model.Message
import com.benrhine.plugins.v1.task.base.AlertBaseTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

class SendPreDefinedAlertTask extends AlertBaseTask {
    // Message passed in from main plugin class
    @Internal Message messageToSend

    /**
     * sendAlert: Execute the sending of an alert.
     *
     * @return void
     */
    @TaskAction
    def sendAlert() {
        // Post message to service
        postMessage(messageToSend)
    }

}
