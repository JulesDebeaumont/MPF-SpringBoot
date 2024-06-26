package com.example.demo.controllers;

import com.example.demo.dtos.UserFileDtos;
import com.example.demo.services.UserFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "userfiles")
public class UserFileController {
    @Autowired
    private UserFileService userFileService;

    @GetMapping(path="/")
    public ResponseEntity<?> getFiles() {
        return ResponseEntity.ok(userFileService.getUserFiles(1L)); // TODO
    }

    @PostMapping(path = "/upload")
    public ResponseEntity<?> uploadFiles(@RequestBody UserFileDtos.UploadIn filesPayload) {
        userFileService.uploadFiles(1L, filesPayload.files());  // TODO
        return ResponseEntity.ok().build();
    }

    @GetMapping(path = "download/{id]")
    public ResponseEntity<?> downloadFile(Long fileId) {
        UserFileDtos.DownloadOut fileData = userFileService.getUserFileDownload(fileId);
        return ResponseEntity.ok().contentType(MediaType.valueOf(fileData.mimeType())).body(fileData.rawData());
    }

    @DeleteMapping(path = "/{id")
    public ResponseEntity<?> deleteFile(Long fileId) {
        userFileService.deleteUserFile(fileId);
        return ResponseEntity.ok().build();
    }
}
