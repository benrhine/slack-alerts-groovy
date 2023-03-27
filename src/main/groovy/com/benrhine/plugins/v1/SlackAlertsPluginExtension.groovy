package com.benrhine.plugins.v1

/** --------------------------------------------------------------------------------------------------------------------
 * SlackAlertsPluginExtension: Global config that will allow for the setting of common variables a single time.
 * ------------------------------------------------------------------------------------------------------------------ */
class SlackAlertsPluginExtension {
    transient String environment
    transient String webHook
    transient String uploadUrl
    transient String token
    transient String channels
    transient String payload
    boolean displayLogging = false
}
