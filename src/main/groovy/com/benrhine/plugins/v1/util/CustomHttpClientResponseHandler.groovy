package com.benrhine.plugins.v1.util

import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.io.HttpClientResponseHandler;

/** --------------------------------------------------------------------------------------------------------------------
 * CustomHttpClientResponseHandler: TODO fill me in.
 * ------------------------------------------------------------------------------------------------------------------ */
class CustomHttpClientResponseHandler implements HttpClientResponseHandler<ClassicHttpResponse> {
    @Override
    public ClassicHttpResponse handleResponse(ClassicHttpResponse response) {
        return response;
    }
}