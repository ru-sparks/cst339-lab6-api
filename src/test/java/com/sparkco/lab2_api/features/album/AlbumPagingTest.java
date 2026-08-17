package com.sparkco.lab2_api.features.album;

import java.util.List;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.sparkco.lab2_api.features.user.SecurityConfig;
import com.sparkco.lab2_api.features.user.UserRepository;

/**
 * Lab 6 Branches 1–2: paged {@code GET /api/albums} with optional title filter.
 */
@WebMvcTest(controllers = AlbumController.class)
@Import(SecurityConfig.class)
class AlbumPagingTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AlbumService albumService;

    @MockitoBean
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void getAlbums_returnsPagedJsonWithDefaultSize20() throws Exception {
        when(albumService.getAllAlbums(nullable(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new AlbumDTO(1, "For Those About To Rock We Salute You", 1)),
                        PageRequest.of(0, 20), 372));

        mockMvc.perform(get("/api/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(372))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(albumService).getAllAlbums(isNull(), pageableCaptor.capture());
        assertEquals(0, pageableCaptor.getValue().getPageNumber());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void getAlbums_honorsPageAndSizeQueryParameters() throws Exception {
        when(albumService.getAllAlbums(nullable(String.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new AlbumDTO(21, "Page Two Album", 2)),
                        PageRequest.of(1, 20), 372));

        mockMvc.perform(get("/api/albums").param("page", "1").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Page Two Album"))
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.totalElements").value(372));

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(albumService).getAllAlbums(isNull(), pageableCaptor.capture());
        assertEquals(1, pageableCaptor.getValue().getPageNumber());
        assertEquals(20, pageableCaptor.getValue().getPageSize());
    }

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void getAlbums_titleFilter_returnsPagedMatchesAndFilteredTotal() throws Exception {
        when(albumService.getAllAlbums(eq("greatest"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(new AlbumDTO(1, "Greatest Hits", 1)),
                        PageRequest.of(0, 1), 4));

        mockMvc.perform(get("/api/albums").param("title", "greatest").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Greatest Hits"))
                .andExpect(jsonPath("$.totalElements").value(4));

        verify(albumService).getAllAlbums(eq("greatest"), any(Pageable.class));
    }

    @Test
    @WithMockUser(username = "alice", authorities = "USER")
    void getAlbums_titleFilterWithNoMatches_returnsEmptyPage() throws Exception {
        when(albumService.getAllAlbums(eq("zzzz-no-such-title"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 20), 0));

        mockMvc.perform(get("/api/albums").param("title", "zzzz-no-such-title"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.empty").value(true));
    }
}
