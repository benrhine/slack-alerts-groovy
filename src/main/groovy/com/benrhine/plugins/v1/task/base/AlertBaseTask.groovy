package com.benrhine.plugins.v1.task.base

import com.benrhine.plugins.v1.model.Message
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask

/** --------------------------------------------------------------------------------------------------------------------
 * AlertBaseTask: Common methods for all tasks.
 * ------------------------------------------------------------------------------------------------------------------ */
class AlertBaseTask extends DefaultTask {
    /**
     * postMessage: Execute the actual post of a given message.
     *
     * @param messageToSend
     */
    def postMessage(final Message messageToSend) {
        final GsonBuilder builder = new GsonBuilder()
        final Gson gson = builder.create()

        final json = gson.toJson(messageToSend)
        final post = new URL(messageToSend.webHook).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "application/json")
        post.getOutputStream().write(json.getBytes("UTF-8"))

        final statusCode = post.getResponseCode()

        if (messageToSend.displayLogging) {
            println "WebHook - $messageToSend.webHook"
            println "JSON - $json"
            println("Returned Status code $statusCode")
            println(post.getInputStream().getText())
        }

        if (statusCode != 200) {
            throw new Exception(post.getInputStream().getText())
        }
    }
}
