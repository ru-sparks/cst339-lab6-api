package com.sparkco.lab2_api.features.media_type;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/media-types")
public class MediaTypeController {

    private final MediaTypeService mediaTypeService;

    public MediaTypeController(MediaTypeService mediaTypeService) {
        this.mediaTypeService = mediaTypeService;
    }

    @GetMapping
    public List<MediaTypeDTO> getAllMediaTypes() {
        return mediaTypeService.getAllMediaTypes();
    }

    @GetMapping("/{mediaTypeId}")
    public ResponseEntity<MediaTypeDTO> getMediaTypeById(@PathVariable Integer mediaTypeId) {
        MediaTypeDTO mediaTypeDTO = mediaTypeService.getMediaTypeById(mediaTypeId);
        return mediaTypeDTO != null ? ResponseEntity.ok(mediaTypeDTO) : ResponseEntity.notFound().build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MediaTypeDTO createMediaType(@RequestBody MediaTypeDTO mediaTypeDTO) {
        return mediaTypeService.createMediaType(mediaTypeDTO);
    }

    @PutMapping("/{mediaTypeId}")
    public ResponseEntity<MediaTypeDTO> updateMediaType(@PathVariable Integer mediaTypeId,
            @RequestBody MediaTypeDTO mediaTypeDTO) {
        MediaTypeDTO updatedMediaType = mediaTypeService.updateMediaType(mediaTypeId, mediaTypeDTO);
        return updatedMediaType != null ? ResponseEntity.ok(updatedMediaType) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{mediaTypeId}")
    public ResponseEntity<Void> deleteMediaType(@PathVariable Integer mediaTypeId) {
        if (mediaTypeService.deleteMediaType(mediaTypeId)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
