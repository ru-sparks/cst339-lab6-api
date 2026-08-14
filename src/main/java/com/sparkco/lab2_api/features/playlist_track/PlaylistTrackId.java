package com.sparkco.lab2_api.features.playlist_track;

import java.io.Serializable;
import java.util.Objects;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class PlaylistTrackId implements Serializable {

    @Column(name = "playlist_id")
    private Integer playlistId;

    @Column(name = "track_id")
    private Integer trackId;

    public PlaylistTrackId() {
    }

    public PlaylistTrackId(Integer playlistId, Integer trackId) {
        this.playlistId = playlistId;
        this.trackId = trackId;
    }

    public Integer getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public Integer getTrackId() {
        return trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlaylistTrackId that)) {
            return false;
        }
        return Objects.equals(playlistId, that.playlistId) && Objects.equals(trackId, that.trackId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playlistId, trackId);
    }
}
