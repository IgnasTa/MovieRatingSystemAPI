package com.movieratingsystem.repository;

import com.movieratingsystem.model.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByMovieId(Long movieId);

    List<Rating> findByUserName(String userName);

    boolean existsByMovieIdAndUserName(Long movieId, String userName);
}
