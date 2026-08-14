package com.sparkco.lab2_api.features.playlist_track;

public class PlaylistTrackMapper {

    public static PlaylistTrackDTO toDTO(PlaylistTrack playlistTrack) {
        if (playlistTrack == null) {
            return null;
        }
        return new PlaylistTrackDTO(playlistTrack.getPlaylistId(), playlistTrack.getTrackId());
    }

    public static PlaylistTrack toEntity(PlaylistTrackDTO playlistTrackDTO) {
        if (playlistTrackDTO == null) {
            return null;
        }
        PlaylistTrack playlistTrack = new PlaylistTrack();
        playlistTrack.setPlaylistId(playlistTrackDTO.playlistId());
        playlistTrack.setTrackId(playlistTrackDTO.trackId());
        return playlistTrack;
    }
}
