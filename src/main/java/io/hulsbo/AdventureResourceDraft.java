///*
//package io.hulsbo;
//
//import jakarta.inject.Inject;
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import java.util.UUID;
//import io.hulsbo.model.Adventure;
//import io.hulsbo.model.Manager;
//import io.hulsbo.util.CrewMember.Gender;
//import io.hulsbo.util.CrewMember.KCalCalculationStrategies.KCalCalculationStrategy;
//import io.hulsbo.util.CrewMember.PhysicalActivity;
//
//@Path("/adventures")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//public class AdventureResourceDraft {
//
//    @Inject
//    KCalCalculationStrategy kCalCalculationStrategy;
//
//    @POST
//    public Response createAdventure() {
//        Adventure adventure = new Adventure(kCalCalculationStrategy);
//        return Response.ok(adventure).build();
//    }
//
//    @GET
//    @Path("/{id}")
//    public Response getAdventure(@PathParam("id") UUID id) {
//        Adventure adventure = (Adventure) Manager.getObject(id);
//        if (adventure == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//        return Response.ok(adventure).build();
//    }
//
//    @POST
//    @Path("/{id}/crew")
//    public Response addCrewMember(
//            @PathParam("id") UUID adventureId,
//            @QueryParam("name") String name,
//            @QueryParam("age") int age,
//            @QueryParam("height") double height,
//            @QueryParam("weight") double weight,
//            @QueryParam("gender") Gender gender,
//            @QueryParam("activity") PhysicalActivity activity) {
//
//        Adventure adventure = (Adventure) Manager.getObject(adventureId);
//        if (adventure == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        adventure.addCrewMember(name, age, height, weight, gender, activity, kCalCalculationStrategy);
//        return Response.ok(adventure).build();
//    }
//
//    @PUT
//    @Path("/{id}/days")
//    public Response setDays(@PathParam("id") UUID id, @QueryParam("days") int days) {
//        Adventure adventure = (Adventure) Manager.getObject(id);
//        if (adventure == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        adventure.setDays(days);
//        return Response.ok(adventure).build();
//    }
//
//    @GET
//    @Path("/{id}/info")
//    public Response getAdventureInfo(@PathParam("id") UUID id) {
//        Adventure adventure = (Adventure) Manager.getObject(id);
//        if (adventure == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        adventure.getInfo();
//        return Response.ok("Adventure info printed to console").build();
//    }
//}*/
