package com.sparkco.lab2_api.features.album;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;

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
     * Retrieve a page of albums. Query parameters {@code page} (0-based),
     * {@code size}, and {@code sort} bind to Spring Data {@link Pageable}.
     * Default size is 20. {@code page=0} with {@code size} and {@code sort} is a top N.
     *
     * @param pageable page index, size, and optional sort from the request
     * @return a Spring Data page of album DTOs ({@code content} plus paging metadata)
     */
    @GetMapping
    @Operation(
            summary = "List albums (paged)",
            description = """
                    Returns a Spring Data Page of AlbumDTO (content plus metadata).
                    All query parameters are optional. Omit them for page 0, size 20, unsorted.
                    page=0 with size and sort is a top N (for example size=20&sort=title is a top 20 by title).
                    sort must be an Album field (title, albumId, artistId), optionally with ,desc.
                    Do not send the OpenAPI placeholder string.
                    """)
    @Parameters({
            @Parameter(
                    name = "page",
                    in = ParameterIn.QUERY,
                    required = false,
                    description = "0-based page index. Default 0.",
                    schema = @Schema(type = "integer", defaultValue = "0", example = "0")),
            @Parameter(
                    name = "size",
                    in = ParameterIn.QUERY,
                    required = false,
                    description = "Page size. Default 20. With page 0 and sort, this is a top N.",
                    schema = @Schema(type = "integer", defaultValue = "20", example = "20")),
            @Parameter(
                    name = "sort",
                    in = ParameterIn.QUERY,
                    required = false,
                    description = "Album property to sort by, optionally with direction. "
                            + "Examples: title, title,desc, albumId. Omit for database order. "
                            + "Do not send the type placeholder string.",
                    schema = @Schema(type = "string", example = "title"))
    })
    public Page<AlbumDTO> getAllAlbums(
            @Parameter(hidden = true) @PageableDefault(size = 20) Pageable pageable) {
        return albumService.getAllAlbums(pageable);
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
