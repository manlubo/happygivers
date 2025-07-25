package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.servlet.http.Part;

import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Object;

@Slf4j
public class S3Util {
	private static final Properties props = PropsLoaderUtil.getProperties("secret/aws_s3.properties");
	private static S3Client s3;

	private static S3Client getS3() {
		if (s3 == null) {
			s3 = S3Client.builder()
					.region(Region.of(props.getProperty("region-name")))
					.credentialsProvider(
							StaticCredentialsProvider.create(
									AwsBasicCredentials.create(
											props.getProperty("access-key"),
											props.getProperty("secret-key")
									)
							)
					).build();
		}
		return s3;
	}
//	private static final S3Client s3 = S3Client.builder()
//			.region(Region.of(props.getProperty("region-name")))
//			.credentialsProvider(
//					StaticCredentialsProvider.create(
//							AwsBasicCredentials.create(
//									props.getProperty("access-key"),
//									props.getProperty("secret-key")
//									)
//							)
//					).build();

	
	
	public static void upload(Part part, String key) {
		try {
			uploadInternal(part.getInputStream(), key, part.getSize(), part.getContentType());
		} catch (IOException e) {
			throw new RuntimeException("S3 업로드 실패", e);
		}
	}
	
	
	
//	public static void upload(File file, String key) {
//		try {
//			uploadInternal(new FileInputStream(file), key, file.length(), Files.probeContentType(file.toPath()));
//		} catch (IOException e) {
//			throw new RuntimeException("S3 업로드 실패", e);
//		}
//	}
	
	
	
	private static void uploadInternal(InputStream is, String key, long size, String contentType) {
		PutObjectRequest putReq = PutObjectRequest.builder()
				.bucket(props.getProperty("bucket-name"))
				.key(key)
				.contentType(contentType != null ? contentType : "application/octet-stream")
				.build();
						
		try (is){
			getS3().putObject(putReq, RequestBody.fromInputStream(is, size));
		} catch (IOException e) {
			throw new RuntimeException("S3 업로드 중 오류", e);
		}
	}
	

	public static void remove(String key) {
		try { DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
					.bucket(props.getProperty("bucket-name"))
					.key(key)
					.build();

			getS3().deleteObject(deleteReq);
			log.info(key);
		} catch (Exception e) {
			
			throw new RuntimeException("S3 삭제 중 오류", e);
		}
	}
	
	public static int removeAll(List<String> keys) {
	    int deletedCount = 0;

	    for (String key : keys) {
	        try {
	            DeleteObjectRequest deleteReq = DeleteObjectRequest.builder()
	                    .bucket(props.getProperty("bucket-name"))
	                    .key(key)
	                    .build();
				getS3().deleteObject(deleteReq);
	            deletedCount++;
	            log.info("[S3 DELETE] {}", key);
	        } catch (Exception e) {
	            log.warn("S3 삭제 실패: {}", key, e);
	        }
	    }

	    return deletedCount;
	}
	
	
	public static List<String> listObjects(String prefix) {
	    ListObjectsV2Request req = ListObjectsV2Request.builder()
	            .bucket(props.getProperty("bucket-name"))
	            .prefix(prefix) // 예: "upload/2025/07/08/"
	            .build();

	    ListObjectsV2Response res = getS3().listObjectsV2(req);
	    return res.contents().stream()
	            .map(S3Object::key)
	            .collect(Collectors.toList());
	}




	public static void shutdown() {
		if (s3 != null) {
			try {
				s3.close();
				s3 = null; // GC 대상 처리
				log.info("[S3Client] 연결 해제 완료");
			} catch (Exception e) {
				e.printStackTrace(); // log.warn 가능
			}
		}
	}
	
	
}
