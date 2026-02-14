import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizService, quizAttemptService } from '../../services/apiService';

export const QuizTakingPage = () => {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const [quiz, setQuiz] = React.useState(null);
  const [currentQuestionIndex, setCurrentQuestionIndex] = React.useState(0);
  const [selectedAnswers, setSelectedAnswers] = React.useState({});
  const [timeLeft, setTimeLeft] = React.useState(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const [isSubmitting, setIsSubmitting] = React.useState(false);

  React.useEffect(() => {
    fetchQuiz();
  }, [quizId]);

  React.useEffect(() => {
    if (!timeLeft || timeLeft <= 0) return;

    const timer = setInterval(() => {
      setTimeLeft(prev => {
        if (prev <= 1) {
          handleSubmitQuiz();
          return 0;
        }
        return prev - 1;
      });
    }, 1000);

    return () => clearInterval(timer);
  }, [timeLeft]);

  const fetchQuiz = async () => {
    try {
      const response = await quizService.getQuizById(quizId);
      setQuiz(response.data);
      setTimeLeft(response.data.timeLimit * 60);
    } catch (error) {
      toast.error('Failed to load quiz');
      navigate('/participant/quizzes');
    } finally {
      setIsLoading(false);
    }
  };

  const handleAnswerSelect = (questionId, answerIndex) => {
    setSelectedAnswers(prev => ({
      ...prev,
      [questionId]: answerIndex,
    }));
  };

  const handleSubmitQuiz = async () => {
    setIsSubmitting(true);

    try {
      // Validate that all questions are answered
      if (Object.keys(selectedAnswers).length < quiz.questions.length) {
        toast.error('Please answer all questions before submitting');
        setIsSubmitting(false);
        return;
      }

      const response = await quizAttemptService.submitQuiz({
        quizId,
        selectedAnswers,
        timeSpent: (quiz.timeLimit * 60) - timeLeft,
      });

      toast.success('Quiz submitted successfully!');
      navigate(`/participant/results/${response.data.id}`);
    } catch (error) {
      console.error('Quiz submission error:', error);
      const errorMessage = error.response?.data?.message || error.message || 'Failed to submit quiz';
      toast.error(errorMessage);
    } finally {
      setIsSubmitting(false);
    }
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  if (!quiz || quiz.questions.length === 0) {
    return <div>Quiz not found or has no questions</div>;
  }

  const currentQuestion = quiz.questions[currentQuestionIndex];
  const progress = ((currentQuestionIndex + 1) / quiz.questions.length) * 100;

  return (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      className="max-w-4xl mx-auto"
    >
      {/* Header */}
      <div className="mb-8">
        <div className="flex justify-between items-center mb-4">
          <h1 className="text-3xl font-bold gradient-text">{quiz.title}</h1>
          <div className={`text-2xl font-bold ${timeLeft <= 60 ? 'text-red-600' : 'text-green-600'}`}>
            ⏱️ {formatTime(timeLeft)}
          </div>
        </div>

        {/* Progress Bar */}
        <div className="w-full bg-gray-300 dark:bg-gray-700 rounded-full h-2">
          <motion.div
            className="bg-gradient-primary h-2 rounded-full"
            style={{ width: `${progress}%` }}
            transition={{ duration: 0.3 }}
          />
        </div>
        <p className="text-sm text-gray-600 dark:text-gray-400 mt-2">
          Question {currentQuestionIndex + 1} of {quiz.questions.length}
        </p>
      </div>

      {/* Question */}
      <motion.div
        key={currentQuestion.id}
        initial={{ opacity: 0, x: 20 }}
        animate={{ opacity: 1, x: 0 }}
        className="card space-y-6"
      >
        <h2 className="text-2xl font-bold text-gray-800 dark:text-white">
          {currentQuestion.questionText}
        </h2>

        {/* Answer Options */}
        <div className="space-y-3">
          {currentQuestion.options.map((option, index) => (
            <motion.button
              key={index}
              whileHover={{ scale: 1.02 }}
              onClick={() => handleAnswerSelect(currentQuestion.id, index)}
              className={`w-full p-4 text-left rounded-lg border-2 transition-all ${
                selectedAnswers[currentQuestion.id] === index
                  ? 'border-purple-600 bg-purple-50 dark:bg-purple-900'
                  : 'border-gray-300 dark:border-gray-600 hover:border-purple-400'
              }`}
            >
              <span className="font-semibold">
                {String.fromCharCode(65 + index)}.
              </span>
              {' '}
              {option}
            </motion.button>
          ))}
        </div>
      </motion.div>

      {/* Navigation */}
      <div className="mt-8 flex justify-between">
        <motion.button
          whileHover={{ scale: 1.05 }}
          whileTap={{ scale: 0.95 }}
          onClick={() => setCurrentQuestionIndex(Math.max(0, currentQuestionIndex - 1))}
          disabled={currentQuestionIndex === 0}
          className="btn-secondary disabled:opacity-50"
        >
          Previous
        </motion.button>

        <div className="flex gap-2">
          {currentQuestionIndex === quiz.questions.length - 1 ? (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={handleSubmitQuiz}
              disabled={isSubmitting}
              className="btn-primary disabled:opacity-50"
            >
              {isSubmitting ? 'Submitting...' : 'Submit Quiz'}
            </motion.button>
          ) : (
            <motion.button
              whileHover={{ scale: 1.05 }}
              whileTap={{ scale: 0.95 }}
              onClick={() => setCurrentQuestionIndex(currentQuestionIndex + 1)}
              className="btn-primary"
            >
              Next
            </motion.button>
          )}
        </div>
      </div>
    </motion.div>
  );
};
