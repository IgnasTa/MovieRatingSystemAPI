package com.movieratingsystem.service;

import com.movieratingsystem.dto.CommentDto;
import com.movieratingsystem.model.Comment;
import com.movieratingsystem.model.Movie;
import com.movieratingsystem.repository.CommentRepository;
import com.movieratingsystem.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final MovieRepository movieRepository;

    public CommentDto addComment(CommentDto commentDto) {
        Movie movie = movieRepository.findById(commentDto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + commentDto.getMovieId()));

        Comment comment = new Comment();
        comment.setUserName(commentDto.getUserName());
        comment.setCommentText(commentDto.getCommentText());
        comment.setMovie(movie);

        movie.addComment(comment);
        movieRepository.save(movie);

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByMovieId(Long movieId) {
        return commentRepository.findByMovieId(movieId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByUserName(String userName) {
        return commentRepository.findByUserName(userName).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long id) {
        return commentRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));
    }

    public CommentDto updateComment(Long id, CommentDto commentDto) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + id));

        comment.setCommentText(commentDto.getCommentText());

        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }

    public void deleteComment(Long id) {
        if (!commentRepository.existsById(id)) {
            throw new RuntimeException("Comment not found with id: " + id);
        }
        commentRepository.deleteById(id);
    }

    private CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setUserName(comment.getUserName());
        dto.setCommentText(comment.getCommentText());
        dto.setMovieId(comment.getMovie().getId());
        return dto;
    }
}
