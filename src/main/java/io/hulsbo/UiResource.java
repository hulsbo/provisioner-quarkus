package io.hulsbo;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/ui")
public class UiResource {

    @GET
    @Path("/clear")
    @Produces(MediaType.TEXT_HTML)
    public Response returnModal() {
        return Response.ok().build();
    }

}