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

@Path("/objects")
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
					return Response.status(Response.Status.NO_CONTENT).header("HX-Trigger", "evt__crud_crew").build();
				}
				case "meal" -> {
					Adventure parent = (Adventure) Manager.getBaseClass(SafeID.fromString(parentId));
					Meal newMeal = new Meal();
					parent.putChild(newMeal);
					return Response.status(Response.Status.NO_CONTENT).header("HX-Trigger", "evt__crud_meal").build();
				}
				case "ingredient" -> {
					Meal parent = (Meal) Manager.getBaseClass(SafeID.fromString(parentId));
					Ingredient newIngredient = new Ingredient();
					parent.putChild(newIngredient);
					return Response.status(Response.Status.NO_CONTENT).header("HX-Trigger", "evt__crud_ingredient").build();
				}
				case "adventure" -> {
					new Adventure();
					return Response.status(Response.Status.NO_CONTENT).header("HX-Trigger", "evt__crud_adventure").build();
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
	public Response getInfoForObject(@QueryParam("id") String id, @QueryParam("type") String type) {

		if (id != null && id.equals("null")) {
			return Response.status(Response.Status.NO_CONTENT).build();
		}

		BaseClass object = Manager.getBaseClass(SafeID.fromString(id));

		String renderedHtml = adventureDashboard.getFragment("info_object").data("object", object, "type", type).render();
		String componentInstance = createComponentInstance(renderedHtml, adventureDashboard);

		return Response.ok(componentInstance).build();
	}

	@PUT
	@Path("/adventure/{id}/input")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response putAdventure(MultivaluedMap<String, String> formParams, @PathParam("id") SafeID id) {

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
		return Response.ok().header("HX-Trigger", "evt__crud_adventure").build();
	}

	@PUT
	@Path("/meal/{id}/input")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response putMeal(MultivaluedMap<String, String> formParams, @PathParam("id") SafeID id) {

		Meal meal = (Meal) Manager.getBaseClass(id);

		if (meal == null) {
			throw new WebApplicationException("Meal not found", Response.Status.NOT_FOUND);
		}

		for (Map.Entry<String, List<String>> entry : formParams.entrySet()) {
			String key = entry.getKey();
			String singleValue = entry.getValue().get(0);  // Assuming single value

			switch (key) {
				case "name" -> meal.setName(singleValue);
				// Add more cases as needed
				default -> throw new IllegalArgumentException("Unexpected form parameter: " + key);
			}
		}
		return Response.ok().header("HX-Trigger", "evt__crud_meal").build();
	}

	@PUT
	@Path("/ingredient/{id}/input")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response putIngredient(MultivaluedMap<String, String> multivaluedFormParams, @PathParam("id") SafeID id) {

		Map<String, String> formParams = new HashMap<>();

		for (String key : multivaluedFormParams.keySet()) {
			formParams.put(key, multivaluedFormParams.getFirst(key));
		}

		Ingredient ingredient = (Ingredient) Manager.getBaseClass(id);

		if (ingredient == null) {
			throw new WebApplicationException("Ingredient not found", Response.Status.NOT_FOUND);
		}

		for (Map.Entry<String, String> entry : formParams.entrySet()) {
			String key = entry.getKey();

			if (key.equals("name")) {
				ingredient.setName(entry.getValue());
			} else {
				double value = Integer.parseInt(entry.getValue());
				switch (key) {
					case "carbs" -> ingredient.modifyNutrient("carbs", value);
					case "protein" -> ingredient.modifyNutrient("protein", value);
					case "fat" -> ingredient.modifyNutrient("fat", value);
					case "water" -> ingredient.modifyNutrient("water", value);
					case "fiber" -> ingredient.modifyNutrient("fiber", value);
					case "salt" -> ingredient.modifyNutrient("salt", value);
					// Add more cases as needed
					default -> throw new IllegalArgumentException("Unexpected form parameter: " + key);
				}
			}
		}
		return Response.ok().header("HX-Trigger", "evt__crud_ingredient").build();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response getAdventure(@QueryParam("id") SafeID id, @QueryParam("fragmentId") String fragmentId) {

		if (fragmentId != null && fragmentId.equals("null")) {
			fragmentId = null;
		}

		try {
            Adventure adventure = (Adventure) Manager.getBaseClass(id);

			if (adventure == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("<div class='error'>Adventure not found</div>")
						.build();
			}

			// Render in Qute
			String renderedHtml;
			if ( fragmentId == null ) {
				// Render whole dashboard
				renderedHtml = adventureDashboard.data("adventure", adventure ).render();
			} else {
				// Render fragment only
				renderedHtml = adventureDashboard.getFragment(fragmentId).data("adventure", adventure ).render();
			}

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