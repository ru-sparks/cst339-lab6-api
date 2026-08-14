package com.sparkco.lab2_api.features.playlist_track;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "playlist_track")
public class PlaylistTrack {

    @EmbeddedId
    private PlaylistTrackId id;

    public PlaylistTrack() {
    }

    public PlaylistTrack(PlaylistTrackId id) {
        this.id = id;
    }

    public Integer getPlaylistId() {
        return this.id != null ? this.id.getPlaylistId() : null;
    }

    public void setPlaylistId(Integer playlistId) {
        if (this.id == null) {
            this.id = new PlaylistTrackId();
        }
        this.id.setPlaylistId(playlistId);
    }

    public Integer getTrackId() {
        return this.id != null ? this.id.getTrackId() : null;
    }

    public void setTrackId(Integer trackId) {
        if (this.id == null) {
            this.id = new PlaylistTrackId();
        }
        this.id.setTrackId(trackId);
    }

    public PlaylistTrackId getId() {
        return id;
    }

    public void setId(PlaylistTrackId id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PlaylistTrack{" +
                "playlistId=" + getPlaylistId() +
                ", trackId=" + getTrackId() +
                '}';
    }
}
