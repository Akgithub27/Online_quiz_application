import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizService } from '../../services/apiService';
import { useQuizStore } from '../../context/quizStore';

export const AdminDashboard = () => {
  const [quizzes, setQuizzes] = React.useState([]);
  const [isLoading, setIsLoading] = React.useState(true);
  const navigate = useNavigate();

  React.useEffect(() => {
    fetchQuizzes();
  }, []);

  const fetchQuizzes = async () => {
    try {
      const response = await quizService.getAdminQuizzes();
      setQuizzes(response.data);
    } catch (error) {
      toast.error('Failed to load quizzes');
    } finally {
      setIsLoading(false);
    }
  };

  const handleDeleteQuiz = async (quizId) => {
    if (window.confirm('Are you sure you want to delete this quiz?')) {
      try {
        await quizService.deleteQuiz(quizId);
        toast.success('Quiz deleted successfully');
        fetchQuizzes();
      } catch (error) {
        toast.error('Failed to delete quiz');
      }
    }
  };

  return (
    <div className="space-y-8">
      <div className="flex justify-between items-center">
        <h1 className="text-4xl font-bold gradient-text">Admin Dashboard</h1>
        <button
          onClick={() => navigate('/admin/create-quiz')}
          className="btn-primary"
        >
          + Create Quiz
        </button>
      </div>

      {isLoading ? (
        <div className="flex justify-center py-12">
          <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-purple-600"></div>
        </div>
      ) : quizzes.length === 0 ? (
        <div className="card text-center py-12">
          <p className="text-xl text-gray-600 dark:text-gray-400">No quizzes created yet.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
          {quizzes.map((quiz, index) => (
            <motion.div
              key={quiz.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: index * 0.1 }}
              className="card hover:shadow-2xl"
            >
              <div className="space-y-4">
                <h3 className="text-2xl font-bold text-gray-800 dark:text-white">{quiz.title}</h3>
                <p className="text-gray-600 dark:text-gray-400 line-clamp-2">{quiz.description}</p>
                
                <div className="flex justify-between items-center text-sm text-gray-500 dark:text-gray-400">
                  <span>⏱️ {quiz.timeLimit} min</span>
                  <span>❓ {quiz.questionCount} questions</span>
                  <span className={`px-3 py-1 rounded-full ${quiz.isPublished ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'}`}>
                    {quiz.isPublished ? 'Published' : 'Draft'}
                  </span>
                </div>

                <div className="flex gap-2 pt-4">
                  <button
                    onClick={() => navigate(`/admin/quiz/${quiz.id}/results`)}
                    className="flex-1 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
                  >
                    View Results
                  </button>
                  <button
                    onClick={() => navigate(`/admin/quiz/${quiz.id}/edit`)}
                    className="flex-1 px-4 py-2 bg-purple-600 text-white rounded-lg hover:bg-purple-700"
                  >
                    Edit
                  </button>
                  <button
                    onClick={() => handleDeleteQuiz(quiz.id)}
                    className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700"
                  >
                    Delete
                  </button>
                </div>
              </div>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
};
