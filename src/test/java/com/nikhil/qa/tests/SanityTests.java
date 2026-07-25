package com.nikhil.qa.tests;

import com.nikhil.qa.base.BaseTest;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * SANITY SUITE
 * Purpose: fast, shallow checks that the build under test is stable enough
 * to proceed with deeper testing. Run first, on every build (per SCRUM/CI
 * gate expectations). Should complete in seconds, not minutes.
 *
 * Targets the "products" collection, which is provisioned by default in
 * every new reqres.in free-tier project (comes pre-seeded with sample data).
 */
@Epic("Sanity")
public class SanityTests extends BaseTest {

    private static final String RECORDS_PATH = "/collections/products/records";

    @Test(groups = {"sanity"}, priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Feature("Service availability")
    @Description("Confirms the API is reachable and returns a healthy status code")
    public void apiIsReachable() {
        Response response = given()
                .spec(requestSpec)
                .when()
                .get(RECORDS_PATH);

        Assert.assertEquals(response.statusCode(), 200,
                "API should be reachable and return 200 on the products collection");
    }

    @Test(groups = {"sanity"}, priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Feature("Response contract")
    @Description("Confirms the response is valid JSON with the expected top-level shape")
    public void responseHasExpectedShape() {
        given()
                .spec(requestSpec)
                .when()
                .get(RECORDS_PATH)
                .then()
                .contentType("application/json")
                .body("$", org.hamcrest.Matchers.hasKey("data"));
    }
}
