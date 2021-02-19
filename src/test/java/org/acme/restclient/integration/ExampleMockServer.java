package org.acme.restclient.integration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class ExampleMockServer implements QuarkusTestResourceLifecycleManager {

    private WireMockServer server;

    @Override
    public Map<String, String> start() {
        server = new WireMockServer(wireMockConfig().dynamicPort());
        server.start();

        server.stubFor(any(anyUrl())
            .atPriority(10)
            .willReturn(aResponse().withStatus(404)));

        return Collections.singletonMap("org.acme.restclient.ExampleClient/mp-rest/url", server.baseUrl());
    }

    @Override
    public void stop() {
        Optional.ofNullable(server)
                .ifPresent(WireMockServer::stop);
    }

    @Override
    public void inject(Object testInstance) {
        Class<?> c = testInstance.getClass();
        while (c != Object.class) {
            for (Field f : c.getDeclaredFields()) {
                if (f.getAnnotation(ExampleMockClient.class) != null) {
                    if (!WireMockServer.class.isAssignableFrom(f.getType())) {
                        throw new RuntimeException("@ExampleMockClient can only be used on fields of type WireMockServer");
                    }

                    f.setAccessible(true);
                    try {
                        f.set(testInstance, server);
                        return;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            c = c.getSuperclass();
        }
    }

    
    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    public @interface ExampleMockClient {
        
    }
    
}