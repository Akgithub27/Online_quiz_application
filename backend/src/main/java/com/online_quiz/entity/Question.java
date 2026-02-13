package com.online_quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(columnDefinition = "JSON", nullable = false)
    private String options; // JSON array: ["option1", "option2", "option3", "option4"]

    @Column(nullable = false)
    private Integer correctAnswerIndex; // 0, 1, 2, or 3

    @Column(name = "question_order")
    private Integer questionOrder;

    @Column(nullable = false)
    private Boolean isActive = true;
}
