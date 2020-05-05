package com.springbootapp.fileoperations;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.springbootapp.fileoperations.util.FileUploadProperties;

@SpringBootApplication
@EnableConfigurationProperties ( {
	FileUploadProperties.class
})
public class FileOperationsApplication {

	public static void main(String[] args) {
		SpringApplication.run(FileOperationsApplication.class, args);
	}

}
