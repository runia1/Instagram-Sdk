package com.mrunia.instagram.sdk;

import android.os.Bundle;

/**
 * Created by mar on 2/17/15.
 */
public class InstagramAuthConfig {
    private static String clientId, clientSecret, callbackUrl;
    private static String[] scopes;

    public InstagramAuthConfig() { }
    public InstagramAuthConfig(String clientId, String clientSecret, String callbackUrl, String[] scopes) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.callbackUrl = callbackUrl;
        this.scopes = scopes;
    }

    public void setClientId(String c) {
        clientId = c;
    }

    public void setClientSecret(String c) {
        clientSecret = c;
    }

    public void setCallbackUrl(String c) {
        callbackUrl = c;
    }

    public void setScopes(String[] c) {
        scopes = c;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public String[] getScopes() {
        return scopes;
    }
}
