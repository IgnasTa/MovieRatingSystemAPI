package com.movieratingsystem.service;

import com.movieratingsystem.dto.CommentDto;
import com.movieratingsystem.model.Comment;
import com.movieratingsystem.model.Movie;
import com.movieratingsystem.repository.CommentRepository;
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
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MovieRepository movieRepository;

    @InjectMocks
    private CommentService commentService;

    private Comment testComment;
    private CommentDto testCommentDto;
    private Movie testMovie;

    @BeforeEach
    void setUp() {
        testMovie = new Movie();
        testMovie.setId(1L);
        testMovie.setTitle("Test Movie");

        testComment = new Comment();
        testComment.setId(1L);
        testComment.setUserName("testuser");
        testComment.setCommentText("Great movie!");
        testComment.setMovie(testMovie);

        testCommentDto = new CommentDto();
        testCommentDto.setUserName("testuser");
        testCommentDto.setCommentText("Great movie!");
        testCommentDto.setMovieId(1L);
    }

    @Test
    void addComment_Success() {
        when(movieRepository.findById(1L)).thenReturn(Optional.of(testMovie));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);
        when(movieRepository.save(any(Movie.class))).thenReturn(testMovie);

        CommentDto result = commentService.addComment(testCommentDto);

        assertNotNull(result);
        assertEquals("testuser", result.getUserName());
        assertEquals("Great movie!", result.getCommentText());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void addComment_MovieNotFound_ThrowsException() {
        when(movieRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            commentService.addComment(testCommentDto);
        });
    }

    @Test
    void getCommentsByMovieId_Success() {
        List<Comment> comments = Arrays.asList(testComment);
        when(commentRepository.findByMovieId(1L)).thenReturn(comments);

        List<CommentDto> result = commentService.getCommentsByMovieId(1L);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Great movie!", result.get(0).getCommentText());
    }

    @Test
    void getCommentsByUserName_Success() {
        List<Comment> comments = Arrays.asList(testComment);
        when(commentRepository.findByUserName("testuser")).thenReturn(comments);

        List<CommentDto> result = commentService.getCommentsByUserName("testuser");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUserName());
    }

    @Test
    void getCommentById_Success() {
        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));

        CommentDto result = commentService.getCommentById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Great movie!", result.getCommentText());
    }

    @Test
    void getCommentById_NotFound_ThrowsException() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            commentService.getCommentById(99L);
        });
    }

    @Test
    void updateComment_Success() {
        CommentDto updateDto = new CommentDto();
        updateDto.setCommentText("Updated comment");

        when(commentRepository.findById(1L)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenReturn(testComment);

        CommentDto result = commentService.updateComment(1L, updateDto);

        assertNotNull(result);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void updateComment_NotFound_ThrowsException() {
        when(commentRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            commentService.updateComment(99L, testCommentDto);
        });
    }

    @Test
    void deleteComment_Success() {
        when(commentRepository.existsById(1L)).thenReturn(true);
        doNothing().when(commentRepository).deleteById(1L);

        commentService.deleteComment(1L);

        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteComment_NotFound_ThrowsException() {
        when(commentRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> {
            commentService.deleteComment(99L);
        });

        verify(commentRepository, never()).deleteById(anyLong());
    }
}
