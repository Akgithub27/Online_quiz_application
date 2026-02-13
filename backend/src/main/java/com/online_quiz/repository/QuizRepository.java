package com.online_quiz.repository;

import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCreatedBy(User user);
    List<Quiz> findByIsPublishedTrue();
}
