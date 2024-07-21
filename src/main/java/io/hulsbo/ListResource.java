package io.hulsbo;

import io.hulsbo.model.Adventure;
import io.hulsbo.model.BaseClass;
import io.hulsbo.model.Manager;
import io.hulsbo.util.model.SafeID;
import io.hulsbo.util.model.baseclass.ChildWrapper;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("/ui/list_js")
public class ListResource {

    @GET
    @Path("/{parentId}/{type}")
    @Produces(MediaType.TEXT_HTML)
    public Response getListForJs(
            @PathParam("parentId") SafeID parentId,
            @PathParam("type") String type
    ) {
        if (!"meal".equalsIgnoreCase(type)) {
            throw new WebApplicationException("Only meal type is supported", Response.Status.BAD_REQUEST);
        }

        BaseClass parent = Manager.getBaseClass(parentId);
        if (!(parent instanceof Adventure)) {
            throw new WebApplicationException("Parent must be an Adventure for meal list", Response.Status.BAD_REQUEST);
        }

        Adventure adventure = (Adventure) parent;
        List<ChildWrapper> meals = adventure.getAllChildren(); // Assuming this method exists

        StringBuilder htmlBuilder = new StringBuilder();
        htmlBuilder.append("<ul>");

        for (ChildWrapper wrapper : meals) {
            htmlBuilder.append("<li id=\"").append(wrapper.getChild().getId()).append("\">");
            htmlBuilder.append("<div class=\"name\">").append(wrapper.getChild().getName()).append("</span>");
            htmlBuilder.append("</li>");
        }

        htmlBuilder.append("</ul>");

        return Response.ok(htmlBuilder.toString()).build();
    }

    @GET
    @Path("/{type}/create")
    @Produces(MediaType.TEXT_HTML)
    public Response createItem(
            @PathParam("type") String type,
            @QueryParam("parentId") SafeID parentId
    ) {
        // Implement the create logic here
        // This is a placeholder implementation
        return Response.ok("<p>Create action for " + type + " with parent " + parentId + " initiated</p>").build();
    }

    @GET
    @Path("/{type}/edit/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response editItem(
            @PathParam("type") String type,
            @PathParam("id") SafeID id,
            @QueryParam("parentId") SafeID parentId
    ) {
        // Implement the edit logic here
        // This is a placeholder implementation
        return Response.ok("<p>Edit action for " + type + " with id " + id + " and parent " + parentId + " initiated</p>").build();
    }

    @GET
    @Path("/{type}/remove/{id}")
    @Produces(MediaType.TEXT_HTML)
    public Response removeItem(
            @PathParam("type") String type,
            @PathParam("id") SafeID id,
            @QueryParam("parentId") SafeID parentId
    ) {
        // Implement the remove logic here
        // This is a placeholder implementation
        return Response.ok("<p>Remove action for " + type + " with id " + id + " and parent " + parentId + " initiated</p>").build();
    }

    // ... (keep other existing methods)
}