package com.levik.discoveryserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = DiscoveryServerApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DiscoveryServerApplicationTests {

	private static final String HOST = "http://localhost:%d%s";
	private static final String TEST_USERNAME = "levik";
	private static final String TEST_PASSWORD = "password";
	private static final String TEST_PASSWORD_WRONG = "1235";


	@LocalServerPort
	private int port;

	@Test
	void shouldReturnEurekaApps() {
		//given
		String url = getHost("/eureka/apps");

		//when
		ResponseEntity<?> entity = new TestRestTemplate(TEST_USERNAME, TEST_PASSWORD)
				.getForEntity(url, Map.class);

		//then
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.OK);
	}

	@Test
	void shouldReturnToLoginPageForEurekaAppsWhenPasswordWrong() {
		//given
		String url = getHost("/eureka/apps");
		String login = getHost("/login");

		//when
		ResponseEntity<?> entity = new TestRestTemplate(TEST_USERNAME, TEST_PASSWORD_WRONG)
				.getForEntity(url, Map.class);

		//then
		assertThat(entity.getStatusCode()).isEqualTo(HttpStatus.FOUND);
		List<String> location = entity.getHeaders().get("Location");
		assertThat(location).isNotNull();
		assertThat(location).isNotEmpty();
		assertThat(location.get(0)).isEqualTo(login);
	}

	private String getHost(String path) {
		return String.format(HOST, port, path);
	}

}
