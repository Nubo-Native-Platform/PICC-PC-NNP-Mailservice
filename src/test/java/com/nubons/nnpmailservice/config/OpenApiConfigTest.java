package com.nubons.nnpmailservice.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.swagger.v3.oas.models.OpenAPI;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class OpenApiConfigTest {

    @Test
    @DisplayName("Verify OpenAPI bean configuration and metadata")
    void testCustomOpenAPI() {
        OpenApiConfig config = new OpenApiConfig();
        ReflectionTestUtils.setField(config, "serverPort", "8080");

        OpenAPI openAPI = config.customOpenAPI();

        assertNotNull(openAPI);
        assertNotNull(openAPI.getInfo());
        assertEquals("NNP Mail Service API", openAPI.getInfo().getTitle());
        assertEquals("0.0.1", openAPI.getInfo().getVersion());
        assertNotNull(openAPI.getInfo().getContact());
        assertEquals("Nubons Platform Team", openAPI.getInfo().getContact().getName());
        assertEquals("support@nubons.com", openAPI.getInfo().getContact().getEmail());
        assertNotNull(openAPI.getInfo().getLicense());
        assertEquals("Apache 2.0", openAPI.getInfo().getLicense().getName());
        assertNotNull(openAPI.getServers());
        assertTrue(openAPI.getServers().size() >= 2);
    }
}
