package com.fileoperations.fileOperations;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fileoperations.fileOperations.service.FileOperationsService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class FileOperationsApplicationTests {

	@Autowired
	private MockMvc mvc;

	@Mock
	private FileOperationsService fileOperationsService;

	@Test
	public void uploadFile() throws Exception {
		String fileName = "test.txt";
		MockMultipartFile multipartFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
				"Hello World".getBytes());
		
		mvc.perform(multipart("/upload")
				.file(multipartFile)
				.param("fileName", fileName))
				.andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.fileName").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.fileDownloadUri").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.fileType").isNotEmpty())
				.andExpect(MockMvcResultMatchers.jsonPath("$.size").isNotEmpty());
	}
	
	@Test
	public void downloadFile() throws Exception {
		
		mvc.perform(post("/download")
				.param("fileName", "test.txt")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.TEXT_PLAIN))
				.andExpect(content().string("Hello World"));
	}
	
	@Test
	public void downloadFileNotFound() throws Exception {
		
		mvc.perform(post("/download")
				.param("fileName", "notfound.txt")
				.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

}
