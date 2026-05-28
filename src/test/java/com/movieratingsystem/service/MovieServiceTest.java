package com.movieratingsystem.service;

import com.movieratingsystem.dto.MovieDto;
import com.movieratingsystem.model.Movie;
import com.movieratingsystem.repository.MovieRepository;
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
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private MovieService movieService;

    private Movie testMovie;
    private MovieDto testMovieDto;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");
        testMovie.setDescription("A test movie description");
        testMovie.setReleaseYear(2024);
        testMovie.setGenre("Action");
        testMovie.setAverageRating(0.0);

        testMovieDto = new MovieDto();
        testMovieDto.setTitle("Test Movie");
        testMovieDto.setDescription("A test movie description");
        testMovieDto.setReleaseYear(2024);
        testMovieDto.setGenre("Action");
    }

    @Test
    void addMovie_Success() {
        when(movieRepository.existsByTitle("Test Movie")).thenReturn(false);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        MovieDto result = movieService.addMovie(testMovieDto);

        assertNotNull(result);
        assertEquals("Test Movie", result.getTitle());
        assertEquals("Action", result.getGenre());
        verify(movieRepository, times(1)).save(any(Movie.class));
    }

    @Test
    void addMovie_DuplicateTitle_ThrowsException() {
        when(movieRepository.existsByTitle("Test Movie")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            movieService.addMovie(testMovieDto);
        });

        verify(movieRepository, never()).save(any(Movie.class));
    }

    @Test
    void getAllMovies_Success() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findAll()).thenReturn(movies);

        List<MovieDto> result = movieService.getAllMovies();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Movie", result.get(0).getTitle());
        verify(movieRepository, times(1)).findAll();
    }

    @Test
    void getMovieById_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));

        MovieDto result = movieService.getMovieById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Movie", result.getTitle());
    }

    @Test
    void getMovieById_NotFound_ThrowsException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movieService.getMovieById(99L);
        });
    }

    @Test
    void getMovieByTitle_Success() {
        when(movieRepository.findByTitle("Test Movie")).thenReturn(Optional.of(testMovie));

        MovieDto result = movieService.getMovieByTitle("Test Movie");

        assertNotNull(result);
        assertEquals("Test Movie", result.getTitle());
    }

    @Test
    void getMovieByTitle_NotFound_ThrowsException() {
        when(movieRepository.findByTitle("Nonexistent")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movieService.getMovieByTitle("Nonexistent");
        });
    }

    @Test
    void getMoviesByGenre_Success() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByGenre("Action")).thenReturn(movies);

        List<MovieDto> result = movieService.getMoviesByGenre("Action");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Action", result.get(0).getGenre());
    }

    @Test
    void getMoviesByReleaseYear_Success() {
        List<Movie> movies = Arrays.asList(testMovie);
        when(movieRepository.findByReleaseYear(2024)).thenReturn(movies);

        List<MovieDto> result = movieService.getMoviesByReleaseYear(2024);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(2024, result.get(0).getReleaseYear());
    }

    @Test
    void updateMovie_Success() {
        MovieDto updateDto = new MovieDto();
        updateDto.setTitle("Updated Movie");
        updateDto.setDescription("Updated description");
        updateDto.setReleaseYear(2025);
        updateDto.setGenre("Drama");

        Movie updatedMovie = new Movie();
        updatedMovie.setId(1L);
        updatedMovie.setTitle("Updated Movie");
        updatedMovie.setDescription("Updated description");
        updatedMovie.setReleaseYear(2025);
        updatedMovie.setGenre("Drama");

        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(movieRepository.save(any(Movie.class))).thenReturn(updatedMovie);

        MovieDto result = movieService.updateMovie(1L, updateDto);

        assertNotNull(result);
        assertEquals("Updated Movie", result.getTitle());
        assertEquals("Drama", result.getGenre());
        assertEquals(2025, result.getReleaseYear());
    }

    @Test
    void updateMovie_NotFound_ThrowsException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            movieService.updateMovie(99L, testMovieDto);
        });
    }

    @Test
    void deleteMovie_Success() {
        when(movieRepository.existsById(1L)).thenReturn(true);
        doNothing().when(movieRepository).deleteById(1L);

        movieService.deleteMovie(1L);

        verify(movieRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteMovie_NotFound_ThrowsException() {
        when(movieRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            movieService.deleteMovie(99L);
        });

        verify(movieRepository, never()).deleteById(anyLong());
    }
}
