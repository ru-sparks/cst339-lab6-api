package com.sparkco.lab2_api.features.user;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.sparkco.lab2_api.features.album.AlbumController;
import com.sparkco.lab2_api.features.album.AlbumDTO;
import com.sparkco.lab2_api.features.album.AlbumService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Lab 5 Branch 12: Role Authorization Policy for the REST API, Swagger, and admin pages.
 *
 * <p>{@code @WithMockUser(authorities = ...)} must use {@code authorities}, not {@code roles},
 * because the filter chain checks {@code hasAuthority("USER")} / {@code hasAuthority("ADMIN")}
 * without a {@code ROLE_} prefix.
 */
@WebMvcTest(controllers = { AlbumController.class, UserManagementController.class })
@Import(SecurityConfig.class)
class ApiAuthorizationTest {

    private static final String ALBUM_JSON = """
            {"title":"Test Album","artistId":1}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlbumService albumService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    void anonymousApiGet_isRejected() throws Exception {
        mockMvc.perform(get("/api/albums"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void anonymousSwagger_isRejected() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void userCanGetApiResource() throws Exception {
        when(albumService.getAllAlbums()).thenReturn(List.of(new AlbumDTO(1, "Test Album", 1)));

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void userIsForbiddenOnApiWrites() throws Exception {
        mockMvc.perform(post("/api/albums").contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/albums/1").contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void adminCanGetAndWriteApiResource() throws Exception {
        when(albumService.getAllAlbums()).thenReturn(List.of(new AlbumDTO(1, "Test Album", 1)));
        when(albumService.createAlbum(any(AlbumDTO.class))).thenReturn(new AlbumDTO(1, "Test Album", 1));
        when(albumService.updateAlbum(eq(1), any(AlbumDTO.class))).thenReturn(new AlbumDTO(1, "Test Album", 1));
        when(albumService.deleteAlbum(1)).thenReturn(true);

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/albums").contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(put("/api/albums/1").contentType(MediaType.APPLICATION_JSON).content(ALBUM_JSON))
                .andExpect(status().isOk());
        mockMvc.perform(delete("/api/albums/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void userCannotAccessAdminPages() throws Exception {
        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void adminCanAccessAdminPages() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of());

        mockMvc.perform(get("/admin/users"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin-users"));
    }
}
