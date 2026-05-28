package com.movieratingsystem.integration;

import com.movieratingsystem.dto.MovieDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieratingsystem.repository.MovieRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class MovieControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @BeforeEach
    void setUp() {
        movieRepository.deleteAll();
    }

    @Test
    void addMovie_Success() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Inception");
        movieDto.setDescription("A mind-bending thriller");
        movieDto.setReleaseYear(2010);
        movieDto.setGenre("Sci-Fi");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Inception"))
                .andExpect(jsonPath("$.genre").value("Sci-Fi"))
                .andExpect(jsonPath("$.releaseYear").value(2010));
    }

    @Test
    void addMovie_MissingTitle_ReturnsBadRequest() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setDescription("A movie without title");

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllMovies_Success() throws Exception {
        MovieDto movie1 = new MovieDto();
        movie1.setTitle("Movie 1");
        movie1.setGenre("Action");

        MovieDto movie2 = new MovieDto();
        movie2.setTitle("Movie 2");
        movie2.setGenre("Drama");

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie1)));

        mockMvc.perform(post("/api/movies")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(movie2)));

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title").value("Movie 1"))
                .andExpect(jsonPath("$[1].title").value("Movie 2"));
    }

    @Test
    void getMovieById_Success() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("The Matrix");
        movieDto.setGenre("Sci-Fi");

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        MovieDto createdMovie = objectMapper.readValue(response, MovieDto.class);

        mockMvc.perform(get("/api/movies/" + createdMovie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Matrix"));
    }

    @Test
    void getMovieById_NotFound_ReturnsNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateMovie_Success() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Old Title");
        movieDto.setGenre("Old Genre");

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andReturn().getResponse().getContentAsString();

        MovieDto createdMovie = objectMapper.readValue(response, MovieDto.class);

        MovieDto updateDto = new MovieDto();
        updateDto.setTitle("New Title");
        updateDto.setGenre("New Genre");
        updateDto.setReleaseYear(2024);

        mockMvc.perform(put("/api/movies/" + createdMovie.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.genre").value("New Genre"));
    }

    @Test
    void deleteMovie_Success() throws Exception {
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("To Delete");
        movieDto.setGenre("Action");

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andReturn().getResponse().getContentAsString();

        MovieDto createdMovie = objectMapper.readValue(response, MovieDto.class);

        mockMvc.perform(delete("/api/movies/" + createdMovie.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/movies/" + createdMovie.getId()))
                .andExpect(status().isNotFound());
    }
}
