package io.hulsbo;

import io.hulsbo.model.Adventure;
import io.hulsbo.model.Meal;
import io.hulsbo.util.model.CrewMember.KCalCalculationStrategies.HarrisBenedictOriginal;
import io.hulsbo.util.model.SafeID;
import io.quarkus.qute.Location;
import io.quarkus.qute.Template;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static io.hulsbo.util.model.CrewMember.Gender.MALE;
import static io.hulsbo.util.model.CrewMember.PhysicalActivity.MODERATE;
import static io.hulsbo.util.service.InstanceClassAndIDsGeneration.addInstanceClassAndIDs;

@Path("/mock/adventures")
public class MockResource {
    @Inject
    @Location("adventureDashboard.html")
    Template adventureDashboard;

    private String createComponentInstance(String renderedHtml, Template template) {
        return addInstanceClassAndIDs(
                renderedHtml,
                template.getId().replace(".html", ""
                ));
    }

    @GET
    @Path("/load")
    @Produces(MediaType.TEXT_HTML)
    public Response getAdventure(@QueryParam("id") SafeID id) {
        try {
            Adventure adventure = new Adventure(new HarrisBenedictOriginal());

            adventure.setName("Salad days");

            adventure.putCrewMember("Oskar", 29, 186, 75, MALE, MODERATE, new HarrisBenedictOriginal());

            Meal meal = new Meal();

            meal.setName("salad");

            adventure.putChild(meal);

            System.out.println("trying to trigger a rebuild");

            // Render in Qute
            String renderedHtml = adventureDashboard.data("adventure", adventure).render();

            // Create component instance
            String componentInstance = createComponentInstance(renderedHtml, adventureDashboard);

            return Response.ok(componentInstance).build();

        } catch (Exception exception) {
            // Log the exception
            exception.printStackTrace();

            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Error 500, refer to stack trace.")
                    .build();
        }
    }

}




