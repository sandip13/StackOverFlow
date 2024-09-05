package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.service.impl.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controller for handling file storage operations.
 */
@RestController
@RequestMapping("/file")
public class StorageController {

    private final StorageService storageService;

    @Autowired
    public StorageController(StorageService storageService) {
        this.storageService=storageService;
    }

    /**
     * Endpoint for uploading a file.
     *
     * @param file The file to upload.
     * @return A ResponseEntity containing a message indicating the status of the upload operation.
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "file") MultipartFile file) {
        try{
            return new ResponseEntity<>(storageService.uploadFile(file), HttpStatus.OK);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    /**
     * Endpoint for downloading a file.
     *
     * @param fileName The name of the file to download.
     * @return A ResponseEntity containing the downloaded file data.
     */
    @GetMapping("/download/{fileName}")
    public ResponseEntity<?> downloadFile(@PathVariable String fileName) {
        try{
            byte[] data = storageService.downloadFile(fileName);
            ByteArrayResource resource = new ByteArrayResource(data);
            return ResponseEntity
                    .ok()
                    .contentLength(data.length)
                    .header("Content-type", "application/octet-stream")
                    .header("Content-disposition", "attachment; filename=\"" + fileName + "\"")
                    .body(resource);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }

    }
}
