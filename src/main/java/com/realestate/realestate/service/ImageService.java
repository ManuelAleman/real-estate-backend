package com.realestate.realestate.service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.realestate.realestate.dto.image.PresignedUrlResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {

    private final S3Presigner s3Presigner;
    private final software.amazon.awssdk.services.s3.S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    @Value("${aws.s3.presigned-url-expiration:3600}")
    private long presignedUrlExpiration;

    /**
     * Genera URLs pre-firmadas para subir imágenes a S3
     * 
     * @param count Número de URLs a generar
     * @param contentType Tipo de contenido (e.g., "image/jpeg")
     * @param folder Carpeta dentro del bucket (e.g., "estates", "characteristics")
     * @return Lista de URLs pre-firmadas
     */
    public List<PresignedUrlResponse> generatePresignedUrls(int count, String contentType, String folder) {
        List<PresignedUrlResponse> responses = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            String fileName = generateUniqueFileName(contentType);
            String key = folder + "/" + fileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofSeconds(presignedUrlExpiration))
                    .putObjectRequest(putObjectRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

            String uploadUrl = presignedRequest.url().toString();

            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", 
                    bucketName, region, key);

            responses.add(PresignedUrlResponse.builder()
                    .uploadUrl(uploadUrl)
                    .fileUrl(fileUrl)
                    .fileName(fileName)
                    .build());

            log.info("Generated presigned URL for file: {} in folder: {}", fileName, folder);
        }

        return responses;
    }

    private String generateUniqueFileName(String contentType) {
        String extension = getFileExtension(contentType);
        return UUID.randomUUID().toString() + extension;
    }


    private String getFileExtension(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> ".jpg";
            case "image/png" -> ".png";
            case "image/gif" -> ".gif";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };
    }

    public List<PresignedUrlResponse> generateEstateImageUrls(int count, String contentType) {
        return generatePresignedUrls(count, contentType, "estates");
    }

    /**
     * Elimina una imagen de S3 dado su URL
     * 
     * @param fileUrl URL completa del archivo en S3
     */
    public void deleteImage(String fileUrl) {
        try {
            String key = extractKeyFromUrl(fileUrl);
            if (key.isEmpty()) {
                log.warn("Could not extract key from URL: {}", fileUrl);
                return;
            }

            s3Client.deleteObject(builder -> builder
                    .bucket(bucketName)
                    .key(key)
                    .build());
            
            log.info("Successfully deleted image from S3: {}", key);
        } catch (Exception e) {
            log.error("Error deleting image from S3: {}", fileUrl, e);
            throw new RuntimeException("Failed to delete image from S3", e);
        }
    }

    /**
     * Elimina múltiples imágenes de S3
     * 
     * @param fileUrls Lista de URLs de archivos a eliminar
     */
    public void deleteImages(List<String> fileUrls) {
        if (fileUrls == null || fileUrls.isEmpty()) {
            return;
        }

        for (String url : fileUrls) {
            try {
                deleteImage(url);
            } catch (Exception e) {
                log.error("Failed to delete image, continuing with others: {}", url);
            }
        }
    }

    private String extractKeyFromUrl(String fileUrl) {
        String[] parts = fileUrl.split(bucketName + ".s3." + region + ".amazonaws.com/");
        return parts.length > 1 ? parts[1] : "";
    }
}
