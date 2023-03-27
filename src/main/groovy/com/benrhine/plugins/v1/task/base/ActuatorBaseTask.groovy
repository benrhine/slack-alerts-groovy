package com.benrhine.plugins.v1.task.base

import com.benrhine.plugins.v1.model.Message
import groovy.json.JsonSlurper

/** --------------------------------------------------------------------------------------------------------------------
 * ActuatorBaseTask: Additional common methods when sending alerts that require outside endpoints to be present.
 * ------------------------------------------------------------------------------------------------------------------ */
class ActuatorBaseTask extends AlertBaseTask {
    /**
     * actuatorHealth: Check the actuator health endpoint and verify if the application is UP.
     *
     * @param messageToSend
     * @return
     */
    protected actuatorHealth(final Message messageToSend) {
        def responseCode = null
        def responseCodeStatus = "DOWN"
        def tryCount = 0

        while(responseCode != 200 && tryCount < messageToSend.retries) {
            tryCount++
            sleep messageToSend.sleepAmountOne
            try {
                final req = messageToSend.verificationUrl.toURL().openConnection()
                responseCode = req.getResponseCode()
                final healthInfo = readFullyAsString(req.getInputStream(), "UTF-8")
                if (messageToSend.displayLogging) {
                    println "Response returned from service $responseCode"
                    println "Response Body returned from service $healthInfo"
                }

                if (healthInfo.contains("UP")) {
                    responseCodeStatus = "UP"
                }
            } catch(final Exception e) {
                println "Failed to connect to service"
                println e.getMessage()
            }
        }

        if (messageToSend.includeInfo) {

        }
        healthStatus(messageToSend, responseCode, responseCodeStatus)
    }

    /**
     * healthStatus: Helper method.
     * @param messageToSend
     * @param responseCodeHealth
     * @param responseCodeStatus
     * @return
     */
    private final healthStatus(final Message messageToSend, final responseCodeHealth, final String responseCodeStatus) {
        String derivedStatusQA
        String derivedStatusColor
        String derivedStatusDescription
        String derivedStatusDescriptionQA

        if (responseCodeStatus.equalsIgnoreCase("UP")) {
            derivedStatusQA = "READY"
            derivedStatusColor = "good"
            derivedStatusDescription = "Actuator health check responded with a $responseCodeHealth code. *Application is now available*"
            derivedStatusDescriptionQA = "QA TESTING CAN START AT THIS POINT (Be aware automated tests may still be running in the background)"
        }

        if (responseCodeStatus.equalsIgnoreCase("DOWN")) {
            derivedStatusQA = "NOT READY"
            derivedStatusColor = "danger"
            derivedStatusDescription = "Actuator health check responded with a $responseCodeHealth code. *Application is unavailable*\n" +
                    "*Project*: $project.name\n*Version*: $project.version"
            derivedStatusDescriptionQA = "QA TESTING SHOULD NOT BE STARTED YET!!!"
        }

        messageToSend.attachments.each { attachment ->
            println attachment.fields.size
            attachment.color = derivedStatusColor

            attachment.fields.findAll { field ->
                println field.title
                if (field.title.contains("Application")) {
                    field.title = "Application Status: $responseCodeStatus"
                    field.value = derivedStatusDescription
                }
                if (field.title.contains("QA")) {
                    field.title = "QA Status: $derivedStatusQA"
                    field.value = derivedStatusDescriptionQA
                }
            }
        }
    }

    /**
     * actuatorInfo: Check the actuator info endpoint and return information about the application.
     *
     * @param messageToSend
     * @return
     */
    protected actuatorInfo(final Message messageToSend) {
        final JsonSlurper jsonSlurper               = new JsonSlurper()
        def responseCode = null
        def tryCount = 0
        def response = null

        while(responseCode != 200 && tryCount < messageToSend.retries) {
            tryCount++
            sleep messageToSend.sleepAmountOne
            try {
                final req = messageToSend.verificationUrl.toURL().openConnection()
                responseCode = req.getResponseCode()
                final deployInfo = readFullyAsString(req.getInputStream(), "UTF-8")
                println deployInfo
                response = jsonSlurper.parseText(deployInfo)
                println "Response returned from service $responseCode"
            } catch (final Exception e) {
                println "Failed to connect to service"
                println e.getMessage()
            }
        }
        applicationInfo(messageToSend, responseCode, response)
    }

    /**
     * applicationInfo: Helper method.
     *
     * @param messageToSend
     * @param responseCode
     * @param response
     * @return
     */
    private applicationInfo(final Message messageToSend, final responseCode, final response) {
        String derivedStatusColor
        String derivedStatus
        String buildTime
        String buildInfo
        String gitInfo

        if (responseCode == 200) {
            derivedStatusColor = "good"
            derivedStatus = "Available"
        } else {
            derivedStatusColor = "danger"
            derivedStatus = "Unavailable"
        }

        try {
            gitInfo = "*Branch:* ${response.git.branch}\n*Commit:* ${response.git.commit.id}"
            buildInfo = "*Group:* ${response.build.group}\n*Artifact:* ${response.build.artifact}\n*Version:* ${response.build.version}"

            long millis = response.build.time.longValue()
//            long minutes = (millis / 1000) / 60 / 60
//            long seconds = (minutes / 60) / 60
            buildTime = " *Time to build:* $millis ms (This is the time reported by info endpoint)"
        } catch (final Exception e) {
            println "Failed to build message body"
            println e.getMessage()
        }

        messageToSend.attachments.each { attachment ->
            attachment.color = derivedStatusColor

            attachment.fields.findAll { field ->
                if (field.title.contains("Application")) {
                    field.title = "Application Info: $derivedStatus"
                    field.value = buildTime
                }
                if (field.title.contains("Build")) {
                    field.value = buildInfo
                }
                if (field.title.contains("Git")) {
                    field.value = gitInfo
                }
            }
        }
    }

    /**
     * readFullyAsString: Helper method.
     * - https://stackoverflow.com/questions/20976013/how-to-read-full-response-from-httpurlconnection
     * @param inputStream
     * @param encoding
     * @return
     * @throws IOException
     */
    protected String readFullyAsString(final InputStream inputStream, final String encoding) throws IOException {
        return readFully(inputStream).toString(encoding);
    }

    /**
     * readFully: Helper method.
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    protected ByteArrayOutputStream readFully(final InputStream inputStream) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream()
        final byte[] buffer = new byte[1024]
        int length = 0
        while ((length = inputStream.read(buffer)) != -1) {
            baos.write(buffer, 0, length)
        }
        return baos
    }
}
