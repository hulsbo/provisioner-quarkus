//package io.hulsbo;
//
//import jakarta.ws.rs.*;
//import jakarta.ws.rs.core.MediaType;
//import jakarta.ws.rs.core.Response;
//import java.util.UUID;
//
//import io.hulsbo.model.Meal;
//import io.hulsbo.model.Ingredient;
//import io.hulsbo.model.Manager;
//
//@Path("/meals")
//@Produces(MediaType.APPLICATION_JSON)
//@Consumes(MediaType.APPLICATION_JSON)
//public class MealResource {
//
//    @POST
//    public Response createMeal(@QueryParam("name") String name) {
//        Meal meal = new Meal();
//        meal.setName(name);
//        return Response.ok(meal).build();
//    }
//
//    @GET
//    @Path("/{id}")
//    public Response getMeal(@PathParam("id") UUID id) {
//        Meal meal = (Meal) Manager.getObject(id);
//        if (meal == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//        return Response.ok(meal).build();
//    }
//
//    @POST
//    @Path("/{id}/ingredients")
//    public Response addIngredient(@PathParam("id") UUID mealId, @QueryParam("name") String name) {
//        Meal meal = (Meal) Manager.getObject(mealId);
//        if (meal == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        Ingredient ingredient = new Ingredient();
//        ingredient.setName(name);
//        UUID ingredientId = meal.putChild(ingredient);
//        return Response.ok(ingredientId).build();
//    }
//
//    @PUT
//    @Path("/{mealId}/ingredients/{ingredientId}")
//    public Response modifyIngredientWeight(
//            @PathParam("mealId") UUID mealId,
//            @PathParam("ingredientId") UUID ingredientId,
//            @QueryParam("weight") double weight) {
//
//        Meal meal = (Meal) Manager.getObject(mealId);
//        if (meal == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        meal.modifyWeightOfIngredient(ingredientId, weight);
//        return Response.ok(meal).build();
//    }
//
//    @GET
//    @Path("/{id}/info")
//    public Response getMealInfo(@PathParam("id") UUID id) {
//        Meal meal = (Meal) Manager.getObject(id);
//        if (meal == null) {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//
//        meal.getInfo();
//        return Response.ok("Meal info printed to console").build();
//    }
//}