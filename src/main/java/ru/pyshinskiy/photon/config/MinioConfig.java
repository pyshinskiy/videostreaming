package ru.pyshinskiy.photon.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    public static final String COMMON_BUCKET_NAME = "common";

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.username}")
    private String minioUsername;

    @Value("${minio.password}")
    private String minioPassword;

    @Bean
    public MinioClient minioClient() throws Exception {
        MinioClient client = MinioClient.builder()
                .endpoint(minioUrl)
                .credentials(minioUsername, minioPassword)
                .build();
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(COMMON_BUCKET_NAME).build())) {
            client.makeBucket(
                    MakeBucketArgs
                            .builder()
                            .bucket(COMMON_BUCKET_NAME)
                            .build()
            );
        }
        return client;
    }
}
