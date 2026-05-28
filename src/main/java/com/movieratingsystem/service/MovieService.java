package com.movieratingsystem.service;

import com.movieratingsystem.dto.MovieDto;
import com.movieratingsystem.model.Movie;
import com.movieratingsystem.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieDto addMovie(MovieDto movieDto) {
        if (movieRepository.existsByTitle(movieDto.getTitle())) {
            throw new IllegalArgumentException("Movie with title '" + movieDto.getTitle() + "' already exists");
        }

        Movie movie = new Movie();
        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setReleaseYear(movieDto.getReleaseYear());
        movie.setGenre(movieDto.getGenre());
        movie.setAverageRating(0.0);

        Movie savedMovie = movieRepository.save(movie);
        return convertToDto(savedMovie);
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getAllMovies() {
        return movieRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MovieDto getMovieById(Long id) {
        return movieRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public MovieDto getMovieByTitle(String title) {
        return movieRepository.findByTitle(title)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Movie not found with title: " + title));
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getMoviesByGenre(String genre) {
        return movieRepository.findByGenre(genre).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<MovieDto> getMoviesByReleaseYear(Integer releaseYear) {
        return movieRepository.findByReleaseYear(releaseYear).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public MovieDto updateMovie(Long id, MovieDto movieDto) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));

        movie.setTitle(movieDto.getTitle());
        movie.setDescription(movieDto.getDescription());
        movie.setReleaseYear(movieDto.getReleaseYear());
        movie.setGenre(movieDto.getGenre());

        Movie updatedMovie = movieRepository.save(movie);
        return convertToDto(updatedMovie);
    }

    public void deleteMovie(Long id) {
        if (!movieRepository.existsById(id)) {
            throw new RuntimeException("Movie not found with id: " + id);
        }
        movieRepository.deleteById(id);
    }

    private MovieDto convertToDto(Movie movie) {
        MovieDto dto = new MovieDto();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setGenre(movie.getGenre());
        dto.setAverageRating(movie.getAverageRating());
        return dto;
    }
}
