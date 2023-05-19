package com.github.wansors;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
class ConfigurationResourceTest {

    @Test
    void testDevProfile() {
	given().when()
		.get("/main/foo-dev.json")
		.then()
		.statusCode(200)//
		.body("file.env", equalTo("dev"));
    }

    @Test
    void testMultirepository() {
	given().when()
		.get("/main/multirepoexample-loc.json")
		.then()
		.statusCode(200)//
		.body("file.repo2.branch", equalTo("example"))//
		.body("file.repo1.branch", equalTo("main"));
    }

    @Test
    void testMultirepositoryWithProfile() {
	given().when()
		.get("/main/multirepoexample-dev.json")
		.then()
		.statusCode(200)//
		.body("file.repo2.branch", equalTo("main"))//
		.body("file.repo1.branch", equalTo("main"));
    }

}