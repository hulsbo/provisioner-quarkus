package io.hulsbo;
import io.hulsbo.model.Adventure;
import io.hulsbo.model.BaseClass;
import io.hulsbo.model.Manager;
import io.hulsbo.model.Meal;
import io.hulsbo.util.model.SafeID;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

import static io.hulsbo.ObjectsResource.createComponentInstance;

@Path("/ui")
public class UiResource {

    @Inject
    @Location("list.html")
    Template listTemplate;

    @Inject
    @Location("createCrewMemberModal.html")
    Template createCrewMemberModal;

    @Inject
    @Location("modifyMealModal.html")
    Template modifyMealModal;

    @GET
    @Path("/clear")
    @Produces(MediaType.TEXT_HTML)
    public Response returnModal() {
        return Response.ok().build();
    }

    @GET
    @Path("/modal")
    @Produces(MediaType.TEXT_HTML)
    public Response openModal(
            @QueryParam("id") String id,
            @QueryParam("type") String type,
            @QueryParam("parentId") String parentId,
            @QueryParam("callerId") String callerId
    ) {

        Template someTemplate = null;
        String renderedHtml = "";

        try {
            switch (type) {
                case "crewmember" -> {
                    someTemplate = createCrewMemberModal;

                    // Get parent
                    Adventure adventure = (Adventure) Manager.getBaseClass(SafeID.fromString(parentId));

                    // Render in Qute
                    renderedHtml = someTemplate.data("adventure", adventure, "type", type, "callerId", callerId).render();
                }
                case "meal" -> {
                    someTemplate = modifyMealModal;

                    // Get parent
                    Adventure adventure = (Adventure) Manager.getBaseClass(SafeID.fromString(parentId));

                    // Get meal
                    Meal meal = (Meal) Manager.getBaseClass(SafeID.fromString(id));

                    // Render in Qute
                    renderedHtml = someTemplate.data("adventure", adventure, "meal", meal, "type", type, "callerId", callerId).render();
                }
            }
        } catch (ClassCastException e) {
            throw new WebApplicationException("Cast operation failed for " + type, Response.Status.BAD_REQUEST);
        }

        assert someTemplate != null;
        // Create component instance
        String componentInstance = createComponentInstance(renderedHtml, someTemplate);

        return Response.ok(componentInstance).build();
    }

    @GET
    @Path("/modal/ingredient")
    @Produces(MediaType.TEXT_HTML)
    public Response getIngredientDetails(@QueryParam("id") String id, @QueryParam("type") String type) {

        if (id != null && id.equals("null")) {
            return Response.status(Response.Status.NO_CONTENT).build();
        }

        Ingredient ingredient = (Ingredient) Manager.getBaseClass(SafeID.fromString(id));

        String renderedHtml = modifyMealModal.getFragment("ingredientDetails").data("ingredient", ingredient, "type", type).render();
        String componentInstance = createComponentInstance(renderedHtml, modifyMealModal);

        return Response.ok(componentInstance).build();
    }

    @GET
    @Path("/list/adventures")
    @Produces(MediaType.TEXT_HTML)
    public Response getAdventureList() {

        List<Adventure> adventures = Manager.getAllAdventures();

        Map<String, String> actions = Map.of(
                "add", "/objects/",
                "edit", "/objects/",
                "remove", "/objects/"
        );

        // Render in Qute
        String renderedHtml = listTemplate.data(
                "actions", actions,
                "items", adventures,
                "type", "adventure"
        ).render();

        // Create component instance
        String componentInstance = createComponentInstance(renderedHtml, listTemplate);

        return Response.ok(componentInstance).build();
    }

    @GET
    @Path("/list/{parentId}/{type}")
    @Produces(MediaType.TEXT_HTML)
    public Response getList(
            @PathParam("parentId") String parentId,
            @PathParam("type") String type
    ) {

        SafeID id = SafeID.fromString(parentId);

        if (id == null) {
            throw new WebApplicationException("parentId "+ parentId + " was not a valid SafeID", Response.Status.NOT_FOUND);
        }

        BaseClass parent = Manager.getBaseClass(id);

        if (parent == null) {
            throw new WebApplicationException("Parent object could not be found", Response.Status.NOT_FOUND);
        }

        if (type == null) {
            throw new WebApplicationException("No type parameter in path.", Response.Status.NOT_FOUND);
        }

        List<?> items;
        Map<String, String> actions;

        switch (type.toLowerCase()) {

            case "crewmember":
                if (!(parent instanceof Adventure)) {
                    throw new WebApplicationException("Parent must be an Adventure for crewmember list", Response.Status.BAD_REQUEST);
                }

                items = ((Adventure) parent).getAllCrewMembers();
                actions = Map.of(
                        "add", "ui/modal",
                        "remove", "/objects/"
                );
                break;

            case "meal":
                if (!(parent instanceof Adventure)) {
                    throw new WebApplicationException("Parent must be an Adventure for meal list", Response.Status.BAD_REQUEST);
                }

                items = parent.getAllChildren();
                actions = Map.of(
                        "add", "/objects/",
                        "edit", "ui/modal",
                        "remove", "/objects/"
                );
                break;

            case "ingredient":
                if (!(parent instanceof Meal)) {
                    throw new WebApplicationException("Parent must be a Meal for ingredient list", Response.Status.BAD_REQUEST);
                }

                items = parent.getAllChildren();
                actions = Map.of(
                        "add", "/objects/",
                        "edit", "/objects/",
                        "remove", "/objects/"
                );
                break;

            default:
                throw new WebApplicationException("Invalid type parameter", Response.Status.BAD_REQUEST);
        }

        // Render in Qute
        String renderedHtml = listTemplate.data(
                "type", type,
                "parent", parent,
                "actions", actions,
                "items", items
        ).render();

        // Create component instance
        String componentInstance = createComponentInstance(renderedHtml, listTemplate);

        return Response.ok(componentInstance).build();
    }

}