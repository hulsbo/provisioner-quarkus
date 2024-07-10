package io.hulsbo;

import io.hulsbo.model.Adventure;
import io.hulsbo.model.Manager;
import io.hulsbo.util.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

@Path("/adventures")
public class AdventureResource {

	@Inject
	KCalCalculationStrategy kCalCalculationStrategy;

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response createAdventure() {
		new Adventure(kCalCalculationStrategy);

		return browseAdventures();
	}

	@Inject
	@Location("adventureList.html")
	Template adventureList;

	@GET
	@Produces(MediaType.TEXT_HTML)
	public Response browseAdventures() {
		try {
			List<Adventure> adventures = Manager.getAllAdventures();

			System.out.println(adventures);

			String uniqueId = UUID.randomUUID().toString();
			String renderedHtml = adventureList.data("adventures", adventures, "uniqueId", uniqueId).render();

			return Response
					.ok(renderedHtml)
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

	@Inject
	@Location("adventureModal.html")
	Template adventureModal;

	@GET
	@Path("/modal")
	@Produces(MediaType.TEXT_HTML)
	public Response returnModal() {
		String renderedHtml = adventureModal.render();
		System.out.println(renderedHtml);
		return Response.ok(renderedHtml).build();
	}

	@GET
	@Path("/modal/preview")
	@Produces(MediaType.TEXT_HTML)
	public Response returnPreview() {
		String renderedHtml = adventureModal.render();
		System.out.println(renderedHtml);
		return Response.ok().build();
	}

	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response getAdventure(@PathParam("id") String id) {
		try {
			UUID adventureId = UUID.fromString(id);
			Adventure adventure = (Adventure) Manager.getObject(adventureId);

			if (adventure == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("<div class='error'>Adventure not found</div>")
						.build();
			}

			String htmlResponse = "<div class='adventure-item' data-id='" + adventure.getId() + "'>" +
					"<span class='adventure-name'>" + adventure.getName() + "</span>" +
					"<span class='adventure-days'>" + adventure.getDays() + " days</span>" +
					"<span class='adventure-crew'>" + adventure.getCrewSize() + " crew members</span>" +
					"<span class='adventure-kcal'>" + adventure.getCrewDailyKcalNeed()
					+ " total kCal need per day</span>" +
					"</div>";

			return Response.ok(htmlResponse).build();
		} catch (IllegalArgumentException e) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("<div class='error'>Invalid adventure ID</div>")
					.build();
		}
	}

	@DELETE
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response deleteAdventure(@PathParam("id") UUID id) {
		// Implement deletion logic here
		Manager.removeObject(id);
		// Use the existing browseAdventures method to fetch the updated list
		return browseAdventures();
	}

}