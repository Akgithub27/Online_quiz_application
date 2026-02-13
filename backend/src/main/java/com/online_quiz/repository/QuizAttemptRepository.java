package com.online_quiz.repository;

import com.online_quiz.entity.QuizAttempt;
import com.online_quiz.entity.Quiz;
import com.online_quiz.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {
    List<QuizAttempt> findByUserOrderBySubmittedAtDesc(User user);
    List<QuizAttempt> findByQuizOrderBySubmittedAtDesc(Quiz quiz);
    List<QuizAttempt> findByUserAndQuiz(User user, Quiz quiz);
}
