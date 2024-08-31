package com.stackoverflow.beta.controller;

import com.stackoverflow.beta.service.impl.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class StorageControllerTest {
    @Mock
    private StorageService storageService;

    @InjectMocks
    private StorageController storageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testUploadFile_Success() {
        // Mock MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World Test!".getBytes());

        String uploadMessageResponse = "file_123";
        when(storageService.uploadFile(any(MultipartFile.class))).thenReturn(uploadMessageResponse);

        ResponseEntity<?> response = storageController.uploadFile(file);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(uploadMessageResponse, response.getBody());
    }

    @Test
    void testUploadFile_Failure() {
        // Mock MultipartFile
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "Hello World Test!".getBytes());

        // Set up mock service response
        when(storageService.uploadFile(any(MultipartFile.class))).thenThrow(new RuntimeException("Error occurred uploading file"));
        ResponseEntity<?> response = storageController.uploadFile(file);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred uploading file", response.getBody());
    }

    @Test
    void testDownloadFile_Success() {
        // Mock file name
        String fileName = "test.txt";
        byte[] fileData = "Hello World!".getBytes();

        when(storageService.downloadFile(anyString())).thenReturn(fileData);
        ResponseEntity<?> response = storageController.downloadFile(fileName);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ByteArrayResource(fileData), response.getBody());
    }
    @Test
    void testDownloadFile_Failure() {
        String fileName = "test.txt";
        byte[] fileData = "Hello World!".getBytes();
        when(storageService.downloadFile(anyString())).thenThrow(new RuntimeException("Error occurred downloading file"));
        ResponseEntity<?> response = storageController.downloadFile(fileName);

        // Verify the response
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Error occurred downloading file", response.getBody());
    }
}
