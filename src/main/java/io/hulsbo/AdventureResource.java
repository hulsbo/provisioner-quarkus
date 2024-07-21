package io.hulsbo;

import io.hulsbo.model.Adventure;
import io.hulsbo.model.Manager;
import io.hulsbo.model.Meal;
import io.hulsbo.util.model.CrewMember.Gender;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.HarrisBenedictOriginal;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.hulsbo.util.model.CrewMember.PhysicalActivity;
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

@Path("/adventures")
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

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response createAdventure() {
		Adventure test = new Adventure(kCalCalculationStrategy);

		test.putCrewMember("Oskar", 29, (int) (170 + Math.random()*10), 75,
				Gender.MALE, PhysicalActivity.MODERATE , new HarrisBenedictOriginal());
		test.putCrewMember("Lovisa", 31, (int) (160 + Math.random()*10), 75,
				Gender.FEMALE, PhysicalActivity.VERY_HEAVY , new HarrisBenedictOriginal());
		test.setDays((int) (1 + Math.random() * 10));

		return browseAdventures();
	}

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response browseAdventures() {
		try {
			List<Adventure> adventures = Manager.getAllAdventures();

			// Render in Qute
			String renderedHtml = adventureList.data("adventures", adventures).render();

			// Create component Instance
			String renderedUniqueTemplate = createComponentInstance(renderedHtml, adventureList);

			return Response
					.ok(renderedUniqueTemplate)
					.build();
		} catch (Exception e) {
			// Log the exception
			e.printStackTrace();

			// Return an error response
			return Response
					.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity("<p>An error occurred while fetching adventures.</p>")
					.build();
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
	@Path("/load")
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

	private String createComponentInstance(String renderedHtml, Template template) {
		return addInstanceClassAndIDs(
				renderedHtml,
				template.getId().replace(".html", ""
				));
	}

	@DELETE
	@Path("/{id}/delete")
	@Produces(MediaType.TEXT_HTML)
	public Response deleteAdventure(@PathParam("id") SafeID id) {
		Adventure adventure = (Adventure) Manager.getBaseClass(id);
		if (adventure == null) {
			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
		}
		try {
			String response = Manager.removeBaseClassObject(id);
			return Response.status(Response.Status.NO_CONTENT).header("success-info", response).build();
		}
		catch(Exception exception) {
			// Log the exception
			exception.printStackTrace();

			return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
					.entity(StackTraceFormatter.formatStackTrace(exception))
					.build();
		}
	}

	@DELETE
	@Path("{adventureId}/crew/{crewMemberId}")
	public Response deleteObject(@PathParam("adventureId") SafeID adventureId, @PathParam("crewMemberId") SafeID crewMemberId) {
		Adventure adventure = (Adventure) Manager.getBaseClass(adventureId);
		if (adventure == null) {
			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
		}
		try {
			String response = adventure.removeCrewMember(crewMemberId);
			// TODO: Replace with another call on client - side to path /adventures,
			//  to make this delete method generic for all BaseClass objects/components.
			return Response.ok(response).build();
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