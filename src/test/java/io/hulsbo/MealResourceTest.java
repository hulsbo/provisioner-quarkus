package io.hulsbo;

import io.quarkus.test.junit.QuarkusTest;
import io.hulsbo.model.Meal;
import io.hulsbo.util.model.SafeID;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsString;

@QuarkusTest
@TestMethodOrder(OrderAnnotation.class)
public class MealResourceTest {

	private static String mealId;
	private static String ingredientId;

	@Test
	@Order(1)
	public void testCreateMeal() {
		Response response = given()
				.contentType(ContentType.JSON)
				.when()
				.queryParam("name", "Test Meal")
				.post("/meals")
				.then()
				.statusCode(200)
				.body("id", notNullValue())
				.body("name", is("Test Meal"))
				.extract().response();

		// Extract the meal ID for subsequent tests
		mealId = response.jsonPath().getString("id");
	}

	@Test
	@Order(2)
	public void testGetMeal() {
		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/meals/{id}", mealId)
				.then()
				.statusCode(200)
				.body("name", is("Test Meal"));
	}

	@Test
	@Order(3)
	public void testGetMealNotFound() {
		// Use a non-existent ID
		String nonExistentId = SafeID.randomSafeID().toString();

		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/meals/{id}", nonExistentId)
				.then()
				.statusCode(404);
	}

	@Test
	@Order(4)
	public void testAddIngredient() {
		Response response = given()
				.contentType(ContentType.JSON)
				.when()
				.queryParam("name", "Test Ingredient")
				.post("/meals/{id}/ingredients", mealId)
				.then()
				.statusCode(200)
				.extract().response();

		// Extract the ingredient ID for subsequent tests
		ingredientId = response.asString().replaceAll("\"", "");
	}

	@Test
	@Order(5)
	public void testModifyIngredientWeight() {
		given()
				.contentType(ContentType.JSON)
				.when()
				.queryParam("weight", 100.0)
				.put("/meals/{mealId}/ingredients/{ingredientId}", mealId, ingredientId)
				.then()
				.statusCode(200);
	}

	@Test
	@Order(6)
	public void testGetMealInfo() {
		given()
				.contentType(ContentType.JSON)
				.when()
				.get("/meals/{id}/info", mealId)
				.then()
				.statusCode(200)
				.body(containsString("Meal info printed to console"));
	}
}