package com.sparkco.lab2_api.features.playlist_track;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistTrackService {

    private final PlaylistTrackRepository playlistTrackRepository;

    public PlaylistTrackService(PlaylistTrackRepository playlistTrackRepository) {
        this.playlistTrackRepository = playlistTrackRepository;
    }

    public List<PlaylistTrackDTO> getAllPlaylistTracks() {
        return playlistTrackRepository.findAll().stream()
                .map(PlaylistTrackMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PlaylistTrackDTO getPlaylistTrackById(Integer playlistId, Integer trackId) {
        Optional<PlaylistTrack> playlistTrack = playlistTrackRepository
                .findById(new PlaylistTrackId(playlistId, trackId));
        return playlistTrack.map(PlaylistTrackMapper::toDTO).orElse(null);
    }

    public PlaylistTrackDTO createPlaylistTrack(PlaylistTrackDTO playlistTrackDTO) {
        PlaylistTrack playlistTrack = PlaylistTrackMapper.toEntity(playlistTrackDTO);
        PlaylistTrack savedPlaylistTrack = playlistTrackRepository.save(playlistTrack);
        return PlaylistTrackMapper.toDTO(savedPlaylistTrack);
    }

    public boolean deletePlaylistTrack(Integer playlistId, Integer trackId) {
        PlaylistTrackId id = new PlaylistTrackId(playlistId, trackId);
        if (playlistTrackRepository.existsById(id)) {
            playlistTrackRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
