package com.online_quiz.repository;

import com.online_quiz.entity.Question;
import com.online_quiz.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    List<Question> findByQuiz(Quiz quiz);
    List<Question> findByQuizOrderByQuestionOrderAsc(Quiz quiz);
}
