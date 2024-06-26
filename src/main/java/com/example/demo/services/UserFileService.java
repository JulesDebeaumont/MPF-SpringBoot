package com.example.demo.services;
import com.example.demo.controllers.exceptions.FileStorageException;
import com.example.demo.controllers.exceptions.ResourceNotFoundException;
import com.example.demo.dtos.UserFileDtos;
import com.example.demo.models.UserFile;
import com.example.demo.repositories.UserFileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;


@Service
public class UserFileService {
    @Autowired
    private UserFileRepository userFileRepository;
    private FileStorageService fileStorageService;

    public List<UserFileDtos.GetUserFileOut> getUserFiles(Long userId) {
        return userFileRepository.getUserFiles(userId);
    }

    public void uploadFiles(Long userId, List<MultipartFile> files) {
        for (MultipartFile file : files) {
            FileStorageService.FileStorageResponseWrite responseService = fileStorageService.writeUserFileToStorageAsync(file);
            if (!responseService.getIsSuccess()) {
                throw new FileStorageException(responseService.getErrors().toString()); // TODO directly throw in the storageService
            }
            UserFile userFile = new UserFile();
            userFile.setUserId(userId);
            userFile.setFilename(file.getOriginalFilename());
            userFile.setStoragePath(responseService.getFilePath().toString());
            userFile.setMimeType(file.getContentType()); // TODO
            userFileRepository.save(userFile);
        }
    }

    public UserFileDtos.DownloadOut getUserFileDownload(Long userFileId) {
        UserFile userFile = getUserFileAllColumn(userFileId);
        FileStorageService.FileStorageResponseGet responseService = fileStorageService.getFileFromStorage(userFile.getStoragePath());
        if (!responseService.getIsSuccess()) {
            throw new FileStorageException(responseService.getErrors().toString()); // TODO directly throw in the storageService
        }
        return new UserFileDtos.DownloadOut(responseService.getFileBytes(), userFile.getMimeType());
    }

    public void deleteUserFile(Long userFileId) {
        UserFile userFile = getUserFileAllColumn(userFileId);
        ApplicationService.ResponseService responseService = fileStorageService.eraseFileFromStorage(userFile.getStoragePath());
    }

    private UserFile getUserFileAllColumn(Long id) {
        Optional<UserFile> UserFile = userFileRepository.findById(id);
        if (UserFile.isEmpty()) {
            throw new ResourceNotFoundException("UserFile not found with id : " + id);
        }
        return UserFile.get();
    }
}
