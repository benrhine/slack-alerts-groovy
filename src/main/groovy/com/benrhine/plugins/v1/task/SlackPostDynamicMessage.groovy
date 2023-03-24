package com.benrhine.plugins.v1.task

import com.benrhine.plugins.v1.model.Message
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/** --------------------------------------------------------------------------------------------------------------------
 * SlackPostDynamicMessage: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class SlackPostDynamicMessage extends DefaultTask {

    @Internal boolean displayLogging
    @Internal String  messageName
    @Internal Message messageToSend
    @Internal String uploadUrl

    @TaskAction
    def postMessage() {

        final GsonBuilder builder = new GsonBuilder()
        final Gson gson = builder.create()

        if (messageName) {
            updateUnitTestResults()
        }

        final json = gson.toJson(messageToSend)
        final post = new URL(messageToSend.webHook).openConnection()
        post.setRequestMethod("POST")
        post.setDoOutput(true)
        post.setRequestProperty("Content-Type", "application/json")
        post.getOutputStream().write(json.getBytes("UTF-8"))

        final statusCode = post.getResponseCode()

        if (displayLogging) {
            println "WebHook - $messageToSend.webHook"
            println "JSON - $json"
            println("STATUS CODE $statusCode")
            println(post.getInputStream().getText())
        }

        if (statusCode != 200) throw Exception(post.getInputStream().getText())
    }

//    def postMessage2() {
//        def fileInputStream = new FileInputStream(file);
//        def json = '{"path":"' + file.getName() + '","contentHash":"SHA-256{' + hash +'}","version":"' + version + '","type":"REGULAR","length":"' + file.length() + '"}'
//
//        def connection = new URL(url).openConnection() as HttpsURLConnection
//        connection.setDoInput(true)
//        connection.setDoOutput(true)
//        connection.setUseCaches(false)
//        connection.setRequestMethod('POST')
//        connection.setRequestProperty('Connection', 'Keep-Alive');
//        connection.setRequestProperty('Authorization', 'Basic ' + "xapp-1-A0507P30QQK-5010394778100-0fb2e5b11291db8834036a3b2402e73fcb3dd29354bcadee04e48847818d3091")
//        connection.setRequestProperty('Content-Type' , 'multipart/form-data; boundary=' + boundary)
//        def outputStream = new DataOutputStream(connection.getOutputStream())
//    }

    def updateUnitTestResults() {
        messageToSend.attachments.each { attachment ->
            attachment.color = project.unitTestBuildColor

            attachment.fields.findAll { field ->
                if (field.title == "Results") {
                    if (project.testsUnitResults.size == 1) {
                        field.value = project.testsUnitResults.get(0)
                    } else {
                        field.value = project.testsUnitResults
                    }

                }
            }
        }
    }
}
