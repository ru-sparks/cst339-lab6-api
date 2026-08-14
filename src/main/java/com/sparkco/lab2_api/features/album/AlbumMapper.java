package com.sparkco.lab2_api.features.album;

public class AlbumMapper {

    public static AlbumDTO toDTO(Album album) {
        if (album == null) {
            return null;
        }
        return new AlbumDTO(album.getAlbumId(), album.getTitle(), album.getArtistId());
    }

    public static Album toEntity(AlbumDTO albumDTO) {
        if (albumDTO == null) {
            return null;
        }
        Album album = new Album(albumDTO.title(), albumDTO.artistId());
        if (albumDTO.albumId() != null) {
            album.setAlbumId(albumDTO.albumId());
        }
        return album;
    }
}
