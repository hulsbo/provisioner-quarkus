package io.hulsbo;

import io.hulsbo.model.Adventure;
import io.hulsbo.model.Manager;
import io.hulsbo.util.model.CrewMember.Gender;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.HarrisBenedictOriginal;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import io.hulsbo.util.model.CrewMember.PhysicalActivity;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;

import static io.hulsbo.util.service.InstanceClassAndIDsGeneration.addInstanceClassAndIDs;

@Path("/adventures")
public class AdventureResource {

	@Inject
	KCalCalculationStrategy kCalCalculationStrategy;

	@POST
	@Produces(MediaType.TEXT_HTML)
	public Response createAdventure() {
		Adventure test = new Adventure(kCalCalculationStrategy);


		test.addCrewMember("Oskar", 29,170 + Math.random()*10, 75,
				Gender.MALE, PhysicalActivity.MODERATE , new HarrisBenedictOriginal());
		test.addCrewMember("Lovisa", 31,160 + Math.random()*10, 75,
				Gender.FEMALE, PhysicalActivity.VERY_HEAVY , new HarrisBenedictOriginal());
		test.setDays((int) (1 + Math.random() * 10));

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
			System.out.println("yesys");
			String renderedUniqueTemplate = renderUniqueTemplate(adventures);

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

	/**
	 * <p>Renders a HTML template with unique id and class values that includes template filename.</p>
	 * <p>NOTE: The template should have id placeholders as <pre> {@code __id_p{number}__  }</pre> to be replaced with unique ones.</p>
	 * <p>Also possible to transfer IDs from parent components sent in the request.</p>
	 *
	 * @param adventures the list of adventures to render
	 * @return the rendered HTML template with unique instance classes and IDs
	 */
	private String renderUniqueTemplate(List<Adventure> adventures) {
		String renderedHtml = adventureList.data("adventures", adventures).render();
		String renderedHtmlWithClass = addInstanceClassAndIDs(
				renderedHtml,
				adventureList.getId().replace(".html", ""
				));
		return renderedHtmlWithClass;
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

	@Inject
	@Location("adventureInfo.html")
	Template adventureInfoTemplate;

	@GET
	@Path("/{id}/info")
	@Produces(MediaType.TEXT_HTML)
	public Response getAdventureInfo(@PathParam("id") UUID id) {
		Adventure adventure = (Adventure) Manager.getObject(id);
		if (adventure == null) {
			throw new WebApplicationException("Adventure not found", Response.Status.NOT_FOUND);
		}
		String renderedHtml = adventureInfoTemplate.data("adventure", adventure).render();
		return Response.ok(renderedHtml).build();
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