import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizService } from '../../services/apiService';

export const AdminResultsPage = () => {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const [quiz, setQuiz] = React.useState(null);
  const [results, setResults] = React.useState([]);
  const [isLoading, setIsLoading] = React.useState(true);

  React.useEffect(() => {
    fetchResultsData();
  }, [quizId]);

  const fetchResultsData = async () => {
    try {
      setIsLoading(true);
      
      // Get quiz details
      const quizzesResponse = await quizService.getAdminQuizzes();
      const currentQuiz = quizzesResponse.data.find(q => q.id === parseInt(quizId));
      
      if (!currentQuiz) {
        toast.error('Quiz not found');
        navigate('/admin/dashboard');
        return;
      }

      setQuiz(currentQuiz);

      // Get results
      try {
        const resultsResponse = await quizService.getQuizResults(quizId);
        setResults(resultsResponse.data || []);
      } catch (error) {
        // Results endpoint might not exist, set empty results
        console.log('Results not available yet');
        setResults([]);
      }
    } catch (error) {
      toast.error('Failed to load results');
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  if (isLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-xl text-gray-600">Loading...</div>
      </div>
    );
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-4xl mx-auto px-4 py-8"
    >
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold text-white">Quiz Results</h1>
        <button
          onClick={() => navigate('/admin/dashboard')}
          className="px-4 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition"
        >
          Back to Dashboard
        </button>
      </div>

      {/* Quiz Info Card */}
      {quiz && (
        <motion.div
          initial={{ opacity: 0, y: 10 }}
          animate={{ opacity: 1, y: 0 }}
          className="bg-gradient-to-r from-blue-600 to-blue-800 rounded-lg p-6 mb-8 text-white"
        >
          <h2 className="text-2xl font-bold mb-2">{quiz.title}</h2>
          <p className="text-blue-100 mb-2">{quiz.description}</p>
          <div className="text-sm text-blue-200">
            Time Limit: {quiz.timeLimit} minutes | Created by: {quiz.createdByName}
          </div>
        </motion.div>
      )}

      {/* Results Table */}
      <motion.div
        initial={{ opacity: 0 }}
        animate={{ opacity: 1 }}
        className="bg-gray-800 rounded-lg p-6 border border-gray-700 overflow-x-auto"
      >
        <h2 className="text-2xl font-bold text-white mb-6">Attempts ({results.length || 0})</h2>

        {results.length === 0 ? (
          <div className="text-center py-12 text-gray-400">
            <p className="text-lg">No attempts yet for this quiz</p>
            <p className="text-sm text-gray-500 mt-2">Participants haven't taken this quiz yet</p>
          </div>
        ) : (
          <table className="w-full text-left">
            <thead className="bg-gray-700 border-b border-gray-600">
              <tr>
                <th className="px-4 py-3 font-semibold text-gray-300">Participant</th>
                <th className="px-4 py-3 font-semibold text-gray-300">Email</th>
                <th className="px-4 py-3 font-semibold text-gray-300 text-center">Score</th>
                <th className="px-4 py-3 font-semibold text-gray-300">Status</th>
                <th className="px-4 py-3 font-semibold text-gray-300">Submitted At</th>
              </tr>
            </thead>
            <tbody>
              {results.map((result, index) => (
                <motion.tr
                  key={result.id}
                  initial={{ opacity: 0, x: -20 }}
                  animate={{ opacity: 1, x: 0 }}
                  transition={{ delay: index * 0.05 }}
                  className="border-b border-gray-700 hover:bg-gray-700 transition"
                >
                  <td className="px-4 py-3 text-white">{result.participantName || 'Unknown'}</td>
                  <td className="px-4 py-3 text-gray-300">{result.participantEmail || 'N/A'}</td>
                  <td className="px-4 py-3 text-center">
                    <span className="bg-green-600 text-white px-3 py-1 rounded-full font-semibold">
                      {result.score || 0}
                    </span>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`px-3 py-1 rounded-full text-sm font-semibold ${
                      result.status === 'COMPLETED' 
                        ? 'bg-green-600 text-white'
                        : result.status === 'IN_PROGRESS'
                        ? 'bg-yellow-600 text-white'
                        : 'bg-gray-600 text-gray-200'
                    }`}>
                      {result.status || 'COMPLETED'}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-300">
                    {result.submittedAt 
                      ? new Date(result.submittedAt).toLocaleDateString('en-US', {
                          year: 'numeric',
                          month: 'short',
                          day: 'numeric',
                          hour: '2-digit',
                          minute: '2-digit'
                        })
                      : 'N/A'
                    }
                  </td>
                </motion.tr>
              ))}
            </tbody>
          </table>
        )}
      </motion.div>

      {/* Statistics */}
      {results.length > 0 && (
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          className="grid grid-cols-1 md:grid-cols-4 gap-4 mt-8"
        >
          <div className="bg-blue-600 rounded-lg p-6 text-white">
            <div className="text-3xl font-bold">{results.length}</div>
            <div className="text-blue-100 text-sm mt-1">Total Attempts</div>
          </div>
          <div className="bg-green-600 rounded-lg p-6 text-white">
            <div className="text-3xl font-bold">
              {(results.reduce((sum, r) => sum + (r.score || 0), 0) / results.length).toFixed(1)}
            </div>
            <div className="text-green-100 text-sm mt-1">Average Score</div>
          </div>
          <div className="bg-purple-600 rounded-lg p-6 text-white">
            <div className="text-3xl font-bold">
              {Math.max(...results.map(r => r.score || 0))}
            </div>
            <div className="text-purple-100 text-sm mt-1">Highest Score</div>
          </div>
          <div className="bg-orange-600 rounded-lg p-6 text-white">
            <div className="text-3xl font-bold">
              {Math.min(...results.map(r => r.score || 0))}
            </div>
            <div className="text-orange-100 text-sm mt-1">Lowest Score</div>
          </div>
        </motion.div>
      )}
    </motion.div>
  );
};
