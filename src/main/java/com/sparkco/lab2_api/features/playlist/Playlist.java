package com.sparkco.lab2_api.features.playlist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "playlist")
public class Playlist {

    @Id
    @Column(name = "playlist_id")
    private Integer playlistId;
    @Column(name = "name")
    private String name;

    public Integer getPlaylistId() {
        return this.playlistId;
    }

    public void setPlaylistId(Integer playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistId=" + playlistId +
                ", name='" + name + '\'' +
                '}';
    }
}
