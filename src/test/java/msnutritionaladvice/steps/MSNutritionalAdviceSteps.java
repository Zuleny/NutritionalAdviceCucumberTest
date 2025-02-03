package msnutritionaladvice.steps;

import context.World;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.Transpose;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.Assert;
import util.RequestSpecificationFactory;

import java.io.IOException;
import java.util.*;

import static util.Util.jsonTemplate;

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
    public void verifyHTTPResponseCode(Integer status) {
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
}
