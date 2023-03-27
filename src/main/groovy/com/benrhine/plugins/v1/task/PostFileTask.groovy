package com.benrhine.plugins.v1.task


import com.benrhine.plugins.v1.util.CustomHttpClientResponseHandler
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.FileBody;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.entity.mime.StringBody;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;

import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpEntity
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

/** --------------------------------------------------------------------------------------------------------------------
 * PostFileTask: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class PostFileTask extends DefaultTask {
    // Message passed in from main plugin class
    @Internal boolean displayLogging
    @Internal String token
    @Internal String webHook
    @Internal String uploadFilePath
    @Internal String uploadFileName
    @Internal String uploadFileType
    @Internal String uploadTitle
    @Internal String channels

    /**
     * sendAlert: Execute the sending of an alert.
     *
     * @return void
     */
    @TaskAction
    def sendFile() {

        final HttpPost post = new HttpPost(webHook)
        post.setHeader("Authorization", "Bearer $token")

        String fullPath

        if (uploadFilePath && !uploadFilePath.isEmpty()) {
            fullPath = "$uploadFilePath/$uploadFileName"
        } else {
            fullPath = "$project.buildDir/$uploadFileName"
        }

        if (displayLogging) {
            println "Upload to channel - $channels"
            println "File to Upload - $fullPath"
        }

        final File file = new File(fullPath)

        if(file.createNewFile()){
            println "File Created: $fullPath"
        } else {
            println "File already exists"
        }

        if ((!file.exists()) || file.isDirectory()) {
            throw new RuntimeException("Provided file path does not exist or is a directory")
        } else {
            final MultipartEntityBuilder builder = MultipartEntityBuilder.create()
            builder.setMode(HttpMultipartMode.LEGACY)
            builder.addPart("file", new FileBody(file, ContentType.DEFAULT_BINARY))
            // Check not needed for filename as this is handled above with file check
            builder.addPart("filename", new StringBody(uploadFileName, ContentType.MULTIPART_FORM_DATA))
            if (channels) {
                builder.addPart("channels", new StringBody(channels, ContentType.MULTIPART_FORM_DATA))
            } else {
                println "No channel(s) set, nothing will be sent"
            }
            if (uploadFileType) {
                builder.addPart("filetype", new StringBody(uploadFileType, ContentType.MULTIPART_FORM_DATA))
            }
            if (uploadTitle) {
                builder.addPart("title", new StringBody(uploadTitle, ContentType.MULTIPART_FORM_DATA))
            }
            //builder.addPart("initial_comment", new StringBody("Zipped JaCoCo Unit Coverage Report", ContentType.MULTIPART_FORM_DATA))

            final HttpEntity entity = builder.build()
            post.setEntity(entity)
            try (final CloseableHttpClient client = HttpClientBuilder.create().build()
                 final CloseableHttpResponse response = (CloseableHttpResponse) client
                         .execute(post, new CustomHttpClientResponseHandler())) {

                if (displayLogging) {
                    println "Upload URL - $webHook"
                    println("Returned Status code $response.code")
                }

                if (response.code != 200) {
                    throw new RuntimeException("Bad Status from Slack API returned $response.code")
                }
            }
        }
    }
}

// https://kodejava.org/how-do-i-do-multipart-upload-using-httpclient/
//https://www.baeldung.com/httpclient-multipart-upload