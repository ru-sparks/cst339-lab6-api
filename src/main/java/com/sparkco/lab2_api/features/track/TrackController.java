package com.sparkco.lab2_api.features.track;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public List<TrackDTO> getAllTracks() {
        return trackService.getAllTracks();
    }

    @GetMapping("/{trackId}")
    public ResponseEntity<TrackDTO> getTrackById(@PathVariable Integer trackId) {
        TrackDTO trackDTO = trackService.getTrackById(trackId);
        return trackDTO != null ? ResponseEntity.ok(trackDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TrackDTO createTrack(@RequestBody TrackDTO trackDTO) {
        return trackService.createTrack(trackDTO);
    }

    @PutMapping("/{trackId}")
    public ResponseEntity<TrackDTO> updateTrack(@PathVariable Integer trackId, @RequestBody TrackDTO trackDTO) {
        TrackDTO updatedTrack = trackService.updateTrack(trackId, trackDTO);
        return updatedTrack != null ? ResponseEntity.ok(updatedTrack) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteTrack(@PathVariable Integer trackId) {
        if (trackService.deleteTrack(trackId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
