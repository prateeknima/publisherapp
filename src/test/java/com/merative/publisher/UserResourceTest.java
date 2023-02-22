package com.merative.publisher;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserResourceTest {
    public static Map<String, String> data = new HashMap<String, String>();
    @BeforeAll
    public static void initializeJSONBody(){
        data.put("username","jeffbezos@amazon.com");
        data.put("firstName","jeff");
        data.put("lastName","bezos");
        data.put("userRole","Author");
    }
    // Get Endpoint

    @Test
    @Order(1)
    public void testGetAllUsersEndpointUnauthorizedUsers() {
        given()
                .when().get("/users")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testGetAllUsersEndpointAdministrator() {
        given().when()
                .get("/users")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testGetAllUsersEndpointAuthor() {
        given().when()
                .get("/users")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testGetAllUsersEndpointPublisher() {
        given().when()
                .get("/users")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testGetAllUsersEndpointReviewer() {
        given().when()
                .get("/users")
                .then()
                .assertThat()
                .statusCode(200);
    }


    // Post Endpoint

    @Test
    @Order(1)
    public void testAddUserEndpointUnauthorizedUsers() {
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("/users")
                .then()
                .statusCode(401);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testAddUserEndpointAdministrator() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/users")
                .then()
                .statusCode(200);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testAddUserEndpointAuthor() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testGenerateNewUserKeyEndpointAuthor() {

        given().contentType("application/json")
                .pathParam("id",3)
                .when()
                .get("/users/generate-new-user-key/{id}")
                .then()
                .assertThat()
                .statusCode(200).body(containsString("New key for user with ID"));;
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testAddUserEndpointPublisher() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @Order(1)
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testAddUserEndpointReviewer() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/users")
                .then()
                .assertThat()
                .statusCode(403);
    }


    // Put Endpoint
    @Test
    public void testUpdateUserEndpointUnauthorizedUsers() {
        Map<String,String> testData = new HashMap<String, String>();
        testData.put("username","elonmusk@tesla.com");
        testData.put("firstName","ELON");
        testData.put("identifier","4");


        given()
                .contentType("application/json")
                .body(testData)
                .when()
                .put("/users")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testUpdateUserEndpointAdministrator() {
        Map<String, String> testData = new HashMap<>();
        testData.put("username","johndoe@acme.com");
        testData.put("firstName","JOHN");
        testData.put("identifier","1");

        given().contentType("application/json")
                .body(testData)
                .when()
                .put("/users")
                .then()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testUpdateUserEndpointAuthor() {
        Map<String, String> testData = new HashMap<>();
        testData.put("username","jobs@apple.com");
        testData.put("firstName","JOBS");
        testData.put("identifier","3");
        testData.put("userKey","KEY-432342");

        given().contentType("application/json")
                .body(testData)
                .when()
                .put("/users")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testUpdateUserEndpointPublisher() {
        Map<String, String> testData = new HashMap<>();
        testData.put("username","janedoe@acme.com");
        testData.put("firstName","JANE");
        testData.put("identifier","2");
        testData.put("userKey","KEY-213112");
        given().contentType("application/json")
                .body(testData)
                .when()
                .put("/users")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testUpdateUserEndpointReviewer() {
        Map<String, String> testData = new HashMap<>();
        testData.put("username","elonmusk@tesla.com");
        testData.put("firstName","ELON");
        testData.put("identifier","5");
        testData.put("userKey","KEY-343277");
        given().contentType("application/json")
                .body(testData)
                .when()
                .put("/users")
                .then()
                .assertThat()
                .statusCode(403);
    }

    // Delete endpoints

    @Test
    public void testDeleteUserEndpointUnauthorizedUsers() {
        given()
                .pathParam("id",4)
                .when().delete("/users/{id}")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testDeleteUserEndpointAdministrator() {
        given().pathParam("id",7)
                .when()
                .delete("/users/{id}")
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testDeleteUserEndpointAuthor() {
        given().pathParam("id",4)
                .when()
                .delete("/users/{id}")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testDeleteUserEndpointPublisher() {
        given().pathParam("id",6)
                .when()
                .delete("/users/{id}")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testDeleteUserEndpointReviewer() {
        given().pathParam("id",6)
                .when()
                .delete("/users/{id}")
                .then()
                .assertThat()
                .statusCode(403);
    }

}
