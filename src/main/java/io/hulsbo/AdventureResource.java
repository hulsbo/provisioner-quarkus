package io.hulsbo;

import io.hulsbo.util.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;


@Path("/adventure")
public class AdventureResource {

    @Inject
    KCalCalculationStrategy kCalCalculationStrategy;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return kCalCalculationStrategy.toString();
    }
}
