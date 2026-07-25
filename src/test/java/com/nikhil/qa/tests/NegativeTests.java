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
 * NEGATIVE / BOUNDARY SUITE
 * Purpose: verify the API fails gracefully and predictably on bad input.
 * NOTE: a couple of assertions here are intentionally written to be
 * lenient on the exact status code (documenting acceptable ranges rather
 * than a single guessed value) since this targets a third-party API whose
 * exact validation behavior wasn't independently confirmed before writing
 * these tests. Tighten them to exact expected codes once you've run this
 * once and observed real responses -- that tightening is itself good
 * interview material (test refinement from observed behavior).
 */
@Epic("Negative & Boundary")
public class NegativeTests extends BaseTest {

    private static final String COLLECTION = "/collections/products/records";

    @Test(groups = {"negative", "regression"})
    @Severity(SeverityLevel.NORMAL)
    @Feature("Error handling")
    @Description("GET a record with a non-existent id should return 404, not a silent empty 200")
    public void getRecord_withInvalidId_returns404() {
        given()
                .spec(requestSpec)
                .when()
                .get(COLLECTION + "/nonexistent-record-id-xyz")
                .then()
                .statusCode(404);
    }

    @Test(groups = {"negative", "regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Input validation")
    @Description("POST without the required 'data' field should be rejected, not accepted as an empty record")
    public void createRecord_withMissingDataField_isRejected() {
        Map<String, Object> emptyPayload = new HashMap<>();
        // 'data' key intentionally omitted -- it's a required field per the API schema

        Response response = given()
                .spec(requestSpec)
                .body(emptyPayload)
                .when()
                .post(COLLECTION);

        // Document actual behavior here once observed; expecting a 4xx, not a 2xx or 5xx.
        Assert.assertTrue(response.statusCode() >= 400 && response.statusCode() < 500,
                "Missing required 'data' field should return a 4xx client error, got: " + response.statusCode());
    }

    @Test(groups = {"negative", "regression"})
    @Severity(SeverityLevel.NORMAL)
    @Feature("Error handling")
    @Description("Requesting a collection slug that doesn't exist should return 404, not 200 with empty data")
    public void listRecords_fromNonexistentCollection_returns404() {
        given()
                .spec(requestSpec)
                .when()
                .get("/collections/this-collection-does-not-exist/records")
                .then()
                .statusCode(404);
    }

    @Test(groups = {"negative", "regression"})
    @Severity(SeverityLevel.MINOR)
    @Feature("Boundary values")
    @Description("Requesting a limit above the documented max (100) should not cause a server error")
    public void listRecords_withOverMaxLimit_doesNotServerError() {
        given()
                .spec(requestSpec)
                .queryParam("limit", 500)
                .when()
                .get(COLLECTION)
                .then()
                .statusCode(org.hamcrest.Matchers.lessThan(500));
    }

    @Test(groups = {"negative", "regression"})
    @Severity(SeverityLevel.MINOR)
    @Feature("Boundary values")
    @Description("Requesting page=0 (below valid range) should not cause a server error")
    public void listRecords_withZeroPage_doesNotServerError() {
        given()
                .spec(requestSpec)
                .queryParam("page", 0)
                .when()
                .get(COLLECTION)
                .then()
                .statusCode(org.hamcrest.Matchers.lessThan(500));
    }
}
