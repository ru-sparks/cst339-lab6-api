package com.sparkco.lab2_api.features.playlist;

public class PlaylistMapper {

    public static PlaylistDTO toDTO(Playlist playlist) {
        if (playlist == null) {
            return null;
        }
        return new PlaylistDTO(playlist.getPlaylistId(), playlist.getName());
    }

    public static Playlist toEntity(PlaylistDTO playlistDTO) {
        if (playlistDTO == null) {
            return null;
        }
        Playlist playlist = new Playlist();
        if (playlistDTO.playlistId() != null) {
            playlist.setPlaylistId(playlistDTO.playlistId());
        }
        playlist.setName(playlistDTO.name());
        return playlist;
    }
}
