package com.sparkco.lab2_api.features.album;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Integer> {

    /**
     * Case-insensitive title contains, paged in PostgreSQL — not {@code findAll()} plus a Java filter.
     */
    Page<Album> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
