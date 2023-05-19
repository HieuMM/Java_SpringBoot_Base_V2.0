package com.vudt.login.utils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Component
public class FileUploadProvider {

    private final String bucket = "attend-project";
    private final String bucketEndpoint = "https://attend-project.s3.ap-northeast-2.amazonaws.com/";
    private final AmazonS3 s3Client;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final Executor taskExecutor;

    public FileUploadProvider(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
        this.s3Client = amazonS3ClientBuilder().build();
    }

    //Upload file to aws s3
    public String uploadFile(String folder, MultipartFile file) throws IOException {
        StringBuilder checkFileName = new StringBuilder(folder);
        checkFileName.append(file.getOriginalFilename());
        if (isFileExist(checkFileName.toString())) { //Check if file exist, make a copy with increase prefix
            int i = 1;
            while (true) {
                checkFileName.setLength(0);
                checkFileName.append(folder).append(i++).append(file.getOriginalFilename());
                if (!isFileExist(file.toString()))
                    break;
            }
        }
        String filePath = checkFileName.toString();
        s3Client.putObject(this.bucket, filePath, file.getInputStream(), null);
        return bucketEndpoint + filePath;
    }

    public String uploadFile(String folder, Part file) throws IOException {
        StringBuilder checkFileName = new StringBuilder(folder);
        checkFileName.append(file.getSubmittedFileName());
        if (isFileExist(checkFileName.toString())) { //Check if file exist, make a copy with increase prefix
            int i = 1;
            while (true) {
                checkFileName.setLength(0);
                checkFileName.append(folder).append(i++).append(file.getSubmittedFileName());
                if (!isFileExist(file.toString()))
                    break;
            }
        }
        String filePath = checkFileName.toString();
        s3Client.putObject(this.bucket, filePath, file.getInputStream(), null);
        return bucketEndpoint + filePath;
    }

    public CompletableFuture[] asyncUploadFiles(List<String> containFile, String folder, List<MultipartFile> files) {
        return files.stream().map(file -> this.asyncUpload1File(folder, file).thenAccept(path -> containFile.add(path))).toArray(CompletableFuture[]::new);
    }

    public CompletableFuture<String> asyncUpload1File(String folder, MultipartFile file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return uploadFile(folder, file);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Error when upload file {}", file.getOriginalFilename());
                return null;
            }
        }, taskExecutor);
    }

    public CompletableFuture<String> asyncUpload1File(String folder, Part file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return uploadFile(folder, file);
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Error when upload file {}", file.getSubmittedFileName());
                return null;
            }
        }, taskExecutor);
    }

    //Check if file exist
    public boolean isFileExist(String key) {
        try {
            s3Client.getObject(bucket, key);
            return true;
        } catch (Exception e) {
            log.info(key.concat(" isn't existed"));
            return false;
        }
    }

    public void deleteFile(String key) {
        System.out.println("Delete File: " + key);
        if (key != null)
          try {
              this.s3Client.deleteObject(this.bucket, key.replace(this.bucketEndpoint, ""));
          } catch (Exception e) {
              log.error("Error when delete file {}", key);
          }
    }

    //Client initialization
    public AmazonS3ClientBuilder amazonS3ClientBuilder() {
        String region = "ap-northeast-2";
        return AmazonS3ClientBuilder.standard()
                .withCredentials(credentialsProvider())
                .withRegion(region);
    }

    //Client's credential provider
    private AWSCredentialsProvider credentialsProvider() {
        String accessSecret = "CJgMYoQI7Kv/5mRQsoqcNzWHqG2KrJ2VO9mWVmyH";
        String accessKey = "AKIA2GSEWDCLRXXMMCMG";
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, accessSecret);
        return new AWSStaticCredentialsProvider(awsCredentials);
    }

}
