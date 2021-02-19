package org.acme.restclient;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static com.github.tomakehurst.wiremock.client.WireMock.*;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.acme.restclient.integration.ExampleMockServer;
import org.acme.restclient.integration.ExampleMockServer.ExampleMockClient;

@QuarkusTest
@QuarkusTestResource(ExampleMockServer.class)
@TestHTTPEndpoint(ExampleResource.class)
public class ExampleResourceTest {

    @ExampleMockClient
    private WireMockServer server;

    @Test
    public void testExampleRemoteEndpoint() {

         server.stubFor(get(urlEqualTo("/example"))
                .willReturn(aResponse().withHeader("Content-type", "application/json").withBody("Hello")));

        given()
          .when()
          .header("x-propagation", "propagation value")
          .get()
          .then()
             .statusCode(200)
             .body(is("Hello"));
    }


    @Test
    public void testExampleRemotePropagateHeader() {
         server.stubFor(get(urlEqualTo("/example")).withHeader("x-propagation", equalTo("propagation value"))
                .willReturn(aResponse().withHeader("Content-type", "application/json").withBody("Hello")));

        given()
          .when()
          .header("x-propagation", "propagation value")
          .get()
          .then()
             .statusCode(200)
             .body(is("Hello"));
    }


    @Test
    public void testExampleRemoteIncludesClientHeader() {
        server.stubFor(get(urlEqualTo("/example"))
                .withHeader("x-propagation", equalTo("propagation value"))
                .withHeader("x-client-header", equalTo("client header value"))
                .willReturn(aResponse().withHeader("Content-type", "application/json").withBody("Hello")));

        given()
          .when()
          .header("x-propagation", "propagation value")
          .get()
          .then()
             .statusCode(200)
             .body(is("Hello"));
    }

}