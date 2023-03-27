package com.benrhine.plugins.v1.model

class Message {
    transient final String name     // Name the message - this happens automatically
    transient String webHook        // Where is the message being sent
    String verificationUrl          // External call to retrieve data

    boolean displayLogging = false  // Display sys outs for logging
    boolean includeInfo = true      // Future enhancement to optionally include build info on health check

    String uploadFilePath           // File path to any uploadable artifact
    String uploadFileName           // File name of uploadable artifact
    String uploadFileType           // What type of file is being uploaded
    String uploadTitle              // Rename the file with a better name
    String authType                 // What type of auth is required - Basic, Bearer
    String environment              // What environment is this message coming from or going to
    String token                    // Authentication token if necessary
    String payload                  // Extra field for generating custom messages
    int retries = 1                 // Default number of retries
    int sleepAmountOne = 0          // Set if delay is necessary for verification checks
    int sleepAmountTwo = 0          // Set if delay is necessary for verification checks

    // Specific to Slack
    String channels                 // Channel ID (C0XXXXXXXXX), in theory supports multiple comma seperated values C0XXXXXXXXX,C0XXXXXXXXX,C0XXXXXXXXX but I couldnt get this to work
    String iconUrl
    String iconEmoji
    String text
    String username
    String threadTs
    Boolean mrkdwn

    def attachments = []
    def blocks = []

    Message(String name) {
        this.name = name
    }

    void attachment(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        Attachment attachment = new Attachment()
        closure.delegate = attachment
        attachments << attachment
        closure()
    }

    void block(Closure closure) {
        closure.resolveStrategy = Closure.DELEGATE_FIRST
        Block block = new Block()
        closure.delegate = block
        blocks << block
        closure()
    }

}