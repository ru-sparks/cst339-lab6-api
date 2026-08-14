package com.sparkco.lab2_api.features.playlist;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PlaylistService {

    private final PlaylistRepository playlistRepository;

    public PlaylistService(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    public List<PlaylistDTO> getAllPlaylists() {
        return playlistRepository.findAll().stream()
                .map(PlaylistMapper::toDTO)
                .collect(Collectors.toList());
    }

    public PlaylistDTO getPlaylistById(Integer playlistId) {
        Optional<Playlist> playlist = playlistRepository.findById(playlistId);
        return playlist.map(PlaylistMapper::toDTO).orElse(null);
    }

    public PlaylistDTO createPlaylist(PlaylistDTO playlistDTO) {
        Playlist playlist = PlaylistMapper.toEntity(playlistDTO);
        Playlist savedPlaylist = playlistRepository.save(playlist);
        return PlaylistMapper.toDTO(savedPlaylist);
    }

    public PlaylistDTO updatePlaylist(Integer playlistId, PlaylistDTO playlistDTO) {
        Optional<Playlist> existingPlaylist = playlistRepository.findById(playlistId);
        if (existingPlaylist.isPresent()) {
            Playlist playlist = existingPlaylist.get();
            playlist.setName(playlistDTO.name());
            Playlist updatedPlaylist = playlistRepository.save(playlist);
            return PlaylistMapper.toDTO(updatedPlaylist);
        }
        return null;
    }

    public boolean deletePlaylist(Integer playlistId) {
        if (playlistRepository.existsById(playlistId)) {
            playlistRepository.deleteById(playlistId);
            return true;
        }
        return false;
    }
}
