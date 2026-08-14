package com.sparkco.lab2_api.features.playlist_track;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/playlist-tracks")
public class PlaylistTrackController {

    private final PlaylistTrackService playlistTrackService;

    public PlaylistTrackController(PlaylistTrackService playlistTrackService) {
        this.playlistTrackService = playlistTrackService;
    }

    @GetMapping
    public List<PlaylistTrackDTO> getAllPlaylistTracks() {
        return playlistTrackService.getAllPlaylistTracks();
    }

    @GetMapping("/{playlistId}/{trackId}")
    public ResponseEntity<PlaylistTrackDTO> getPlaylistTrackById(@PathVariable Integer playlistId,
            @PathVariable Integer trackId) {
        PlaylistTrackDTO playlistTrackDTO = playlistTrackService.getPlaylistTrackById(playlistId, trackId);
        return playlistTrackDTO != null ? ResponseEntity.ok(playlistTrackDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlaylistTrackDTO createPlaylistTrack(@RequestBody PlaylistTrackDTO playlistTrackDTO) {
        return playlistTrackService.createPlaylistTrack(playlistTrackDTO);
    }

    @DeleteMapping("/{playlistId}/{trackId}")
    public ResponseEntity<Void> deletePlaylistTrack(@PathVariable Integer playlistId, @PathVariable Integer trackId) {
        if (playlistTrackService.deletePlaylistTrack(playlistId, trackId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
