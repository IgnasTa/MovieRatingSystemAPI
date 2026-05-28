package com.movieratingsystem.repository;

import com.movieratingsystem.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByMovieId(Long movieId);

    List<Comment> findByUserName(String userName);
}
