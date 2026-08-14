package com.sparkco.lab2_api.features.track;

public class TrackMapper {

    public static TrackDTO toDTO(Track track) {
        if (track == null) {
            return null;
        }
        return new TrackDTO(track.getTrackId(), track.getName(), track.getAlbumId(), track.getMediaTypeId(),
                track.getGenreId(), track.getComposer(), track.getMilliseconds(), track.getBytes(),
                track.getUnitPrice());
    }

    public static Track toEntity(TrackDTO trackDTO) {
        if (trackDTO == null) {
            return null;
        }
        Track track = new Track();
        if (trackDTO.trackId() != null) {
            track.setTrackId(trackDTO.trackId());
        }
        track.setName(trackDTO.name());
        track.setAlbumId(trackDTO.albumId());
        track.setMediaTypeId(trackDTO.mediaTypeId());
        track.setGenreId(trackDTO.genreId());
        track.setComposer(trackDTO.composer());
        track.setMilliseconds(trackDTO.milliseconds());
        track.setBytes(trackDTO.bytes());
        track.setUnitPrice(trackDTO.unitPrice());
        return track;
    }
}
