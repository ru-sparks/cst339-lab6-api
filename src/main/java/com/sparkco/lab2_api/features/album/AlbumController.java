package com.sparkco.lab2_api.features.album;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * REST controller for album CRUD operations.
 */
@RestController
@RequestMapping("/api/albums")
public class AlbumController {

    private final AlbumService albumService;

    public AlbumController(AlbumService albumService) {
        this.albumService = albumService;
    }

    /**
     * Retrieve all albums.
     *
     * @return a list of all album DTOs
     */
    @GetMapping
    public List<AlbumDTO> getAllAlbums() {
        return albumService.getAllAlbums();
    }

    /**
     * Retrieve an album by its unique identifier.
     *
     * @param albumId the ID of the album to retrieve
     * @return the album DTO if found, or 404 Not Found otherwise
     */
    @GetMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> getAlbumById(@PathVariable Integer albumId) {
        AlbumDTO albumDTO = albumService.getAlbumById(albumId);
        return albumDTO != null ? ResponseEntity.ok(albumDTO) : ResponseEntity.notFound().build();
    }

    /**
     * Create a new album.
     *
     * @param albumDTO the album data to create
     * @return the created album DTO with generated ID
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AlbumDTO createAlbum(@RequestBody AlbumDTO albumDTO) {
        return albumService.createAlbum(albumDTO);
    }

    /**
     * Update an existing album.
     *
     * @param albumId  the ID of the album to update
     * @param albumDTO the album data to apply
     * @return the updated album DTO if found, or 404 Not Found otherwise
     */
    @PutMapping("/{albumId}")
    public ResponseEntity<AlbumDTO> updateAlbum(@PathVariable Integer albumId, @RequestBody AlbumDTO albumDTO) {
        AlbumDTO updatedAlbum = albumService.updateAlbum(albumId, albumDTO);
        return updatedAlbum != null ? ResponseEntity.ok(updatedAlbum) : ResponseEntity.notFound().build();
    }

    /**
     * Delete an album by its ID.
     *
     * @param albumId the ID of the album to delete
     * @return 204 No Content when deleted, or 404 Not Found if missing
     */
    @DeleteMapping("/{albumId}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable Integer albumId) {
        if (albumService.deleteAlbum(albumId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
