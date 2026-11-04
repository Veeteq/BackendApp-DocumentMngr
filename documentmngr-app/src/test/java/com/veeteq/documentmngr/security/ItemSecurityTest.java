package com.veeteq.documentmngr.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.veeteq.documentmngr.config.TestSecurityProblemSupportConfig;
import com.veeteq.documentmngr.exception.ExceptionHandling;
import com.veeteq.documentmngr.rest.api.ItemController;
import com.veeteq.documentmngr.rest.dto.CategoryDto;
import com.veeteq.documentmngr.rest.dto.ItemDto;
import com.veeteq.documentmngr.rest.dto.ItemRequestDto;
import com.veeteq.documentmngr.service.ItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockCookie;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static com.veeteq.documentmngr.config.ApiConstants.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@Import({SecurityConfig.class,
         ExceptionHandling.class,
         TestSecurityProblemSupportConfig.class})
public class ItemSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test Security - Should Reject Create Item Request Without Item Admin Role")
    void shouldRejectCreateuAccontWithoutItemAdminRole() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();

        var jwt = Jwt.withTokenValue("document-admin-token")
                .header("alg", "RS256")
                .subject("jmclane")
                .claim("roles", List.of("DOCUMENT_ADMIN"))
                .issuer("http://localhost:8282")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode("document-admin-token")).thenReturn(jwt);

        mockMvc.perform(post(ITEMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer document-admin-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Test Security - Should Allow Create Item Request With Item Admin Role")
    void shouldAllowCreateItemWithItemAdminRole() throws Exception {
        var cookie = new MockCookie("cookieParam", "12345");
        var transactionId = UUID.randomUUID().toString();

        var jwt = Jwt.withTokenValue("item-admin-token")
                .header("alg", "RS256")
                .subject("jmclane")
                .claim("roles", List.of("ITEM_ADMIN"))
                .issuer("http://localhost:8282")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        when(jwtDecoder.decode("item-admin-token")).thenReturn(jwt);

        var request = createSampleRequest();
        var response = createSampleResponse();
        when(itemService.save(any(ItemRequestDto.class))).thenReturn(response);

        mockMvc.perform(post(ITEMS_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .header(AUTHORIZATION, "Bearer item-admin-token")
                        .header(ACCEPT_LANGUAGE_HEADER, ACCEPT_LANGUAGE)
                        .header(TRANSACTION_ID, transactionId)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    private ItemDto createSampleResponse() {
        var itemCategory = new CategoryDto()
                .categoryId(1L)
                .categoryName("Security Category");
        var response = new ItemDto()
                .itemId(10011L)
                .itemName("Security Test Item")
                .itemCategory(itemCategory);
        return response;
    }

    private ItemRequestDto createSampleRequest() {
        var request = new ItemRequestDto()
                .itemName("Security Test Item")
                .categoryId(5L);
        return request;
    }
}