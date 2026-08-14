package com.sparkco.lab2_api.features.album;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;

    public AlbumService(AlbumRepository albumRepository) {
        this.albumRepository = albumRepository;
    }

    public List<AlbumDTO> getAllAlbums() {
        return albumRepository.findAll().stream()
                .map(AlbumMapper::toDTO)
                .collect(Collectors.toList());
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
