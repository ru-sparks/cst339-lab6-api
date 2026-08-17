package com.sparkco.lab2_api.features.album;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    /**
     * Page albums from PostgreSQL. When {@code title} is present, the filter runs in the
     * repository query so {@code totalElements} is the match count, not the full catalog.
     */
    public Page<AlbumDTO> getAllAlbums(String title, Pageable pageable) {
        if (title == null || title.isBlank()) {
            return albumRepository.findAll(pageable).map(AlbumMapper::toDTO);
        }
        return albumRepository.findByTitleContainingIgnoreCase(title.trim(), pageable)
                .map(AlbumMapper::toDTO);
    }

    public AlbumDTO getAlbumById(Integer albumId) {
        Optional<Album> album = albumRepository.findById(albumId);
        return album.map(AlbumMapper::toDTO).orElse(null);
    }

    public AlbumDTO createAlbum(AlbumDTO albumDTO) {
        Album album = AlbumMapper.toEntity(albumDTO);
        Album savedAlbum = albumRepository.save(album);
        return AlbumMapper.toDTO(savedAlbum);
    }

    public AlbumDTO updateAlbum(Integer albumId, AlbumDTO albumDTO) {
        Optional<Album> existingAlbum = albumRepository.findById(albumId);
        if (existingAlbum.isPresent()) {
            Album album = existingAlbum.get();
            album.setTitle(albumDTO.title());
            album.setArtistId(albumDTO.artistId());
            Album updatedAlbum = albumRepository.save(album);
            return AlbumMapper.toDTO(updatedAlbum);
        }
        return null;
    }

    public boolean deleteAlbum(Integer albumId) {
        if (albumRepository.existsById(albumId)) {
            albumRepository.deleteById(albumId);
            return true;
        }
        return false;
    }
}
