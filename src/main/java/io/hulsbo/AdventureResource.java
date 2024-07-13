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

	@Inject
	@Location("adventureDashboard.html")
	Template adventureDashboard;

	@GET
	@Path("/load")
	@Produces(MediaType.TEXT_HTML)
	public Response getAdventure(@QueryParam("id") String id) {
		try {
			UUID adventureId = UUID.fromString(id);
			Adventure adventure = (Adventure) Manager.getObject(adventureId);

			if (adventure == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("<div class='error'>Adventure not found</div>")
						.build();
			}

			// Render in Qute
			String renderedHtml = adventureDashboard.data("adventureId", adventure ).render();

			// Create component instance
			String componentInstance = createComponentInstance(renderedHtml, adventureDashboard);

			return Response.ok(componentInstance).build();

		} catch(Exception exception) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("<div class='error'>Bad request, could not load adventure.</div>")
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
	@Path("/{id}")
	@Produces(MediaType.TEXT_HTML)
	public Response deleteAdventure(@PathParam("id") UUID id) {
		// Implement deletion logic here
		Manager.removeObject(id);
		// Use the existing browseAdventures method to fetch the updated list
		return browseAdventures();
	}
}