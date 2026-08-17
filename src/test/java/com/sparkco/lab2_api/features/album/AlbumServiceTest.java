package com.sparkco.lab2_api.features.album;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

/**
 * Lab 6 Branch 1: paging is executed by the repository, not {@code findAll()} plus a Java slice.
 */
@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @InjectMocks
    private AlbumService albumService;

    @Test
    void getAllAlbums_pagesThroughRepository() {
        Pageable pageable = PageRequest.of(0, 20);
        Album album = new Album("For Those About To Rock We Salute You", 1);
        album.setAlbumId(1);
        when(albumRepository.findAll(pageable))
                .thenReturn(new PageImpl<>(List.of(album), pageable, 372));

        Page<AlbumDTO> result = albumService.getAllAlbums(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(372, result.getTotalElements());
        assertEquals("For Those About To Rock We Salute You", result.getContent().get(0).title());
        verify(albumRepository).findAll(pageable);
        verify(albumRepository, never()).findAll();
    }
}
