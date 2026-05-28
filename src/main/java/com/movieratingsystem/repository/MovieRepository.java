package com.movieratingsystem.repository;

import com.movieratingsystem.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {

    Optional<Movie> findByTitle(String title);

    List<Movie> findByGenre(String genre);

    List<Movie> findByReleaseYear(Integer releaseYear);

    boolean existsByTitle(String title);
}
