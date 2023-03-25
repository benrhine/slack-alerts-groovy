package com.benrhine.plugins.v1.task

import com.benrhine.plugins.v1.model.Message
import com.benrhine.plugins.v1.util.CustomHttpClientResponseHandler
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpStatus;

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

import java.nio.file.Path

/** --------------------------------------------------------------------------------------------------------------------
 * PostFileTask: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class PostFileTask extends DefaultTask {

    @Internal boolean displayLogging
    @Internal String  messageName
    @Internal String token
    @Internal String uploadUrl
    @Internal String uploadFilePath
    @Internal String uploadFileName
    @Internal String channels

    @TaskAction
    def postMessage() {

        final HttpPost post = new HttpPost(uploadUrl)
        post.setHeader("Authorization", "Bearer $token")

        String fullPath

        if (uploadFilePath && !uploadFilePath.isEmpty()) {
            fullPath = "$uploadFilePath/$uploadFileName"
        } else {
            fullPath = "$project.buildDir/$uploadFileName"
        }

        final File file = new File(fullPath)

        if ((!file.exists()) || file.isDirectory()) {
            throw new RuntimeException("Provided file path does not exist or is a directory")
        } else {
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
            builder.setMode(HttpMultipartMode.LEGACY)
            builder.addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY))
            builder.addPart("channels", new StringBody(channels, ContentType.MULTIPART_FORM_DATA))
            builder.addPart("filename", new StringBody(uploadFileName, ContentType.MULTIPART_FORM_DATA))
            builder.addPart("filetype", new StringBody("zip", ContentType.MULTIPART_FORM_DATA))
            builder.addPart("title", new StringBody("JaCoCo Coverage Report", ContentType.MULTIPART_FORM_DATA))
            //builder.addPart("initial_comment", new StringBody("Zipped JaCoCo Unit Coverage Report", ContentType.MULTIPART_FORM_DATA))

            final HttpEntity entity = builder.build()

            post.setEntity(entity)
            try (final CloseableHttpClient client = HttpClientBuilder.create().build()
                 final CloseableHttpResponse response = (CloseableHttpResponse) client
                         .execute(post, new CustomHttpClientResponseHandler())) {

                if (displayLogging) {
                    println "Upload URL - $uploadUrl"
                    println("Returned Status code $response.code")
                }

                if (response.code != 200) {
                    throw new RuntimeException("Bad Status from Slack API returned $response.code")
                }
            }
        }
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

//    def updateUnitTestResults() {
//        messageToSend.attachments.each { attachment ->
//            attachment.color = project.unitTestBuildColor
//
//            attachment.fields.findAll { field ->
//                if (field.title == "Results") {
//                    if (project.testsUnitResults.size == 1) {
//                        field.value = project.testsUnitResults.get(0)
//                    } else {
//                        field.value = project.testsUnitResults
//                    }
//
//                }
//            }
//        }
//    }
}

// https://kodejava.org/how-do-i-do-multipart-upload-using-httpclient/
//https://www.baeldung.com/httpclient-multipart-upload