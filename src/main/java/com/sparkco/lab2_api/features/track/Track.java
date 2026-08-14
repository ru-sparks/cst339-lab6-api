package com.sparkco.lab2_api.features.track;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "track")
public class Track {

    @Id
    @Column(name = "track_id")
    private Integer trackId;
    @Column(name = "name")
    private String name;
    @Column(name = "album_id")
    private Integer albumId;
    @Column(name = "media_type_id")
    private Integer mediaTypeId;
    @Column(name = "genre_id")
    private Integer genreId;
    @Column(name = "composer")
    private String composer;
    @Column(name = "milliseconds")
    private Integer milliseconds;
    @Column(name = "bytes")
    private Integer bytes;
    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    public Integer getTrackId() {
        return this.trackId;
    }

    public void setTrackId(Integer trackId) {
        this.trackId = trackId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAlbumId() {
        return this.albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getMediaTypeId() {
        return this.mediaTypeId;
    }

    public void setMediaTypeId(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public Integer getGenreId() {
        return this.genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getComposer() {
        return this.composer;
    }

    public void setComposer(String composer) {
        this.composer = composer;
    }

    public Integer getMilliseconds() {
        return this.milliseconds;
    }

    public void setMilliseconds(Integer milliseconds) {
        this.milliseconds = milliseconds;
    }

    public Integer getBytes() {
        return this.bytes;
    }

    public void setBytes(Integer bytes) {
        this.bytes = bytes;
    }

    public BigDecimal getUnitPrice() {
        return this.unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    @Override
    public String toString() {
        return "Track{" +
                "trackId=" + trackId +
                ", name='" + name + '\'' +
                ", albumId=" + albumId +
                ", mediaTypeId=" + mediaTypeId +
                ", genreId=" + genreId +
                ", composer='" + composer + '\'' +
                ", milliseconds=" + milliseconds +
                ", bytes=" + bytes +
                ", unitPrice=" + unitPrice +
                '}';
    }
}
