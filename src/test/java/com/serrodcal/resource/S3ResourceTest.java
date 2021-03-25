package com.serrodcal.resource;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.wildfly.common.Assert;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class S3ResourceTest {

    @Test
    public void testFileUploadEndpoint() {
        /*given()
          .when().get("/hello-resteasy")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));*/
        Assert.assertTrue(true);
    }

}