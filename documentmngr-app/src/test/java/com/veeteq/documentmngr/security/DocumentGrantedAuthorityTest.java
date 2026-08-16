package com.veeteq.documentmngr.security;

import com.veeteq.documentmngr.config.TestSecurityProblemSupportConfig;
import com.veeteq.documentmngr.exception.ExceptionHandling;
import com.veeteq.documentmngr.rest.api.DocumentController;
import com.veeteq.documentmngr.service.DocumentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DocumentController.class)
@Import({SecurityConfig.class,
         ExceptionHandling.class,
         TestSecurityProblemSupportConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DocumentGrantedAuthorityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DocumentService documentService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    @Order(value = 0)
    @DisplayName("Test Security - Should Map Roles Claim To Authorities")
    void shouldMapRolesClaimToAuthorities() throws Exception {
        var jwt = Jwt.withTokenValue("valid-token-with-role")
                .header("alg", "RS256")
                .subject("jmclane")
                .claim("roles", List.of("DOCUMENT_ADMIN"))
                .issuer("http://localhost:8282")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode("valid-token-with-role")).thenReturn(jwt);

        mockMvc.perform(get("/test/security/authorities")
                        .header(AUTHORIZATION, "Bearer valid-token-with-role"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("DOCUMENT_ADMIN"));
    }

    @TestConfiguration
    static class TestControllerConfig {

        @RestController
        static class TestSecurityController {

            @GetMapping("/test/security/authorities")
            List<String> authorities(Authentication authentication) {
                return authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();
            }
        }
    }
}
