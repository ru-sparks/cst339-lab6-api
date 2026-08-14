package com.sparkco.lab2_api.features.media_type;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "media_type")
public class MediaType {

    @Id
    @Column(name = "media_type_id")
    private Integer mediaTypeId;
    @Column(name = "name")
    private String name;

    public Integer getMediaTypeId() {
        return this.mediaTypeId;
    }

    public void setMediaTypeId(Integer mediaTypeId) {
        this.mediaTypeId = mediaTypeId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "MediaType{" +
                "mediaTypeId=" + mediaTypeId +
                ", name='" + name + '\'' +
                '}';
    }
}
