import React from 'react';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizAttemptService } from '../../services/apiService';

export const HistoryPage = () => {
  const [attempts, setAttempts] = React.useState([]);
  const [isLoading, setIsLoading] = React.useState(true);

  React.useEffect(() => {
    fetchHistory();
  }, []);

  const fetchHistory = async () => {
    try {
      const response = await quizAttemptService.getUserHistory();
      setAttempts(response.data);
    } catch (error) {
      toast.error('Failed to load history');
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

  return (
    <div className="space-y-8">
      <h1 className="text-4xl font-bold gradient-text">Quiz History</h1>

      {attempts.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-xl text-gray-600 dark:text-gray-400">
            You haven't taken any quizzes yet.
          </p>
        </div>
      ) : (
        <div className="space-y-4">
          {attempts.map((attempt, index) => (
            <motion.div
              key={attempt.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: index * 0.1 }}
              className="card"
            >
              <div className="flex justify-between items-center">
                <div>
                  <h3 className="text-lg font-bold text-gray-800 dark:text-white">
                    {attempt.quizTitle}
                  </h3>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    Submitted: {new Date(attempt.submittedAt).toLocaleDateString()}
                  </p>
                </div>
                <div className="text-right">
                  <p className={`text-2xl font-bold ${attempt.percentage >= 60 ? 'text-green-600' : 'text-orange-600'}`}>
                    {attempt.percentage.toFixed(1)}%
                  </p>
                  <p className="text-sm text-gray-600 dark:text-gray-400">
                    {attempt.score}/{attempt.totalQuestions}
                  </p>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
};
