package com.wierdest.photo_data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

import com.wierdest.photo_data.DTOs.InfoDTO;
import com.wierdest.photo_data.controllers.HelloController;
import com.wierdest.photo_data.controllers.PhotoController;

// tests the behaviour of the appplication, connecting the server

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class PhotoDataApplicationTests {

	@Autowired
	private HelloController helloController;

	@Autowired 
	private PhotoController photoController;

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	private int port;

	@Test
	void contextLoads() {
		assertThat(helloController).isNotNull();
		assertThat(photoController).isNotNull();
	}

	@Test
	void helloParamShouldReturnDefaultMessage() {
		assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/hello/param?message=", String.class))
			.contains("Hello, World!");
	}

	@Test
	void helloPathShouldReturnDefaultMessage() {
		String message = "Path";
		assertThat(this.testRestTemplate.getForObject("http://localhost:" + port + "/hello/path/{message}", String.class, message))
			.contains("Hello, Path!");
	}

	@Test
	void photosUploadShouldExist() {
		ResponseEntity<String> response = this.testRestTemplate.postForEntity(
			"http://localhost:" + port + "/photos/upload",
			null,
			String.class

		);
		assertThat(response.getStatusCode().isSameCodeAs(HttpStatus.NOT_FOUND)).isFalse();
	}

	@Test
	void photosUploadShouldReceiveCorrectBody() {
		ClassPathResource mockFile = new ClassPathResource("test-image-png.png");
		InfoDTO mockInfo = new InfoDTO();
		mockInfo.setDescriptor("some descriptor string");

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("file", mockFile);
		body.add("info", mockInfo);

		HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

		ResponseEntity<String> response = this.testRestTemplate.postForEntity(
			"http://localhost:" + port + "/photos/upload",
			requestEntity,
			String.class
		);

		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

}
