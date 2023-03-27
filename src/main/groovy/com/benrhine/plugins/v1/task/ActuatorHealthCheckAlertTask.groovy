package com.benrhine.plugins.v1.task

import com.benrhine.plugins.v1.model.Message
import com.benrhine.plugins.v1.task.base.ActuatorBaseTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/** --------------------------------------------------------------------------------------------------------------------
 * HealthCheckAlertTask: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class ActuatorHealthCheckAlertTask extends ActuatorBaseTask {
    // Message passed in from main plugin class
    @Internal Message messageToSend

    /**
     * sendAlert: Execute the sending of an alert.
     *
     * @return void
     */
    @TaskAction
    def sendAlert() {
        // Update the messageToSend
        actuatorHealth(messageToSend)
        // Post message to service
        postMessage(messageToSend)
    }
}