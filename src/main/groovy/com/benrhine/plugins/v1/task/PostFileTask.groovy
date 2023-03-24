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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
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
    @Internal Message messageToSend
    @Internal String uploadUrl

    @TaskAction
    def postMessage() {

        final HttpPost post = new HttpPost(baseNoteUri + "/" + awardId + "/attachments");
        post.setHeader(AUTHORIZATION, "Bearer " + token);
        final Path path = tempDir.resolve("temp.txt");
        final List<String> lines = List.of("1", "2", "3");
        Files.write(path, lines);

        final File file = path.toFile();
        final FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
        final StringBody stringBody1 = new StringBody("Compliance", ContentType.MULTIPART_FORM_DATA);

        final MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.LEGACY);
        builder.addPart("attachment", fileBody);
        builder.addPart("attachmentType", stringBody1);
        final HttpEntity entity = builder.build();

        post.setEntity(entity);
        try (final CloseableHttpClient client = HttpClientBuilder.create().build();
             final CloseableHttpResponse response = (CloseableHttpResponse) client
                     .execute(post, new CustomHttpClientResponseHandler())){

//            assertNotNull(response);
            // SUCCESSFULLY CREATION IS SHOWN IN THE testFindAdminNoteAndAttachment TEST

            // TODO this is a bug - should return but does not
            // When
            //final JsonObject findBodyAsJson = super.find(baseNoteUri + "/" + awardId + "/sub-awards/" + subawardId + "/attachments", headers);
            // Then
            //assertNotNull(findBodyAsJson);
        }

//        final GsonBuilder builder = new GsonBuilder()
//        final Gson gson = builder.create()
//
//        if (messageName) {
//            updateUnitTestResults()
//        }
//
//        final def boundary =  'abcd' + Long.toString(System.currentTimeMillis()) * 2 + 'dcba'
//        def fileInputStream = new FileInputStream(project.buildDir.toString() + "/unitJacoco.zip" );
//        def file = new File(project.buildDir.toString() + "/unitJacoco.zip")
//        println file
//
////        final File file = path.toFile();
//        final fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
//        final stringBody1 = new StringBody("C04RVG40RTP", ContentType.MULTIPART_FORM_DATA);
//
//        final MultipartEntityBuilder builder2 = MultipartEntityBuilder.create();
//        builder2.setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
////        builder2.addPart("file", fileBody);
////        builder2.addPart("channels", "C04RVG40RTP");
//        builder2.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, file.name);
//        builder2.addTextBody("channels", "#common-security-new-artifact", ContentType.DEFAULT_BINARY);
//        def entity = builder2.build()
//
//        println entity
////        builder2.addPart("text2", stringBody2);
//        final json = gson.toJson(messageToSend)
//        final post = new URL("https://slack.com/api/files.upload").openConnection()
//        post.setRequestMethod("POST")
//        post.setDoOutput(true)
////        post.setRequestProperty("Content-Type", 'multipart/form-data; boundary=' + boundary)
//        post.setRequestProperty('Authorization', 'Bearer ' + "xapp-1-A0507P30QQK-5010394778100-0fb2e5b11291db8834036a3b2402e73fcb3dd29354bcadee04e48847818d3091")
////        post.getOutputStream().write(entity.w)
//
//        entity.writeTo {post.getOutputStream()}
//
//        final statusCode = post.getResponseCode()
//
//        if (displayLogging) {
//            println "WebHook - $messageToSend.webHook"
//            println "JSON - $json"
//            println("STATUS CODE $statusCode")
//            println(post.getInputStream().getText())
//        }
//
//        if (statusCode != 200) throw Exception(post.getInputStream().getText())
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

// https://kodejava.org/how-do-i-do-multipart-upload-using-httpclient/
//https://www.baeldung.com/httpclient-multipart-upload