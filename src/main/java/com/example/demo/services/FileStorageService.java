package com.example.demo.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FileStorageService extends ApplicationService {

    public FileStorageService() {}

    public FileStorageResponseWrite writeFileToStorageAsync(MultipartFile file, String relativePath) {
        FileStorageResponseWrite response = new FileStorageResponseWrite();
        Path filePath = Paths.get(this.getMainPathStorage(), relativePath, FileStorageService.getRandomFilename());
        response.setFilePath(filePath);
        try {
            byte[] bytesFile = file.getBytes();
            Files.write(filePath, bytesFile);
            response.setSuccess();
        } catch(IOException e) {
            response.addErrors(e.getMessage());
            return response;
        }
        return response;
    }

    public FileStorageResponseWrite writeUserFileToStorageAsync(MultipartFile file) {
        FileStorageResponseWrite response = new FileStorageResponseWrite();
        ApplicationService.ResponseService responseIsFileOk = UserFileOptions.isUserFileOk(file);
        if (!responseIsFileOk.getIsSuccess()) {
            response.setErrors(responseIsFileOk.getErrors());
            return response;
        }
        return this.writeFileToStorageAsync(file, UserFileOptions.folderName);
    }

    public FileStorageResponseWrite writeProjectFileToStorageAsync(MultipartFile file) {
        FileStorageResponseWrite response = new FileStorageResponseWrite();
        ApplicationService.ResponseService responseIsFileOk = ProjectFileOptions.isProjectFileOk(file);
        if (!responseIsFileOk.getIsSuccess()) {
            response.setErrors(responseIsFileOk.getErrors());
            return response;
        }
        return this.writeFileToStorageAsync(file, ProjectFileOptions.folderName);
    }

    public ResponseService eraseFileFromStorage(String relativePath) {
        ResponseService response = new ResponseService();
        Path filePath = Paths.get(this.getMainPathStorage(), relativePath);
        if (!Files.exists(filePath)) {
            response.addErrors("File does not exist");
            return response;
        }
        try {
            Files.delete(filePath);
            response.setSuccess();
        }  catch(IOException e) {
            response.addErrors(e.getMessage());
            return response;
        }
        return response;
    }

    public FileStorageResponseGet getFileFromStorage(String relativePath) {
        FileStorageResponseGet response = new FileStorageResponseGet();
        Path filePath = Paths.get(this.getMainPathStorage(), relativePath);
        if (!Files.exists(filePath)) {
            response.addErrors("File does not exist");
            return response;
        }
        try {
            response.setFileBytes(Files.readAllBytes(filePath));
            response.setSuccess();
        }  catch(IOException e) {
            response.addErrors(e.getMessage());
            return response;
        }
        return response;
    }

    private String getMainPathStorage() {
        return "";
    }

    private static String getRandomFilename() {
        Instant now = Instant.now();
        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
                .appendLiteral('Z')
                .toFormatter();
        String timestamp = formatter.format(now);
        return timestamp + "-" + UUID.randomUUID();
    }

    public static String getFileExtension(MultipartFile file) {
        String filename = file.getOriginalFilename();
        if (filename == null) {
            return null;
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return null;
        } else {
            return filename.substring(dotIndex);
        }
    }

    private static class FileStorageResponseWrite extends ResponseService {
        public Path getFilePath() {
            return filePath;
        }

        public void setFilePath(Path filePath) {
            this.filePath = filePath;
        }

        private Path filePath;
    }

    private static class FileStorageResponseGet extends ResponseService {
        public byte[] getFileBytes() {
            return fileBytes;
        }

        public void setFileBytes(byte[] fileBytes) {
            this.fileBytes = fileBytes;
        }

        private byte[] fileBytes;
    }

    private static class UserFileOptions {
        public final static String folderName = "UserFile";
        private final static List<String> permittedExtensions = Arrays.asList(".pdf", ".csv", ".docx", ".json", ".png", ".jpeg");
        private final static Long maxFileLength = 5L * 1024L * 1024L; // 5mB

        public static ResponseService isUserFileOk(MultipartFile file) {
            ApplicationService.ResponseService response = new ResponseService();
            if (file.isEmpty()) {
                response.addErrors("File is empty");
                return response;
            }
            if (!UserFileOptions.permittedExtensions.contains(FileStorageService.getFileExtension(file))) {
                response.addErrors("File extension is not accepted");
                return response;
            }
            long fileSize = file.getSize();
            if (fileSize > UserFileOptions.maxFileLength) {
                response.addErrors(String.format("File is too big : %d > %d", fileSize, UserFileOptions.maxFileLength));
                return response;
            }
            response.setSuccess();
            return response;
        }
    }

    private static class ProjectFileOptions {
        public final static String folderName = "UserFile";
        private final static List<String> permittedExtensions = Arrays.asList(".pdf", ".csv", ".docx", ".png", ".jpeg");
        private final static Long maxFileLength = 5L * 1024L * 1024L; // 5mB

        public static ResponseService isProjectFileOk(MultipartFile file) {
            ApplicationService.ResponseService response = new ResponseService();
            if (file.isEmpty()) {
                response.addErrors("File is empty");
                return response;
            }
            if (!ProjectFileOptions.permittedExtensions.contains(FileStorageService.getFileExtension(file))) {
                response.addErrors("File extension is not accepted");
                return response;
            }
            long fileSize = file.getSize();
            if (fileSize > ProjectFileOptions.maxFileLength) {
                response.addErrors(String.format("File is too big : %d > %d", fileSize, ProjectFileOptions.maxFileLength));
                return response;
            }
            response.setSuccess();
            return response;
        }
    }
}
