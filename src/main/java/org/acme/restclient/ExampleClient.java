package org.acme.restclient;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/example")
@RegisterRestClient
@RegisterClientHeaders
@ClientHeaderParam(name = "x-client-header", value = "client header value")
public interface ExampleClient {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    String hello(@QueryParam("name") String name);

}
