package com.sparkco.lab2_api.features.track;

import java.math.BigDecimal;

public record TrackDTO(Integer trackId, String name, Integer albumId, Integer mediaTypeId, Integer genreId,
        String composer, Integer milliseconds, Integer bytes, BigDecimal unitPrice) {
}
