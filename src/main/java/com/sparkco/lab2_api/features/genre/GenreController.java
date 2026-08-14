package com.sparkco.lab2_api.features.genre;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/genres")
public class GenreController {

    private final GenreService genreService;

    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<GenreDTO> getAllGenres() {
        return genreService.getAllGenres();
    }

    @GetMapping("/{genreId}")
    public ResponseEntity<GenreDTO> getGenreById(@PathVariable Integer genreId) {
        GenreDTO genreDTO = genreService.getGenreById(genreId);
        return genreDTO != null ? ResponseEntity.ok(genreDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public GenreDTO createGenre(@RequestBody GenreDTO genreDTO) {
        return genreService.createGenre(genreDTO);
    }

    @PutMapping("/{genreId}")
    public ResponseEntity<GenreDTO> updateGenre(@PathVariable Integer genreId, @RequestBody GenreDTO genreDTO) {
        GenreDTO updatedGenre = genreService.updateGenre(genreId, genreDTO);
        return updatedGenre != null ? ResponseEntity.ok(updatedGenre) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{genreId}")
    public ResponseEntity<Void> deleteGenre(@PathVariable Integer genreId) {
        if (genreService.deleteGenre(genreId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
