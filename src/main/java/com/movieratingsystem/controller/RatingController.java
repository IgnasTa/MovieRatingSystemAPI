package com.movieratingsystem.controller;

import com.movieratingsystem.dto.RatingDto;
import com.movieratingsystem.service.RatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<RatingDto> addRating(@Valid @RequestBody RatingDto ratingDto) {
        RatingDto createdRating = ratingService.addRating(ratingDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<RatingDto>> getRatingsByMovieId(@PathVariable Long movieId) {
        List<RatingDto> ratings = ratingService.getRatingsByMovieId(movieId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<RatingDto>> getRatingsByUserName(@PathVariable String userName) {
        List<RatingDto> ratings = ratingService.getRatingsByUserName(userName);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RatingDto> getRatingById(@PathVariable Long id) {
        RatingDto rating = ratingService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RatingDto> updateRating(@PathVariable Long id,
                                                   @Valid @RequestBody RatingDto ratingDto) {
        RatingDto updatedRating = ratingService.updateRating(id, ratingDto);
        return ResponseEntity.ok(updatedRating);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(@PathVariable Long id) {
        ratingService.deleteRating(id);
        return ResponseEntity.noContent().build();
    }
}
