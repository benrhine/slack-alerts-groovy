package com.benrhine.plugins.v1

import com.benrhine.plugins.v1.model.Attachment
import com.benrhine.plugins.v1.model.Message

/** --------------------------------------------------------------------------------------------------------------------
 * SlackAlertsPluginExtension: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class SlackAlertsPluginExtension {
    transient String webHook
    transient String uploadUrl

    transient String payload

    boolean displayLogging = false

}
