package com.sparkco.lab2_api.features.genre;

public class GenreMapper {

    public static GenreDTO toDTO(Genre genre) {
        if (genre == null) {
            return null;
        }
        return new GenreDTO(genre.getGenreId(), genre.getName());
    }

    public static Genre toEntity(GenreDTO genreDTO) {
        if (genreDTO == null) {
            return null;
        }
        Genre genre = new Genre();
        if (genreDTO.genreId() != null) {
            genre.setGenreId(genreDTO.genreId());
        }
        genre.setName(genreDTO.name());
        return genre;
    }
}
