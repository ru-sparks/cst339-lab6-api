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
    /* findByTitleContainingIgnoreCase is a custom query method that is used to find albums by title. 
    custom query methods are defined in the repository interface and are used to query the database.
    custom query methods are not part of the JpaRepository interface.
    the reciepe for creating custom query methods is to prefix the method name with findBy and then the property name.
    other common prefixes are countBy, deleteBy, existsBy, and more
    so prefix+property name is the name of the custom query method.*/
    Page<Album> findByTitleContainingIgnoreCase(String title, Pageable pageable);
}
