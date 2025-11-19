package learn.spring.prem.spring5webapp.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;

@Service
public class S3Service {

    private final S3Client s3Client;
    private static final String BUCKET_NAME = "book-covers";

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void createBucketIfNotExists() {
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(BUCKET_NAME).build());
        } catch (NoSuchBucketException e) {
            s3Client.createBucket(CreateBucketRequest.builder().bucket(BUCKET_NAME).build());
        }
    }

    public void uploadBookCover(String bookId, byte[] imageData) {
        String key = "covers/" + bookId + ".jpg";
        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build(),
                RequestBody.fromBytes(imageData)
        );
    }

    public byte[] downloadBookCover(String bookId) {
        String key = "covers/" + bookId + ".jpg";
        return s3Client.getObjectAsBytes(
                GetObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build()
        ).asByteArray();
    }

    public void deleteBookCover(String bookId) {
        String key = "covers/" + bookId + ".jpg";
        s3Client.deleteObject(
                DeleteObjectRequest.builder()
                        .bucket(BUCKET_NAME)
                        .key(key)
                        .build()
        );
    }
}
