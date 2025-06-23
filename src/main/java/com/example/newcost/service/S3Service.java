package com.example.newcost.service;

import com.example.newcost.model.S3BucketDTO;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class S3Service {

    public List<S3BucketDTO> listBuckets(String accessKey, String secretKey, String region) {
        S3Client s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(
                        StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        ListBucketsRequest request = ListBucketsRequest.builder().build();
        ListBucketsResponse response = s3Client.listBuckets(request);

        return response.buckets().stream()
                .map(bucket -> {
                    long sizeInBytes = getBucketSizeInBytes(s3Client, bucket.name());
                    String bucketType = getBucketType(s3Client, bucket.name());
                    String bucketRegion = getBucketRegion(s3Client, bucket.name());
                    String recommendation = getBucketRecommendation(bucketType, sizeInBytes);
                    String estimatedSavings = estimateMonthlySavings(bucketType, sizeInBytes);

                    return new S3BucketDTO(
                            bucket.name(),
                            formatStorageSize(sizeInBytes),
                            bucketType,
                            bucketRegion,
                            recommendation,
                            estimatedSavings
                    );
                })
                .collect(Collectors.toList());
    }

    private String formatStorageSize(long sizeInBytes) {
        if (sizeInBytes < 1024) {
            return String.format("%d Bytes", sizeInBytes);
        } else if (sizeInBytes < 1024 * 1024) {
            return String.format("%.2f KB", sizeInBytes / 1024.0);
        } else if (sizeInBytes < 1024 * 1024 * 1024) {
            return String.format("%.2f MB", sizeInBytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.2f GB", sizeInBytes / (1024.0 * 1024.0 * 1024.0));
        }
    }

    private String getBucketType(S3Client s3Client, String bucketName) {
        try {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .maxKeys(1)
                    .build();
            ListObjectsV2Response response = s3Client.listObjectsV2(request);

            if (!response.contents().isEmpty()) {
                return response.contents().get(0).storageClassAsString();
            }
        } catch (Exception ignored) {
        }
        return "STANDARD";
    }

    private String getBucketRegion(S3Client s3Client, String bucketName) {
        try {
            GetBucketLocationResponse response = s3Client.getBucketLocation(
                    GetBucketLocationRequest.builder().bucket(bucketName).build()
            );
            String regionCode = response.locationConstraintAsString();

            if (regionCode == null || regionCode.isEmpty()) {
                return "us-east-1";
            } else if ("EU".equalsIgnoreCase(regionCode)) {
                return "eu-west-1";
            } else {
                return regionCode;
            }
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to fetch bucket region: " + e.getMessage(), e);
        }
    }

    private String getBucketRecommendation(String bucketType, long sizeInBytes) {
        if ("STANDARD".equals(bucketType)) {
            if (sizeInBytes > 1_000_000_000) {
                return "Consider moving to Intelligent-Tiering for cost savings";
            } else if (sizeInBytes > 100_000_000) {
                return "Consider Standard-IA for infrequently accessed data";
            }
        }
        return "No recommendation";
    }

    private long getBucketSizeInBytes(S3Client s3Client, String bucketName) {
        long totalSize = 0;
        String continuationToken = null;

        do {
            ListObjectsV2Request request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .continuationToken(continuationToken)
                    .build();

            ListObjectsV2Response response = s3Client.listObjectsV2(request);
            totalSize += response.contents().stream()
                    .mapToLong(S3Object::size)
                    .sum();

            continuationToken = response.nextContinuationToken();
        } while (continuationToken != null);

        return totalSize;
    }

    private String estimateMonthlySavings(String storageClass, long sizeInBytes) {
        double standardRate = 0.023;
        double intelligentTieringRate = 0.0125;
        double standardIARate = 0.0125;

        double sizeInGB = sizeInBytes / (1024.0 * 1024.0 * 1024.0);

        double currentCost = sizeInGB * standardRate;
        double estimatedCost = currentCost;

        if ("STANDARD".equals(storageClass)) {
            if (sizeInBytes > 1_000_000_000) {
                estimatedCost = sizeInGB * intelligentTieringRate;
            } else if (sizeInBytes > 100_000_000) {
                estimatedCost = sizeInGB * standardIARate;
            }
        }

        double savings = currentCost - estimatedCost;
        return savings > 0 ? String.format("$%.2f", savings) : "$0.00";
    }

}
