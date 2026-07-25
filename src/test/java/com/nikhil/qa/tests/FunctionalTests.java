package com.nikhil.qa.tests;

import com.nikhil.qa.base.BaseTest;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

/**
 * FUNCTIONAL SUITE
 * Purpose: verify each endpoint behaves correctly against expected business
 * rules for valid input. This class runs a full record lifecycle against
 * the "products" collection: create -> read -> update -> delete, in order,
 * using the id captured at each step (TestNG dependsOnMethods).
 */
@Epic("Functional")
public class FunctionalTests extends BaseTest {

    private static final String COLLECTION = "/collections/products/records";

    // shared across the lifecycle chain within this class
    private static String createdRecordId;

    @Test(groups = {"functional", "regression"}, priority = 1)
    @Severity(SeverityLevel.NORMAL)
    @Feature("Record creation")
    @Description("POST a valid record and expect 201 with the submitted fields echoed back")
    public void createRecord_withValidPayload_returns201() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "QA Test Product");
        data.put("price", 19.99);

        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);

        Response response = given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .post(COLLECTION);

        response.then().statusCode(201);
        createdRecordId = response.jsonPath().getString("data.id");
        Assert.assertNotNull(createdRecordId, "Created record should be assigned an id");
        Assert.assertEquals(response.jsonPath().getString("data.data.name"), "QA Test Product");
    }

    @Test(groups = {"functional", "regression"}, priority = 2,
            dependsOnMethods = "createRecord_withValidPayload_returns201")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Record retrieval")
    @Description("GET the just-created record by id and confirm the data matches")
    public void getRecord_afterCreate_returnsRecord() {
        given()
                .spec(requestSpec)
                .when()
                .get(COLLECTION + "/" + createdRecordId)
                .then()
                .statusCode(200)
                .body("data.id", org.hamcrest.Matchers.equalTo(createdRecordId))
                .body("data.data.name", org.hamcrest.Matchers.equalTo("QA Test Product"));
    }

    @Test(groups = {"functional", "regression"}, priority = 3,
            dependsOnMethods = "createRecord_withValidPayload_returns201")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Record update")
    @Description("PUT an updated payload and confirm the response reflects the new values")
    public void updateRecord_withValidPayload_returns200() {
        Map<String, Object> data = new HashMap<>();
        data.put("name", "QA Test Product (Updated)");
        data.put("price", 29.99);

        Map<String, Object> payload = new HashMap<>();
        payload.put("data", data);

        given()
                .spec(requestSpec)
                .body(payload)
                .when()
                .put(COLLECTION + "/" + createdRecordId)
                .then()
                .statusCode(200)
                .body("data.data.name", org.hamcrest.Matchers.equalTo("QA Test Product (Updated)"));
    }

    @Test(groups = {"functional", "regression"}, priority = 4,
            dependsOnMethods = "updateRecord_withValidPayload_returns200")
    @Severity(SeverityLevel.NORMAL)
    @Feature("Record deletion")
    @Description("DELETE the record and confirm 204, then confirm a follow-up GET returns 404")
    public void deleteRecord_returns204ThenNotFound() {
        given()
                .spec(requestSpec)
                .when()
                .delete(COLLECTION + "/" + createdRecordId)
                .then()
                .statusCode(204);

        given()
                .spec(requestSpec)
                .when()
                .get(COLLECTION + "/" + createdRecordId)
                .then()
                .statusCode(404);
    }

    @Test(groups = {"functional", "regression"})
    @Severity(SeverityLevel.MINOR)
    @Feature("Pagination")
    @Description("GET the collection with a page param should return list metadata")
    public void listRecords_withPageParam_returnsMetadata() {
        given()
                .spec(requestSpec)
                .queryParam("page", 1)
                .when()
                .get(COLLECTION)
                .then()
                .statusCode(200)
                .body("data", org.hamcrest.Matchers.notNullValue());
    }
}
