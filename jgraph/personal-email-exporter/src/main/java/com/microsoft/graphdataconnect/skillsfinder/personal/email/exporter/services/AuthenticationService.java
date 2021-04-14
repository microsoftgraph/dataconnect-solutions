package com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.services;

import com.microsoft.aad.msal4j.DeviceCode;
import com.microsoft.aad.msal4j.DeviceCodeFlowParameters;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.aad.msal4j.PublicClientApplication;
import com.microsoft.graphdataconnect.skillsfinder.personal.email.exporter.constants.AppConstants;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class AuthenticationService {
    private final static String AUTHORITY = "https://login.microsoftonline.com/common/";

    public String getUserAccessToken() {

        Set<String> scopeSet = new HashSet<String>();
        Collections.addAll(scopeSet, AppConstants.APP_SCOPES);

        ExecutorService pool = Executors.newFixedThreadPool(1);
        PublicClientApplication app;
        try {
            app = PublicClientApplication.builder(AppConstants.APP_ID)
                    .authority(AUTHORITY)
                    .executorService(pool)
                    .build();
        } catch (MalformedURLException e) {
            return null;
        }
        Consumer<DeviceCode> deviceCodeConsumer = (DeviceCode deviceCode) -> {
            System.out.println(deviceCode.message());
        };

        IAuthenticationResult result = app.acquireToken(
                DeviceCodeFlowParameters
                        .builder(scopeSet, deviceCodeConsumer)
                        .build()
        ).exceptionally(ex -> {
            System.out.println("Unable to authenticate - " + ex.getMessage());
            return null;
        }).join();

        pool.shutdown();

        if (result != null) {
            return result.accessToken();
        }

        return null;
    }
}
