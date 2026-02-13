import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizService } from '../../services/apiService';

export const QuizBrowsing = () => {
  const [quizzes, setQuizzes] = React.useState([]);
  const [isLoading, setIsLoading] = React.useState(true);
  const navigate = useNavigate();

  React.useEffect(() => {
    fetchQuizzes();
  }, []);

  const fetchQuizzes = async () => {
    try {
      const response = await quizService.getPublishedQuizzes();
      setQuizzes(response.data);
    } catch (error) {
      toast.error('Failed to load quizzes');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="space-y-8">
      <h1 className="text-4xl font-bold gradient-text">Available Quizzes</h1>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        </div>
      ) : quizzes.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-xl text-gray-600 dark:text-gray-400">No quizzes available yet.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {quizzes.map((quiz, index) => (
            <motion.div
              key={quiz.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="card hover:shadow-2xl cursor-pointer group"
              onClick={() => navigate(`/participant/quiz/${quiz.id}`)}
            >
              <div className="space-y-4">
                <h3 className="text-2xl font-bold text-gray-800 dark:text-white group-hover:text-purple-600 transition-colors">
                  {quiz.title}
                </h3>
                <p className="text-gray-600 dark:text-gray-400 line-clamp-2">{quiz.description}</p>
                
                <div className="flex justify-between items-center text-sm text-gray-500 dark:text-gray-400">
                  <span>⏱️ {quiz.timeLimit} min</span>
                  <span>❓ {quiz.questionCount} questions</span>
                </div>

                <motion.button
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                  onClick={(e) => {
                    e.stopPropagation();
                    navigate(`/participant/quiz/${quiz.id}`);
                  }}
                  className="w-full btn-primary mt-4"
                >
                  Start Quiz
                </motion.button>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
};
