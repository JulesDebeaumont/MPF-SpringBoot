package com.example.demo.dtos;

import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class UserFileDtos {
    public record GetUserFileOut(Long id, String filename, String mimeType, Date createdAt) {};

    public record UploadIn(List<MultipartFile> files) {};

    public record DownloadOut(byte[] rawData, String mimeType) {};
}
