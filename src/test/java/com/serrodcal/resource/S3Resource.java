package com.serrodcal.resource;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class S3Resource implements QuarkusTestResourceLifecycleManager {

    private final static String BUCKET_NAME = "quarkus.test.bucket";

    private LocalStackContainer s3;
    private S3Client client;

    @Override
    public Map<String, String> start() {
        DockerClientFactory.instance().client();
        try {
            DockerImageName localstackImage = DockerImageName.parse("localstack/localstack:0.12.7");
            LocalStackContainer localstack = new LocalStackContainer(localstackImage)
                    .withServices(LocalStackContainer.Service.S3);
            localstack.start();

            client = S3Client.builder()
                    .endpointOverride(new URI(localstack.getEndpointConfiguration(LocalStackContainer.Service.S3).getServiceEndpoint()))
                    .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test-key", "test-secret")))
                    .httpClientBuilder(UrlConnectionHttpClient.builder())
                    .region(Region.US_EAST_1).build();

            client.createBucket(b -> b.bucket(BUCKET_NAME));
        } catch (Exception e) {
            throw new RuntimeException("Could not start S3 localstack server", e);
        }

        Map<String, String> properties = new HashMap<>();
        properties.put("quarkus.s3.endpoint-override", endpoint());
        properties.put("quarkus.s3.aws.region", "us-east-1");
        properties.put("quarkus.s3.aws.credentials.type", "static");
        properties.put("quarkus.s3.aws.credentials.static-provider.access-key-id", "test-key");
        properties.put("quarkus.s3.aws.credentials.static-provider.secret-access-key", "test-secret");
        properties.put("bucket.name", BUCKET_NAME);

        return properties;
    }

    @Override
    public void stop() {
        if (s3 != null) {
            s3.close();
        }
    }

    private String endpoint() {
        return String.format("http://%s:%s", s3.getContainerIpAddress(), s3.getMappedPort(LocalStackContainer.Service.S3.getPort()));
    }

}
