package com.merative.publisher;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@QuarkusTest
public class ArticleResourceTest {

    public static Map<Object, Object> data;
    public static Map<Object, Object> authorObject;
    @BeforeAll
    public static void initializeJSONBody(){
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","1");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("author",authorObject);
    }
    

    // Get Endpoint

    @Test
    public void testGetArticlesEndpointUnauthorizedUsers() {
        given()
                .when().get("/articles")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testGetArticlesEndpointAdministrator() {
        given().when()
                .get("/articles")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testGetArticlesEndpointAuthor() {
        given().when()
                .get("/articles")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testGetArticlesEndpointPublisher() {
        given().when()
                .get("/articles")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testGetAllUsersEndpointReviewer() {
        given().when()
                .get("/articles")
                .then()
                .assertThat()
                .statusCode(200);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testGetAllUsersEndpointPublisher() {
        given().when()
                .get("/articles")
                .then()
                .assertThat()
                .statusCode(200);
    }


    // Post Endpoint
    @Test
    public void testAddUserEndpointUnauthorizedUsers() {
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("/articles")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testAddUserEndpointAdministrator() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/articles")
                .then()
                .statusCode(403);
    }


    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testAddUserEndpointAuthor() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/articles")
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Administrator"})
    public void testAddUserEndpointPublisher() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/articles")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testAddUserEndpointReviewer() {
        given().contentType("application/json")
                .body(data)
                .when()
                .post("/articles")
                .then()
                .assertThat()
                .statusCode(403);
    }


    // Put Endpoint
    // Invalid User
    @Test
    public void testUpdateUserEndpointUnauthorizedUsers() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","1");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("author",authorObject);

        Map<String,String> testData = new HashMap<String, String>();
        testData.put("username","elonmusk@tesla.com");
        testData.put("firstName","ELON");
        testData.put("identifier","4");
        testData.put("userKey","KEY-343222");
        given().contentType("application/json")
                .body(testData)
                .when()
                .put("/articles")
                .then()
                .statusCode(401);
    }

    // Administrator - Unauthorized
    @Test
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testUpdateUserEndpointAdministrator() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","1");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("status","ReadyForReview");
        data.put("author",authorObject);

        given().contentType("application/json")
                .body(data)
                .when()
                .put("/articles")
                .then()
                .statusCode(403);
    }

    //Author changes Draft to ReadyForReview
    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testUpdateUserEndpointAuthor() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","2");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("status","ReadyForReview");
        data.put("author",authorObject);

        given().contentType("application/json")
                .body(data)
                .when()
                .put("/articles")
                .then()
                .statusCode(200);
    }

    //Author tries to change ReadyForReview status to Published
    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testUpdateArticleStatusEndpointAuthor() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","5");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("status","Published");
        data.put("author",authorObject);

        given().contentType("application/json")
                .body(data)
                .when()
                .put("/articles")
                .then()
                .statusCode(200).body(equalTo("Article currently not in draft state or doesn't have sufficient privilege"));
    }

    //Author updates the draft article data
    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testUpdateArticleDataEndpointAuthor() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","2");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("status","Draft");
        data.put("author",authorObject);

        given().contentType("application/json")
                .body(data)
                .when()
                .put("/articles")
                .then()
                .statusCode(200).body(equalTo("Author article status successfully updated"));
    }


    //Publisher updates the Approved article to Published
    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testUpdateArticleDataEndpointPublisher() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","7");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("status","Published");
        data.put("author",authorObject);

        given().contentType("application/json")
                .body(data)
                .when()
                .put("/articles")
                .then()
                .statusCode(200).body(equalTo("Article publish status successfully updated"));
    }

    //Reviewer updates the ReadyToReview article to Approved
    @Test
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testUpdateArticleDataEndpointReviewer() {
        data = new HashMap<>();
        authorObject = new HashMap<>();
        authorObject.put("authorID","3");
        authorObject.put("username","jobs@apple.com");
        authorObject.put("firstname","Steve");
        authorObject.put("lastname","Jobs");
        data.put("identifier","5");
        data.put("title","jeff");
        data.put("synopsis","New article test");
        data.put("fulltext","ABC");
        data.put("status","Approved");
        data.put("author",authorObject);

        given().contentType("application/json")
                .body(data)
                .when()
                .put("/articles")
                .then()
                .statusCode(200).body(equalTo("Article review status successfully updated"));
    }



    // Delete
    /*
    @Test
    public void testDeleteUserEndpointUnauthorizedUsers() {
        given()
                .pathParam("id",6)
                .when().delete("/articles/{id}")
                .then()
                .statusCode(401);
    }

    @Test
    @TestSecurity(user = "johndoe@acme.com", roles = {"Administrator"})
    public void testDeleteUserEndpointAdministrator() {
        given().pathParam("id",6)
                .when()
                .delete("/articles/{id}")
                .then()
                .assertThat()
                .statusCode(204);
    }

    @Test
    @TestSecurity(user = "jobs@apple.com", roles = {"Author"})
    public void testDeleteUserEndpointAuthor() {
        given().pathParam("id",6)
                .when()
                .delete("/articles/{id}")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "janedoe@acme.com", roles = {"Publisher"})
    public void testDeleteUserEndpointPublisher() {
        given().pathParam("id",6)
                .when()
                .delete("/articles/{id}")
                .then()
                .assertThat()
                .statusCode(403);
    }

    @Test
    @TestSecurity(user = "elonmusk@tesla.com", roles = {"Reviewer"})
    public void testDeleteUserEndpointReviewer() {
        given().pathParam("id",6)
                .when()
                .delete("/articles/{id}")
                .then()
                .assertThat()
                .statusCode(403);
    }

     */
}
