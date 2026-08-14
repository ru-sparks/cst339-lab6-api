package com.sparkco.lab2_api.features.media_type;

public class MediaTypeMapper {

    public static MediaTypeDTO toDTO(MediaType mediaType) {
        if (mediaType == null) {
            return null;
        }
        return new MediaTypeDTO(mediaType.getMediaTypeId(), mediaType.getName());
    }

    public static MediaType toEntity(MediaTypeDTO mediaTypeDTO) {
        if (mediaTypeDTO == null) {
            return null;
        }
        MediaType mediaType = new MediaType();
        if (mediaTypeDTO.mediaTypeId() != null) {
            mediaType.setMediaTypeId(mediaTypeDTO.mediaTypeId());
        }
        mediaType.setName(mediaTypeDTO.name());
        return mediaType;
    }
}
