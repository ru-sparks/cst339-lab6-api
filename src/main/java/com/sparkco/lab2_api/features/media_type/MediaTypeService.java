package com.sparkco.lab2_api.features.media_type;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MediaTypeService {

    private final MediaTypeRepository mediaTypeRepository;

    public MediaTypeService(MediaTypeRepository mediaTypeRepository) {
        this.mediaTypeRepository = mediaTypeRepository;
    }

    public List<MediaTypeDTO> getAllMediaTypes() {
        return mediaTypeRepository.findAll().stream()
                .map(MediaTypeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public MediaTypeDTO getMediaTypeById(Integer mediaTypeId) {
        Optional<MediaType> mediaType = mediaTypeRepository.findById(mediaTypeId);
        return mediaType.map(MediaTypeMapper::toDTO).orElse(null);
    }

    public MediaTypeDTO createMediaType(MediaTypeDTO mediaTypeDTO) {
        MediaType mediaType = MediaTypeMapper.toEntity(mediaTypeDTO);
        MediaType savedMediaType = mediaTypeRepository.save(mediaType);
        return MediaTypeMapper.toDTO(savedMediaType);
    }

    public MediaTypeDTO updateMediaType(Integer mediaTypeId, MediaTypeDTO mediaTypeDTO) {
        Optional<MediaType> existingMediaType = mediaTypeRepository.findById(mediaTypeId);
        if (existingMediaType.isPresent()) {
            MediaType mediaType = existingMediaType.get();
            mediaType.setName(mediaTypeDTO.name());
            MediaType updatedMediaType = mediaTypeRepository.save(mediaType);
            return MediaTypeMapper.toDTO(updatedMediaType);
        }
        return null;
    }

    public boolean deleteMediaType(Integer mediaTypeId) {
        if (mediaTypeRepository.existsById(mediaTypeId)) {
            mediaTypeRepository.deleteById(mediaTypeId);
            return true;
        }
        return false;
    }
}
