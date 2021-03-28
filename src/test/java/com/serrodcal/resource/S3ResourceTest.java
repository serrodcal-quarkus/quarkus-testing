package com.serrodcal.resource;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;

import static io.restassured.RestAssured.given;

@QuarkusTest
@QuarkusTestResource(S3Resource.class)
public class S3ResourceTest {

    private static final String FILE_MIMETYPE = "multipart/form-data";

    /*@Test
    public void testFileUploadEndpoint() {*/
        /*given()
          .when().get("/hello-resteasy")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));*/
        /*Assert.assertTrue(true);
    }*/

    @Test
    void testFileUploadEndpoint() {

        File file = new File("src/test/resources/test.txt");

        //Upload files

        given()
            .multiPart("filename", file)
            .multiPart("mimetype", FILE_MIMETYPE)
            .when()
            .post("/s3/upload")
                .then()
                .statusCode(HttpResponseStatus.OK.code());

    }

}