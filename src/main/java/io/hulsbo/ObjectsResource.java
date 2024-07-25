package io.hulsbo;

import io.hulsbo.model.*;
import io.hulsbo.util.model.SafeID;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.*;

import static io.hulsbo.util.service.InstanceClassAndIDsGeneration.addInstanceClassAndIDs;

@Path("/adventures")
public class ObjectsResource {

	@Inject
	@Location("adventureInfo.html")
	Template adventureInfoTemplate;

	@Inject
	@Location("adventureModal.html")
	Template adventureModal;

	@Inject
	@Location("adventureDashboard.html")
	Template adventureDashboard;

	@Inject
	UiResource uiResource;

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response createObject(
			// General
			@FormParam("type") String type,
			@FormParam("parentId") String parentId,
			//	Create Crew Member Parameters
			@FormParam("name") String name,
			@FormParam("age") String age,
			@FormParam("height") String height,
			@FormParam("weight") String weight,
			@FormParam("gender") String gender,
			@FormParam("activity") String activity,
			@FormParam("strategy") String strategy
			) {

		if (Arrays.asList("crewmember", "meal", "ingredient").contains(type) && parentId == null) {
				throw new WebApplicationException("Parent ID is required for creating a " + type, Response.Status.BAD_REQUEST);
		}

		try {
			switch (type) {
				case "crewmember" -> {
					Adventure parent = (Adventure) Manager.getBaseClass(SafeID.fromString(parentId));
					parent.putCrewMember(name, Integer.parseInt(age), Integer.parseInt(height), Integer.parseInt(weight), gender, activity, strategy);
					return uiResource.getList(parentId, type);
				}
				case "meal" -> {
					Adventure parent = (Adventure) Manager.getBaseClass(SafeID.fromString(parentId));
					Meal newMeal = new Meal();
					parent.putChild(newMeal);
					return uiResource.getList(parentId, type);
				}
				case "ingredient" -> {
					Meal parent = (Meal) Manager.getBaseClass(SafeID.fromString(parentId));
					Ingredient newIngredient = new Ingredient();
					parent.putChild(newIngredient);
					return uiResource.getList(parentId, type);
				}
				case "adventure" -> {
					new Adventure();
					return uiResource.getAdventureList();
				}
				default -> throw new WebApplicationException("Invalid type: " + type, Response.Status.BAD_REQUEST);
			}
		} catch (ClassCastException e) {
			throw new WebApplicationException("Invalid parent type for " + type, Response.Status.BAD_REQUEST);
		} catch (Exception e) {
			throw new WebApplicationException("Error creating " + type + ": " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
		}

	}

	@GET
	@Path("/modal")
	@Produces(MediaType.TEXT_HTML)
	public Response returnModal() {

		// Render in Qute
		String renderedHtml = adventureModal.render();

		// Create component instance
		String componentInstance = createComponentInstance(renderedHtml, adventureModal);

		return Response.ok(componentInstance).build();
	}

	@GET
	@Path("/info")
	@Produces(MediaType.TEXT_HTML)
	public Response getAdventureInfo(@QueryParam("id") SafeID id) {
		Adventure adventure = (Adventure) Manager.getBaseClass(id);
		if (adventure == null) {
			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
		}
		String renderedHtml = adventureInfoTemplate.data("adventure", adventure).render();
		return Response.ok(renderedHtml).build();
	}

	@PUT
	@Path("/{id}/input")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response putName(MultivaluedMap<String, String> formParams, @PathParam("id") SafeID id) {

		Adventure adventure = (Adventure) Manager.getBaseClass(id);
		if (adventure == null) {
			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
		}

		for (Map.Entry<String, List<String>> entry : formParams.entrySet()) {
			String key = entry.getKey();
			String singleValue = entry.getValue().get(0);  // Assuming single value

			switch (key) {
				case "name" -> adventure.setName(singleValue);
				case "days" -> adventure.setDays(Integer.parseInt(singleValue));
				// Add more cases as needed
				default -> throw new IllegalArgumentException("Unexpected form parameter: " + key);
			}
		}
		String name = new Meal().getClass().getSimpleName();
		return Response.ok().build();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getAdventure(@QueryParam("id") SafeID id) {
		try {
            Adventure adventure = (Adventure) Manager.getBaseClass(id);

			if (adventure == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("<div class='error'>Adventure not found</div>")
						.build();
			}

			// Render in Qute
			String renderedHtml = adventureDashboard.data("adventure", adventure ).render();

			// Create component instance
			String componentInstance = createComponentInstance(renderedHtml, adventureDashboard);

			return Response.ok(componentInstance).build();

		} catch(Exception exception) {
			// Log the exception
			exception.printStackTrace();

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("Error 500, refer to stack trace.")
					.build();
		}
	}

	public static String createComponentInstance(String renderedHtml, Template template) {
		return addInstanceClassAndIDs(
				renderedHtml,
				template.getId().replace(".html", ""
				));
	}

	@DELETE
	public Response deleteObject(
			@FormParam("parentId") SafeID parentId,
			@FormParam("id") SafeID id,
			@FormParam("type") String type) {

		String response = "";

		try {
			if (type.equals("crewmember")) {
				Adventure parent = (Adventure) Manager.getBaseClass(parentId);
				response = parent.removeCrewMember(id);
			} else if (Arrays.asList("meal", "ingredient").contains(type)) {
				BaseClass parent = Manager.getBaseClass(parentId);
				response = parent.removeChild(id);
			} else if (type.equals("adventure")) {
				response = Manager.removeBaseClassObject(id);
			}
			System.out.println(response);
			return Response.status(Response.Status.OK).header("success-info", response).build();
		}
			catch(Exception exception) {
				// Log the exception
				exception.printStackTrace();

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(exception)
					.build();
			}
	}
}