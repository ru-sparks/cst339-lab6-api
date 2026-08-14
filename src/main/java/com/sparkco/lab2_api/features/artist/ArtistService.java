package com.sparkco.lab2_api.features.artist;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(ArtistMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ArtistDTO getArtistById(Integer artistId) {
        Optional<Artist> artist = artistRepository.findById(artistId);
        return artist.map(ArtistMapper::toDTO).orElse(null);
    }

    public ArtistDTO createArtist(ArtistDTO artistDTO) {
        Artist artist = ArtistMapper.toEntity(artistDTO);
        Artist savedArtist = artistRepository.save(artist);
        return ArtistMapper.toDTO(savedArtist);
    }

    public ArtistDTO updateArtist(Integer artistId, ArtistDTO artistDTO) {
        Optional<Artist> existingArtist = artistRepository.findById(artistId);
        if (existingArtist.isPresent()) {
            Artist artist = existingArtist.get();
            artist.setName(artistDTO.name());
            Artist updatedArtist = artistRepository.save(artist);
            return ArtistMapper.toDTO(updatedArtist);
        }
        return null;
    }

    public boolean deleteArtist(Integer artistId) {
        if (artistRepository.existsById(artistId)) {
            artistRepository.deleteById(artistId);
            return true;
        }
        return false;
    }
}
