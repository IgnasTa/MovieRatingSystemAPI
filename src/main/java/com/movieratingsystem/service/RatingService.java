package com.movieratingsystem.service;

import com.movieratingsystem.dto.RatingDto;
import com.movieratingsystem.model.Movie;
import com.movieratingsystem.model.Rating;
import com.movieratingsystem.repository.MovieRepository;
import com.movieratingsystem.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RatingService {

    private final RatingRepository ratingRepository;
    private final MovieRepository movieRepository;

    public RatingDto addRating(RatingDto ratingDto) {
        Movie movie = movieRepository.findById(ratingDto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + ratingDto.getMovieId()));

        if (ratingRepository.existsByMovieIdAndUserName(ratingDto.getMovieId(), ratingDto.getUserName())) {
            throw new IllegalArgumentException("User '" + ratingDto.getUserName() + "' has already rated this movie");
        }

        Rating rating = new Rating();
        rating.setUserName(ratingDto.getUserName());
        rating.setScore(ratingDto.getScore());
        rating.setMovie(movie);

        movie.addRating(rating);
        movieRepository.save(movie);

        Rating savedRating = ratingRepository.save(rating);
        return convertToDto(savedRating);
    }

    @Transactional(readOnly = true)
    public List<RatingDto> getRatingsByMovieId(Long movieId) {
        return ratingRepository.findByMovieId(movieId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RatingDto> getRatingsByUserName(String userName) {
        return ratingRepository.findByUserName(userName).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RatingDto getRatingById(Long id) {
        return ratingRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));
    }

    public RatingDto updateRating(Long id, RatingDto ratingDto) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

        rating.setScore(ratingDto.getScore());

        Rating updatedRating = ratingRepository.save(rating);

        // Recalculate movie average rating
        Movie movie = rating.getMovie();
        movie.calculateAverageRating();
        movieRepository.save(movie);

        return convertToDto(updatedRating);
    }

    public void deleteRating(Long id) {
        Rating rating = ratingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rating not found with id: " + id));

        Movie movie = rating.getMovie();
        ratingRepository.delete(rating);

        // Recalculate movie average rating
        movie.calculateAverageRating();
        movieRepository.save(movie);
    }

    private RatingDto convertToDto(Rating rating) {
        RatingDto dto = new RatingDto();
        dto.setId(rating.getId());
        dto.setUserName(rating.getUserName());
        dto.setScore(rating.getScore());
        dto.setMovieId(rating.getMovie().getId());
        return dto;
    }
}
