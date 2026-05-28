package com.movieratingsystem.integration;

import com.movieratingsystem.dto.CommentDto;
import com.movieratingsystem.dto.MovieDto;
import com.movieratingsystem.dto.RatingDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.movieratingsystem.repository.CommentRepository;
import com.movieratingsystem.repository.MovieRepository;
import com.movieratingsystem.repository.RatingRepository;
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
class RatingAndCommentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Long movieId;

    @BeforeEach
    void setUp() throws Exception {
        movieRepository.deleteAll();
        ratingRepository.deleteAll();
        commentRepository.deleteAll();

        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Test Movie");
        movieDto.setGenre("Action");

        String response = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andReturn().getResponse().getContentAsString();

        MovieDto createdMovie = objectMapper.readValue(response, MovieDto.class);
        movieId = createdMovie.getId();
    }

    @Test
    void addRating_Success() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setUserName("user1");
        ratingDto.setScore(8);
        ratingDto.setMovieId(movieId);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("user1"))
                .andExpect(jsonPath("$.score").value(8))
                .andExpect(jsonPath("$.movieId").value(movieId));
    }

    @Test
    void addRating_InvalidScore_ReturnsBadRequest() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setUserName("user1");
        ratingDto.setScore(15); // Invalid score (> 10)
        ratingDto.setMovieId(movieId);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void addRating_DuplicateUser_ReturnsBadRequest() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setUserName("user1");
        ratingDto.setScore(8);
        ratingDto.setMovieId(movieId);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingDto)));

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getRatingsByMovieId_Success() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setUserName("user1");
        ratingDto.setScore(8);
        ratingDto.setMovieId(movieId);

        mockMvc.perform(post("/api/ratings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ratingDto)));

        mockMvc.perform(get("/api/ratings/movie/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userName").value("user1"));
    }

    @Test
    void addComment_Success() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUserName("user1");
        commentDto.setCommentText("Great movie!");
        commentDto.setMovieId(movieId);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName").value("user1"))
                .andExpect(jsonPath("$.commentText").value("Great movie!"))
                .andExpect(jsonPath("$.movieId").value(movieId));
    }

    @Test
    void addComment_EmptyText_ReturnsBadRequest() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUserName("user1");
        commentDto.setCommentText("");
        commentDto.setMovieId(movieId);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getCommentsByMovieId_Success() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setUserName("user1");
        commentDto.setCommentText("Great movie!");
        commentDto.setMovieId(movieId);

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)));

        mockMvc.perform(get("/api/comments/movie/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].commentText").value("Great movie!"));
    }

    @Test
    void fullWorkflow_AddMovieRateAndComment() throws Exception {
        // Add a movie
        MovieDto movieDto = new MovieDto();
        movieDto.setTitle("Inception");
        movieDto.setGenre("Sci-Fi");
        movieDto.setReleaseYear(2010);

        String movieResponse = mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieDto)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        MovieDto createdMovie = objectMapper.readValue(movieResponse, MovieDto.class);
        Long newMovieId = createdMovie.getId();

        // Rate the movie
        RatingDto ratingDto = new RatingDto();
        ratingDto.setUserName("user1");
        ratingDto.setScore(9);
        ratingDto.setMovieId(newMovieId);

        mockMvc.perform(post("/api/ratings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ratingDto)))
                .andExpect(status().isCreated());

        // Comment on the movie
        CommentDto commentDto = new CommentDto();
        commentDto.setUserName("user1");
        commentDto.setCommentText("Mind-blowing!");
        commentDto.setMovieId(newMovieId);

        mockMvc.perform(post("/api/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated());

        // Verify movie has updated average rating
        mockMvc.perform(get("/api/movies/" + newMovieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageRating").value(9.0));
    }
}
