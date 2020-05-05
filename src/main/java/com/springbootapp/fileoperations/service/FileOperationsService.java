package com.springbootapp.fileoperations.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.springbootapp.fileoperations.exception.FileNotFoundException;
import com.springbootapp.fileoperations.exception.FileUploadException;
import com.springbootapp.fileoperations.util.FileUploadProperties;

@Service
public class FileOperationsService {

	private final Path uploadLocation;
	
	@Autowired
    public FileOperationsService(FileUploadProperties fileProperties) {
        uploadLocation = Paths.get(fileProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(uploadLocation);
        } catch (Exception ex) {
            throw new FileUploadException("Creation of a directory to upload the files - Failed!!!", ex);
        }
    }
    
    public String createFile(String fileName, MultipartFile file) {
    	
    	try {
        	
            // Check if the input fileName has invalid characters
            if(fileName.contains("..")) {
        	    throw new FileUploadException("Filename "+ fileName + " has invalid characters ");
            }

            // create a file, replace if already exists
            Path targetLocation = uploadLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
        	throw new FileUploadException(fileName + "cannot be uploaded, try again! Exception is: ", ex);
        }
    }

    public Resource downloadFile(String fileName) {
        try {
            Path filePath = uploadLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if(resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File: " + fileName + "not found");
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File: " + fileName + "not found, exception:", ex);
        }
    }
}
