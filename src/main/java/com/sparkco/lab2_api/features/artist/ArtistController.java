package com.sparkco.lab2_api.features.artist;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for artist CRUD operations.
 */
@RestController
@RequestMapping("/api/artists")
public class ArtistController {

    private final ArtistService artistService;

    public ArtistController(ArtistService artistService) {
        this.artistService = artistService;
    }

    /**
     * Retrieve all artists.
     *
     * @return a list of all artist DTOs
     */
    @GetMapping
    public List<ArtistDTO> getAllArtists() {
        return artistService.getAllArtists();
    }

    /**
     * Retrieve an artist by its unique identifier.
     *
     * @param artistId the ID of the artist to retrieve
     * @return the artist DTO if found, or 404 Not Found otherwise
     */
    @GetMapping("/{artistId}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Integer artistId) {
        ArtistDTO artistDTO = artistService.getArtistById(artistId);
        return artistDTO != null ? ResponseEntity.ok(artistDTO) : ResponseEntity.notFound().build();
    }

    /**
     * Create a new artist.
     *
     * @param artistDTO the artist data to create
     * @return the created artist DTO with generated ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtistDTO createArtist(@RequestBody ArtistDTO artistDTO) {
        return artistService.createArtist(artistDTO);
    }

    /**
     * Update an existing artist.
     *
     * @param artistId  the ID of the artist to update
     * @param artistDTO the artist data to apply
     * @return the updated artist DTO if found, or 404 Not Found otherwise
     */
    @PutMapping("/{artistId}")
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable Integer artistId, @RequestBody ArtistDTO artistDTO) {
        ArtistDTO updatedArtist = artistService.updateArtist(artistId, artistDTO);
        return updatedArtist != null ? ResponseEntity.ok(updatedArtist) : ResponseEntity.notFound().build();
    }

    /**
     * Delete an artist by its ID.
     *
     * @param artistId the ID of the artist to delete
     * @return 204 No Content when deleted, or 404 Not Found if missing
     */
    @DeleteMapping("/{artistId}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Integer artistId) {
        if (artistService.deleteArtist(artistId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
