package com.sparkco.lab2_api.features.playlist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/playlists")
public class PlaylistController {

    private final PlaylistService playlistService;

    public PlaylistController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping
    public List<PlaylistDTO> getAllPlaylists() {
        return playlistService.getAllPlaylists();
    }

    @GetMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> getPlaylistById(@PathVariable Integer playlistId) {
        PlaylistDTO playlistDTO = playlistService.getPlaylistById(playlistId);
        return playlistDTO != null ? ResponseEntity.ok(playlistDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistDTO createPlaylist(@RequestBody PlaylistDTO playlistDTO) {
        return playlistService.createPlaylist(playlistDTO);
    }

    @PutMapping("/{playlistId}")
    public ResponseEntity<PlaylistDTO> updatePlaylist(@PathVariable Integer playlistId,
            @RequestBody PlaylistDTO playlistDTO) {
        PlaylistDTO updatedPlaylist = playlistService.updatePlaylist(playlistId, playlistDTO);
        return updatedPlaylist != null ? ResponseEntity.ok(updatedPlaylist) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{playlistId}")
    public ResponseEntity<Void> deletePlaylist(@PathVariable Integer playlistId) {
        if (playlistService.deletePlaylist(playlistId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
