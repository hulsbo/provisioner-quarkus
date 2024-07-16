package io.hulsbo;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


@Path("/mock/adventures")
public class MockResource {

    @GET
    @Path("/modal")
    @Produces(MediaType.TEXT_HTML)
    public Response returnModal() {

        // R ender in Qute
        String renderedHtml = adventureModal.render();

        // Create component instance
        String componentInstance = createComponentInstance(renderedHtml, adventureModal);

        return Response.ok(componentInstance).build();
    }
}
