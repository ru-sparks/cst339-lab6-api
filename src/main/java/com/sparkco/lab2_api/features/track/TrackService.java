package com.sparkco.lab2_api.features.track;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TrackService {

    private final TrackRepository trackRepository;

    public TrackService(TrackRepository trackRepository) {
        this.trackRepository = trackRepository;
    }

    public List<TrackDTO> getAllTracks() {
        return trackRepository.findAll().stream()
                .map(TrackMapper::toDTO)
                .collect(Collectors.toList());
    }

    public TrackDTO getTrackById(Integer trackId) {
        Optional<Track> track = trackRepository.findById(trackId);
        return track.map(TrackMapper::toDTO).orElse(null);
    }

    public TrackDTO createTrack(TrackDTO trackDTO) {
        Track track = TrackMapper.toEntity(trackDTO);
        Track savedTrack = trackRepository.save(track);
        return TrackMapper.toDTO(savedTrack);
    }

    public TrackDTO updateTrack(Integer trackId, TrackDTO trackDTO) {
        Optional<Track> existingTrack = trackRepository.findById(trackId);
        if (existingTrack.isPresent()) {
            Track track = existingTrack.get();
            track.setName(trackDTO.name());
            track.setAlbumId(trackDTO.albumId());
            track.setMediaTypeId(trackDTO.mediaTypeId());
            track.setGenreId(trackDTO.genreId());
            track.setComposer(trackDTO.composer());
            track.setMilliseconds(trackDTO.milliseconds());
            track.setBytes(trackDTO.bytes());
            track.setUnitPrice(trackDTO.unitPrice());
            Track updatedTrack = trackRepository.save(track);
            return TrackMapper.toDTO(updatedTrack);
        }
        return null;
    }

    public boolean deleteTrack(Integer trackId) {
        if (trackRepository.existsById(trackId)) {
            trackRepository.deleteById(trackId);
            return true;
        }
        return false;
    }
}
