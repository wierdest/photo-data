package com.wierdest.photo_data;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

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
			"http://localhost:" + port + "/upload",
			null,
			String.class

		);

		assertThat(response.getStatusCode().is4xxClientError()).isFalse();
		assertThat(response.getStatusCode().is5xxServerError()).isFalse();
	}


}
