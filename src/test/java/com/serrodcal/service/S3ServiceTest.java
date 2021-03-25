package com.serrodcal.service;

import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.FileUpload;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.inject.Inject;

import java.util.concurrent.CompletableFuture;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@QuarkusTest
public class S3ServiceTest {

    @Inject
    S3Service s3Service;

    @Test
    public void uploadFileShouldReturnAPutObjectResponse () {

        FileUpload fileUpload = mock(FileUpload.class);

        S3AsyncClient s3Client = mock(S3AsyncClient.class);
        s3Service.s3Client = s3Client;

        PutObjectResponse putObjectResponse = PutObjectResponse.builder().bucketKeyEnabled(true).build();

        when(fileUpload.fileName()).thenReturn("src/test/resources/test.txt");
        when(fileUpload.contentType()).thenReturn("text/plain");
        when(fileUpload.uploadedFileName()).thenReturn("src/test/resources/test.txt");
        when(s3Client.putObject(any(PutObjectRequest.class), any(AsyncRequestBody.class)))
                .thenReturn(CompletableFuture.completedFuture(putObjectResponse));

        Uni<PutObjectResponse> result = s3Service.uploadFile(fileUpload);

        PutObjectResponse response = result.await().indefinitely();

        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.bucketKeyEnabled());
        
    }

}
