package msnutritionaladvice.steps;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import context.World;
import io.cucumber.cienvironment.internal.com.eclipsesource.json.Json;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import util.RequestSpecificationFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static util.Util.jsonTemplate;
import static util.Util.stringFromFile;

public class MSNutritionalAdviceSteps {
    private final World world;
    private final Properties envConfig;
    private RequestSpecification request;

    public MSNutritionalAdviceSteps(World world) {
        this.world = world;
        this.envConfig = World.envConfig;
        this.world.featureContext = World.threadLocal.get();
    }

    @Before
    public void setUp() {
        request = RequestSpecificationFactory.getInstance();
    }

    @Given("a ingredient with valid data")
    public void getIngredientValidData(@Transpose DataTable dataTable) throws IOException {
        // List<Map<String, String>> data = dataTable.asMaps(String.class,
        // String.class);
        // String name = data.get(0).get("name");
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String name = data.get("name");
        String variety = data.get("variety");
        String benefits = data.get("benefits");
        String dishCategory = data.get("dishCategory");
        Map<String, Object> valuesToTemplate = new HashMap<>();

        valuesToTemplate.put("name", name);
        valuesToTemplate.put("variety", variety);
        valuesToTemplate.put("benefits", benefits);
        valuesToTemplate.put("dishCategory", dishCategory);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msnutritionaladvice-ingredient_request"),
                valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
        // world.scenarioContext.put("generatedGuid", uuid);
    }

    @Given("a ingredient with invalid data")
    public void getIngredientInvalidData(@Transpose DataTable dataTable) throws IOException {
        // List<Map<String, String>> data = dataTable.asMaps(String.class,
        // String.class);
        // String codigo = data.get(0).get("codigo");
        Map<String, String> data = dataTable.asMap(String.class, String.class);
        String name = data.get("name");
        String variety = data.get("variety");
        String benefits = data.get("benefits");
        String dishCategory = data.get("dishCategory");

        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("name", name);
        valuesToTemplate.put("variety", variety);
        valuesToTemplate.put("benefits", benefits);
        valuesToTemplate.put("dishCategory", dishCategory);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msnutritionaladvice-ingredient_request"),
                valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for ingredient creation")
    public void submitIngredientCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("msnutritionaladvice-service_url")
                        + envConfig.getProperty("msnutritionaladvice-ingredient_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the Ingredient HTTP response is {int}")
    public void verifyIngredientHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a ingredient id is returned")
    public void checkIngredientId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
        Assert.assertTrue(responseString.matches("\"[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}\""));
        /*
         * String generatedGuid = world.scenarioContext.get("generatedGuid").toString();
         * Assert.assertTrue(responseString.contains(generatedGuid));
         */
    }

    // Recipe

    @Given("a recipe with valid data")
    public void getRecipeValidData(@Transpose DataTable dataTable) throws IOException {
        // List<Map<String, String>> data = dataTable.asMaps(String.class,
        // String.class);
        // String name = data.get(0).get("name");
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String name = data.get("name");
        String description = data.get("description");
        int preparationTime = Integer.parseInt(data.get("preparationTime"));
        int cookingTime = Integer.parseInt(data.get("cookingTime"));
        int portions = Integer.parseInt(data.get("portions"));
        String instructionsStr = data.get("instructions");
        String[] instructions = instructionsStr.split(";");

        // Creamos un mapa con los valores a utilizar en el template
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("name", name);
        valuesToTemplate.put("description", description);
        valuesToTemplate.put("preparationTime", preparationTime);
        valuesToTemplate.put("cookingTime", cookingTime);
        valuesToTemplate.put("portions", portions);
        valuesToTemplate.put("instructions", instructions);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msnutritionaladvice-recipe_request"),
                valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
        // world.scenarioContext.put("generatedGuid", uuid);
    }

    @Given("a recipe with invalid data")
    public void getRecipeInvalidData(@Transpose DataTable dataTable) throws IOException {
        // List<Map<String, String>> data = dataTable.asMaps(String.class,
        // String.class);
        // String codigo = data.get(0).get("codigo");
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String name = data.get("name");
        String description = data.get("description");
        int preparationTime = Integer.parseInt(data.get("preparationTime"));
        int cookingTime = Integer.parseInt(data.get("cookingTime"));
        int portions = Integer.parseInt(data.get("portions"));
        String instructionsStr = data.get("instructions");

        // Creamos un mapa con los valores a utilizar en el template
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("name", name);
        valuesToTemplate.put("description", description);
        valuesToTemplate.put("preparationTime", preparationTime);
        valuesToTemplate.put("cookingTime", cookingTime);
        valuesToTemplate.put("portions", portions);
        valuesToTemplate.put("instructions", instructionsStr);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msnutritionaladvice-recipe_request"),
                valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for recipe creation")
    public void submitRecipeCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("msnutritionaladvice-service_url")
                        + envConfig.getProperty("msnutritionaladvice-recipe_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the Recipe HTTP response is {int}")
    public void verifyRecipetHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a recipe id is returned")
    public void checkRecipeId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
        Assert.assertTrue(responseString.matches("\"[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}\""));
        /*
         * String generatedGuid = world.scenarioContext.get("generatedGuid").toString();
         * Assert.assertTrue(responseString.contains(generatedGuid));
         */
    }

    // MealPlan

    @Given("a mealplan with valid data")
    public void getMealPlanValidData(@Transpose DataTable dataTable) throws IOException {
        // List<Map<String, String>> data = dataTable.asMaps(String.class,
        // String.class);
        // String name = data.get(0).get("name");
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String name = data.get("name");
        String description = data.get("description");
        String goal = data.get("goal");
        int dailyCalories = Integer.parseInt(data.get("dailyCalories"));
        int dailyProtein = Integer.parseInt(data.get("dailyProtein"));
        int dailyCarbohydrates = Integer.parseInt(data.get("dailyCarbohydrates"));
        int dailyFats = Integer.parseInt(data.get("dailyFats"));
        String nutritionistId = data.get("nutritionistId");
        String patientId = data.get("patientId");

        // Creación del objeto JSON a enviar en la solicitud
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("name", name);
        valuesToTemplate.put("description", description);
        valuesToTemplate.put("goal", goal);
        valuesToTemplate.put("dailyCalories", dailyCalories);
        valuesToTemplate.put("dailyProtein", dailyProtein);
        valuesToTemplate.put("dailyCarbohydrates", dailyCarbohydrates);
        valuesToTemplate.put("dailyFats", dailyFats);
        valuesToTemplate.put("nutritionistId", nutritionistId);
        valuesToTemplate.put("patientId", patientId);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msnutritionaladvice-mealplan_request"),
                valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
        // world.scenarioContext.put("generatedGuid", uuid);
    }

    @Given("a mealplan with invalid data")
    public void getMealPlanInvalidData(@Transpose DataTable dataTable) throws IOException {
        // List<Map<String, String>> data = dataTable.asMaps(String.class,
        // String.class);
        // String codigo = data.get(0).get("codigo");
        Map<String, String> data = dataTable.asMap(String.class, String.class);

        String name = data.get("name");
        String description = data.get("description");
        String goal = data.get("goal");
        int dailyCalories = Integer.parseInt(data.get("dailyCalories"));
        int dailyProtein = Integer.parseInt(data.get("dailyProtein"));
        int dailyCarbohydrates = Integer.parseInt(data.get("dailyCarbohydrates"));
        int dailyFats = Integer.parseInt(data.get("dailyFats"));
        String nutritionistId = data.get("nutritionistId");
        String patientId = data.get("patientId");

        // Creación del objeto JSON a enviar en la solicitud
        Map<String, Object> valuesToTemplate = new HashMap<>();
        valuesToTemplate.put("name", name);
        valuesToTemplate.put("description", description);
        valuesToTemplate.put("goal", goal);
        valuesToTemplate.put("dailyCalories", dailyCalories);
        valuesToTemplate.put("dailyProtein", dailyProtein);
        valuesToTemplate.put("dailyCarbohydrates", dailyCarbohydrates);
        valuesToTemplate.put("dailyFats", dailyFats);
        valuesToTemplate.put("nutritionistId", nutritionistId);
        valuesToTemplate.put("patientId", patientId);

        String jsonAsString = jsonTemplate(envConfig.getProperty("msnutritionaladvice-mealplan_request"),
                valuesToTemplate);

        world.scenarioContext.put("requestStr", jsonAsString);
    }

    @When("request is submitted for mealplan creation")
    public void submitMealPlanCreation() {
        String payload = world.scenarioContext.get("requestStr").toString();
        Response response = request
                .accept(ContentType.JSON)
                .body(payload)
                .contentType(ContentType.JSON)
                .when().post(envConfig.getProperty("msnutritionaladvice-service_url")
                        + envConfig.getProperty("msnutritionaladvice-mealplan_api"));

        world.scenarioContext.put("response", response);
    }

    @Then("verify that the MealPlan HTTP response is {int}")
    public void verifyMealPlanHTTPResponseCode(Integer status) {
        Response response = (Response) world.scenarioContext.get("response");
        Integer actualStatusCode = response.then()
                .extract()
                .statusCode();
        Assert.assertEquals(status, actualStatusCode);
    }

    @Then("a mealplan id is returned")
    public void checkMealPlanId() {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
        Assert.assertNotNull(responseString);
        Assert.assertNotEquals("", responseString);
        Assert.assertTrue(responseString.matches("\"[a-f0-9]{8}(-[a-f0-9]{4}){4}[a-f0-9]{8}\""));
        /*
         * String generatedGuid = world.scenarioContext.get("generatedGuid").toString();
         * Assert.assertTrue(responseString.contains(generatedGuid));
         */
    }

    @When("Get all ingredients saved")
    public void getAllIngredientsSaved() {
//        Response response = request.accept(ContentType.JSON)
//                .when().get(
//                        envConfig.getProperty("msnutritionaladvice-service_url")
//                                + envConfig.getProperty("msnutritionaladvice-ingredient_api"));
        Response response = request.accept(ContentType.JSON)
                .when().get(
                        "http://localhost:61157/api" + "/Ingredient");
        world.scenarioContext.put("response", response);
    }

    @And("the ingredients are returned")
    public void theIngredientsAreReturned() throws IOException {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
//        String ingredienteJsonFormat = envConfig.getProperty("msnutritionaladvice-ingredient_request");
        String ingredienteJsonFormat = stringFromFile("C:\\wDevelopment\\NutritionalAdviceCucumberTest\\src\\test\\java\\msnutritionaladvice\\common\\ingredient.json");
        JsonArray objectJson = JsonParser.parseString(responseString).getAsJsonArray();
        JsonObject objectJsonReference = JsonParser.parseString(ingredienteJsonFormat).getAsJsonObject();
        for (JsonElement i : objectJson) {
            JsonObject object = (JsonObject) i;
            Set<String> keysReferences = objectJsonReference.keySet();
            Set<String> keysObject = object.keySet();
            for (String key : keysReferences) {
                Assert.assertTrue(keysObject.contains(key));
            }
        }
    }

    @When("Get all meal plains saved")
    public void getAllMealPlainsSaved() {
        //        Response response = request.accept(ContentType.JSON)
//                .when().get(
//                        envConfig.getProperty("msnutritionaladvice-service_url")
//                                + envConfig.getProperty("msnutritionaladvice-mealplan_api"));
        Response response = request.accept(ContentType.JSON)
                .when().get(
                        "http://localhost:61157/api" + "/MealPlan");
        world.scenarioContext.put("response", response);
    }


    @And("the meal plans are returned")
    public void theMealPlansAreReturned() throws IOException {
        Response response = (Response) world.scenarioContext.get("response");
        String responseString = response.then().extract().asString();
//        String ingredienteJsonFormat = envConfig.getProperty("msnutritionaladvice-mealplan_request");
        String ingredienteJsonFormat = stringFromFile("C:\\wDevelopment\\NutritionalAdviceCucumberTest\\src\\test\\java\\msnutritionaladvice\\common\\mealplan.json");
        JsonArray objectJson = JsonParser.parseString(responseString).getAsJsonArray();
        JsonObject objectJsonReference = JsonParser.parseString(ingredienteJsonFormat).getAsJsonObject();
        for (JsonElement i : objectJson) {
            JsonObject object = (JsonObject) i;
            Set<String> keysReferences = objectJsonReference.keySet();
            Set<String> keysObject = object.keySet();
            for (String key : keysReferences) {
                Assert.assertTrue(keysObject.contains(key));
            }
        }
    }
}
