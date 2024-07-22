package io.hulsbo;

import io.hulsbo.model.*;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.hulsbo.util.model.SafeID;
import io.hulsbo.util.service.StackTraceFormatter;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

import java.util.*;

import static io.hulsbo.util.service.InstanceClassAndIDsGeneration.addInstanceClassAndIDs;

@Path("/adventures") // TODO: Make Adventures into generic resource for all types.
public class AdventureResource {

	@Inject
	KCalCalculationStrategy kCalCalculationStrategy;

	@Inject
	@Location("adventureInfo.html")
	Template adventureInfoTemplate;

	@Inject
	@Location("adventureList.html")
	Template adventureList;

	@Inject
	@Location("adventureModal.html")
	Template adventureModal;

	@Inject
	@Location("createCrewMemberModal.html")
	Template createCrewMemberModal;

	@Inject
	@Location("adventureDashboard.html")
	Template adventureDashboard;

//  NOTE: DEPRECATED, SEE GENERIC LIST.
//	@Inject
//	@Location("crewList.html")
//	Template crewList;
//
//	@Inject
//	@Location("mealsList.html")
//	Template mealsList;

	@Inject
	UiResource uiResource;

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response createAdventure(@FormParam("type") String type, @FormParam("parentId") String parentId) {

		// Common checks for crewMember, meal, and ingredient
		if (Arrays.asList("crewMember", "meal", "ingredient").contains(type)) {

			if (parentId == null) {
				throw new WebApplicationException("Parent ID is required for creating a " + type, Response.Status.BAD_REQUEST);
			}

			BaseClass parent = null;

			try {
				parent = Manager.getBaseClass(SafeID.fromString(parentId));
			} catch (Exception e) {
				throw new WebApplicationException("Error retrieving parent object: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
			}

		}


		// TODO: Finish POST for all types.
		try {
			switch (type) {
				case "crewMember" -> {
					Adventure parentAdventure = (Adventure) parent;
					CrewMember newCrewMember = new CrewMember(name, age, height, weight, gender, activity, kCalCalculationStrategy);
					parentAdventure.putCrewMember();
					yield newCrewMember;
				}
				case "meal" -> {
					Adventure parentAdventure = (Adventure) parent;
					Meal newMeal = new Meal();
					newMeal.setName(name);
					parentAdventure.putChild(newMeal);
					yield newMeal;
				}
				case "ingredient" -> {
					Meal parentMeal = (Meal) parent;
					Ingredient newIngredient = new Ingredient();
					newIngredient.setName(name);
					parentMeal.putChild(newIngredient);
					yield newIngredient;
				}
				case "adventure" -> {
					Adventure newAdventure = new Adventure(kCalCalculationStrategy);
					newAdventure.setName(name);
					yield newAdventure;
				}
				default -> throw new WebApplicationException("Invalid type: " + type, Response.Status.BAD_REQUEST);
			}
		} catch (ClassCastException e) {
			throw new WebApplicationException("Invalid parent type for " + type, Response.Status.BAD_REQUEST);
		} catch (Exception e) {
			throw new WebApplicationException("Error creating " + type + ": " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
		}



		Adventure test = new Adventure(kCalCalculationStrategy);


		return uiResource.getAdventureList();
	}

//	DEPRECATED: SEE GENERIC LIST
//	@GET
//	@Produces(MediaType.TEXT_HTML)
//	public Response browseAdventures() {
//		try {
//			List<Adventure> adventures = Manager.getAllAdventures();
//
//			// Render in Qute
//			String renderedHtml = adventureList.data("adventures", adventures).render();
//
//			// Create component Instance
//			String renderedUniqueTemplate = createComponentInstance(renderedHtml, adventureList);
//
//			return Response
//					.ok(renderedUniqueTemplate)
//					.build();
//		} catch (Exception e) {
//			// Log the exception
//			e.printStackTrace();
//
//			// Return an error response
//			return Response
//					.status(Response.Status.INTERNAL_SERVER_ERROR)
//					.entity("<p>An error occurred while fetching adventures.</p>")
//					.build();
//		}
//	}

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

	@GET
	@Path("/modal/crew-member-form")
	@Produces(MediaType.TEXT_HTML)
	public Response openCreateCrewMemberModal() {

		// Render in Qute
		String renderedHtml = createCrewMemberModal.render();

		// Create component instance
		String componentInstance = createComponentInstance(renderedHtml, createCrewMemberModal);

		return Response.ok(componentInstance).build();
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

//  NOTE: DEPRECATED DUE TO GENERIC LIST CREATION.
//	@GET
//	@Path("/{id}/crew")
//	@Produces(MediaType.TEXT_HTML)
//	public Response getCrewList(@PathParam("id") SafeID id) {
//		Adventure adventure = (Adventure) Manager.getBaseClass(id);
//		if (adventure == null) {
//			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
//		}
//
//		// Render in Qute
//		String renderedHtml = crewList.data( "adventure", adventure).render();
//
//		// Create component instance
//		String componentInstance = createComponentInstance(renderedHtml, crewList);
//
//		return Response.ok(componentInstance).build();
//	}

//	@GET
//	@Path("/{id}/meals")
//	@Produces(MediaType.TEXT_HTML)
//	public Response getMealsList(@PathParam("id") SafeID id) {
//		Adventure adventure = (Adventure) Manager.getBaseClass(id);
//		if (adventure == null) {
//			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
//		}
//
//		// Render in Qute
//		String renderedHtml = mealsList.data( "adventure", adventure).render();
//
//		// Create component instance
//		String componentInstance = createComponentInstance(renderedHtml, mealsList);
//
//		return Response.ok(componentInstance).build();
//	}

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

//  DEPRECATED FOR GENERIC DELETE ENDPOINT SEE BELOW
//	@DELETE
//	@Produces(MediaType.TEXT_HTML)
//	public Response deleteAdventure(@FormParam("id") SafeID id) {
//		Adventure adventure = (Adventure) Manager.getBaseClass(id);
//		if (adventure == null) {
//			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
//		}
//		try {
//			String response = Manager.removeBaseClassObject(id);
//			return Response.status(Response.Status.NO_CONTENT).header("success-info", response).build();
//		}
//		catch(Exception exception) {
//			// Log the exception
//			exception.printStackTrace();
//
//			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
//					.entity(StackTraceFormatter.formatStackTrace(exception))
//					.build();
//		}
//	}

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