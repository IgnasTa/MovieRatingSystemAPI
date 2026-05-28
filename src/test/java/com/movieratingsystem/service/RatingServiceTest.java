package com.movieratingsystem.service;

import com.movieratingsystem.dto.RatingDto;
import com.movieratingsystem.model.Movie;
import com.movieratingsystem.model.Rating;
import com.movieratingsystem.repository.MovieRepository;
import com.movieratingsystem.repository.RatingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private RatingService ratingService;

    private Rating testRating;
    private RatingDto testRatingDto;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setAverageRating(0.0);

        testRating = new Rating();
        testRating.setId(1L);
        testRating.setUserName("testuser");
        testRating.setScore(8);
        testRating.setMovie(testMovie);

        testRatingDto = new RatingDto();
        testRatingDto.setUserName("testuser");
        testRatingDto.setScore(8);
        testRatingDto.setMovieId(1L);
    }

    @Test
    void addRating_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(ratingRepository.existsByMovieIdAndUserName(1L, "testuser")).thenReturn(false);
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        RatingDto result = ratingService.addRating(testRatingDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals(8, result.getScore());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void addRating_MovieNotFound_ThrowsException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ratingService.addRating(testRatingDto);
        });
    }

    @Test
    void addRating_DuplicateRating_ThrowsException() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(ratingRepository.existsByMovieIdAndUserName(1L, "testuser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            ratingService.addRating(testRatingDto);
        });
    }

    @Test
    void getRatingsByMovieId_Success() {
        List<Rating> ratings = Arrays.asList(testRating);
        when(ratingRepository.findByMovieId(1L)).thenReturn(ratings);

        List<RatingDto> result = ratingService.getRatingsByMovieId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUserName());
    }

    @Test
    void getRatingsByUserName_Success() {
        List<Rating> ratings = Arrays.asList(testRating);
        when(ratingRepository.findByUserName("testuser")).thenReturn(ratings);

        List<RatingDto> result = ratingService.getRatingsByUserName("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(8, result.get(0).getScore());
    }

    @Test
    void getRatingById_Success() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));

        RatingDto result = ratingService.getRatingById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(8, result.getScore());
    }

    @Test
    void getRatingById_NotFound_ThrowsException() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ratingService.getRatingById(99L);
        });
    }

    @Test
    void updateRating_Success() {
        RatingDto updateDto = new RatingDto();
        updateDto.setScore(9);

        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(Rating.class))).thenReturn(testRating);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        RatingDto result = ratingService.updateRating(1L, updateDto);

        assertNotNull(result);
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    void updateRating_NotFound_ThrowsException() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ratingService.updateRating(99L, testRatingDto);
        });
    }

    @Test
    void deleteRating_Success() {
        when(ratingRepository.findById(1L)).thenReturn(Optional.of(testRating));
        doNothing().when(ratingRepository).delete(testRating);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        ratingService.deleteRating(1L);

        verify(ratingRepository, times(1)).delete(testRating);
    }

    @Test
    void deleteRating_NotFound_ThrowsException() {
        when(ratingRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            ratingService.deleteRating(99L);
        });
    }
}
