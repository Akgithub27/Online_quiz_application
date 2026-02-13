import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizAttemptService } from '../../services/apiService';

export const ResultsPage = () => {
  const { attemptId } = useParams();
  const [attempt, setAttempt] = React.useState(null);
  const [isLoading, setIsLoading] = React.useState(true);
  const navigate = useNavigate();

  React.useEffect(() => {
    fetchAttemptDetails();
  }, [attemptId]);

  const fetchAttemptDetails = async () => {
    try {
      const response = await quizAttemptService.getAttemptDetails(attemptId);
      setAttempt(response.data);
    } catch (error) {
      toast.error('Failed to load results');
      navigate('/participant/quizzes');
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex justify-center py-12">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
      </div>
    );
  }

  if (!attempt) return null;

  const isPassed = attempt.percentage >= 60;

  return (
    <motion.div
      initial={{ opacity: 0, scale: 0.9 }}
      animate={{ opacity: 1, scale: 1 }}
      className="max-w-2xl mx-auto"
    >
      <div className="card text-center space-y-8">
        {/* Result Icon */}
        <motion.div
          animate={{ scale: [1, 1.1, 1] }}
          transition={{ duration: 0.5 }}
          className="text-6xl"
        >
          {isPassed ? '🎉' : '💪'}
        </motion.div>

        {/* Heading */}
        <div>
          <h1 className={`text-4xl font-bold mb-2 ${isPassed ? 'text-green-600' : 'text-orange-600'}`}>
            {isPassed ? 'Congratulations!' : 'Good Effort!'}
          </h1>
          <p className="text-xl text-gray-600 dark:text-gray-400">
            {attempt.quizTitle}
          </p>
        </div>

        {/* Score Details */}
        <div className="bg-gradient-primary/10 rounded-xl p-8 space-y-4">
          <div className="grid grid-cols-3 gap-4">
            <div>
              <p className="text-gray-600 dark:text-gray-400">Score</p>
              <p className="text-3xl font-bold gradient-text">
                {attempt.score}/{attempt.totalQuestions}
              </p>
            </div>
            <div>
              <p className="text-gray-600 dark:text-gray-400">Percentage</p>
              <p className="text-3xl font-bold gradient-text">
                {attempt.percentage.toFixed(1)}%
              </p>
            </div>
            <div>
              <p className="text-gray-600 dark:text-gray-400">Time Spent</p>
              <p className="text-3xl font-bold gradient-text">
                {Math.floor(attempt.timeSpent / 60)}m
              </p>
            </div>
          </div>

          {/* Performance Bar */}
          <div className="mt-6">
            <div className="w-full bg-gray-300 dark:bg-gray-700 rounded-full h-4">
              <motion.div
                className={`h-4 rounded-full ${isPassed ? 'bg-green-600' : 'bg-orange-600'}`}
                initial={{ width: 0 }}
                animate={{ width: `${attempt.percentage}%` }}
                transition={{ duration: 1 }}
              />
            </div>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="flex gap-4">
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate('/participant/quizzes')}
            className="flex-1 btn-primary"
          >
            Take Another Quiz
          </motion.button>
          <motion.button
            whileHover={{ scale: 1.05 }}
            whileTap={{ scale: 0.95 }}
            onClick={() => navigate('/participant/history')}
            className="flex-1 btn-secondary"
          >
            View History
          </motion.button>
        </div>
      </div>
    </motion.div>
  );
};
