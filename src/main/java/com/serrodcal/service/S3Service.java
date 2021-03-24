package com.serrodcal.service;

import com.serrodcal.exception.FileNullEmptyException;
import io.smallrye.mutiny.Uni;
import io.vertx.ext.web.FileUpload;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.CompletableFuture;

@Singleton
public class S3Service {

    private static final Logger log = Logger.getLogger(S3Service.class);

    @ConfigProperty(name = "bucket.name")
    String bucketName;

    @Inject
    S3AsyncClient s3Client;

    public Uni<PutObjectResponse> uploadFile(FileUpload fileUpload) {

        if (fileUpload == null || fileUpload.fileName().isEmpty()) {
            log.error("File is null or empty");
            return Uni.createFrom().failure(new FileNullEmptyException("File is null or empty"));
        }

        return Uni.createFrom().completionStage(() -> {
            try {
                log.info("Object saved successfully");
                return this.s3Client.putObject(buildPutRequest(fileUpload), AsyncRequestBody.fromFile(uploadToTemp(new FileInputStream(fileUpload.uploadedFileName()))));
            } catch (FileNotFoundException ex) {
                log.error(ex.getMessage());
                return CompletableFuture.completedFuture(ex);
            }
        }).onItem().transform(i -> (PutObjectResponse) i);
    }

    protected PutObjectRequest buildPutRequest(FileUpload fileUpload) {
        return PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileUpload.fileName())
                .contentType(fileUpload.contentType())
                .build();
    }

    protected File uploadToTemp(InputStream data) {
        File tempPath;
        try {
            tempPath = File.createTempFile("uploadS3Tmp", ".tmp");
            Files.copy(data, tempPath.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return tempPath;
    }

}
