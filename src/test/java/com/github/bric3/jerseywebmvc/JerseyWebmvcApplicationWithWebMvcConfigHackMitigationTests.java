package com.github.bric3.jerseywebmvc;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT,
                classes = JerseyWebmvcApplication.class)
@ActiveProfiles(profiles = {"jersey", "web-mvc-config-hack-mitigation"})
@AutoConfigureWebTestClient
public class JerseyWebmvcApplicationWithWebMvcConfigHackMitigationTests {

    @Autowired
    private WebTestClient webClient;


    @Test
    public void can_reach_jersey_jax_rs_endpoint() {
        webClient.get()
                 .uri("/jaxrs")
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(String.class)
                 .value(containsString("\"key\":\"value\""));
    }

    @Test
    public void can_reach_actuator_endpoint() {
        webClient.get()
                 .uri("/actuator/status")
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(String.class)
                 .value(containsString("\"status\":\"UP\""));
    }

    @Test
    public void can_reach_simple_url_mapping_to_doc_index() {
        webClient.get()
                 .uri("/doc/index.html")
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(String.class)
                 .value(containsString("API Documentation"));
    }

    @Test
    public void can_reach_simple_url_mapping_to_doc() {
        webClient.get()
                 .uri("/doc/")
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(String.class)
                 .value(containsString("API Documentation"));
    }

    @Test
    public void can_reach_rest_mapping() {
        webClient.get()
                 .uri("/rest/")
                 .exchange()
                 .expectStatus()
                 .isOk()
                 .expectBody(String.class)
                 .value(containsString("\"key\":\"value\""));
    }

    @Test
    public void can_reach_favicon() {
        webClient.get()
                 .uri("/favicon.ico")
                 .exchange()
                 .expectStatus()
                 .isOk();
    }

}
