package com.movieratingsystem.controller;

import com.movieratingsystem.dto.CommentDto;
import com.movieratingsystem.service.CommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody CommentDto commentDto) {
        CommentDto createdComment = commentService.addComment(commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<List<CommentDto>> getCommentsByMovieId(@PathVariable Long movieId) {
        List<CommentDto> comments = commentService.getCommentsByMovieId(movieId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/user/{userName}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserName(@PathVariable String userName) {
        List<CommentDto> comments = commentService.getCommentsByUserName(userName);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long id) {
        CommentDto comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable Long id,
                                                     @Valid @RequestBody CommentDto commentDto) {
        CommentDto updatedComment = commentService.updateComment(id, commentDto);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}
