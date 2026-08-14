package com.sparkco.lab2_api.features.artist;

public class ArtistMapper {

    public static ArtistDTO toDTO(Artist artist) {
        if (artist == null) {
            return null;
        }
        return new ArtistDTO(artist.getArtistId(), artist.getName());
    }

    public static Artist toEntity(ArtistDTO artistDTO) {
        if (artistDTO == null) {
            return null;
        }
        Artist artist = new Artist(artistDTO.name());
        if (artistDTO.artistId() != null) {
            artist.setArtistId(artistDTO.artistId());
        }
        return artist;
    }
}
