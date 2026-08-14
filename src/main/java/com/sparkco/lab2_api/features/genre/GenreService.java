package com.sparkco.lab2_api.features.genre;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GenreService {

    private final GenreRepository genreRepository;

    public GenreService(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    public List<GenreDTO> getAllGenres() {
        return genreRepository.findAll().stream()
                .map(GenreMapper::toDTO)
                .collect(Collectors.toList());
    }

    public GenreDTO getGenreById(Integer genreId) {
        Optional<Genre> genre = genreRepository.findById(genreId);
        return genre.map(GenreMapper::toDTO).orElse(null);
    }

    public GenreDTO createGenre(GenreDTO genreDTO) {
        Genre genre = GenreMapper.toEntity(genreDTO);
        Genre savedGenre = genreRepository.save(genre);
        return GenreMapper.toDTO(savedGenre);
    }

    public GenreDTO updateGenre(Integer genreId, GenreDTO genreDTO) {
        Optional<Genre> existingGenre = genreRepository.findById(genreId);
        if (existingGenre.isPresent()) {
            Genre genre = existingGenre.get();
            genre.setName(genreDTO.name());
            Genre updatedGenre = genreRepository.save(genre);
            return GenreMapper.toDTO(updatedGenre);
        }
        return null;
    }

    public boolean deleteGenre(Integer genreId) {
        if (genreRepository.existsById(genreId)) {
            genreRepository.deleteById(genreId);
            return true;
        }
        return false;
    }
}
