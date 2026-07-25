package com.nikhil.qa.base;

import com.nikhil.qa.utils.ConfigReader;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeSuite;

/**
 * Shared setup for every test class. Keeping this separate from individual
 * test layers means Sanity/Functional/Negative/Regression suites all hit the
 * same configured environment consistently — a core STLC principle
 * (consistent test environment across cycles).
 */
public class BaseTest {

    protected static RequestSpecification requestSpec;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        RestAssured.baseURI = ConfigReader.getBaseUrl();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigReader.getBaseUrl())
                .setContentType("application/json")
                .addHeader("x-api-key", ConfigReader.getApiKey())
                .addHeader("X-Reqres-Env", "prod")
                .build();

        System.out.println("Running suite against: " + ConfigReader.getBaseUrl());
    }
}
