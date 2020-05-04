package com.fileoperations.fileOperations.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fileoperations.fileOperations.model.FileUploadStatus;
import com.fileoperations.fileOperations.service.FileOperationsService;

@RestController
public class FileOperationsController {

	private static final Logger logger = LoggerFactory.getLogger(FileOperationsController.class);
	
	@Autowired
	private FileOperationsService fileOperationsService;
	
	@PostMapping("/upload")
    public FileUploadStatus upload(@RequestParam("fileName") String fileName,
    		@RequestParam("file") MultipartFile file) {
    	
		fileOperationsService.createFile(fileName, file);
		
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/download/")
                .path(fileName)
                .toUriString();

        return new FileUploadStatus(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }
	
	@PostMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileName") String fileName, HttpServletRequest request) {	
        Resource resource = fileOperationsService.downloadFile(fileName);

        // Get the file content type
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // If the content type is null, default it to octet-stream type
        if(contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
        		.contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
	
}
