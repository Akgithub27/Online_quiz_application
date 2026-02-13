import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizService } from '../../services/apiService';

export const CreateQuizPage = () => {
  const [formData, setFormData] = React.useState({
    title: '',
    description: '',
    timeLimit: 30,
    isPublished: false,
  });
  const [isLoading, setIsLoading] = React.useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (name === 'timeLimit' ? parseInt(value) || 0 : value),
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    if (!formData.title.trim()) {
      toast.error('Quiz title is required');
      return;
    }
    
    if (!formData.timeLimit || formData.timeLimit < 1) {
      toast.error('Time limit must be at least 1 minute');
      return;
    }

    setIsLoading(true);

    try {
      const response = await quizService.createQuiz(formData);
      toast.success('Quiz created successfully!');
      navigate('/admin/dashboard');
    } catch (error) {
      console.error('Quiz creation error:', error);
      toast.error(error.response?.data?.message || 'Failed to create quiz');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      className="max-w-2xl mx-auto"
    >
      <h1 className="text-4xl font-bold gradient-text mb-8">Create New Quiz</h1>

      <div className="card">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Quiz Title *
            </label>
            <input
              type="text"
              name="title"
              value={formData.title}
              onChange={handleChange}
              placeholder="Enter quiz title"
              className="input-field"
              required
            />
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Description
            </label>
            <textarea
              name="description"
              value={formData.description}
              onChange={handleChange}
              placeholder="Enter quiz description"
              rows="4"
              className="input-field"
            ></textarea>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
              Time Limit (minutes) *
            </label>
            <input
              type="number"
              name="timeLimit"
              value={formData.timeLimit}
              onChange={handleChange}
              min="1"
              className="input-field"
              required
            />
          </div>

          <div className="flex items-center">
            <input
              type="checkbox"
              name="isPublished"
              id="isPublished"
              checked={formData.isPublished}
              onChange={handleChange}
              className="w-4 h-4"
            />
            <label htmlFor="isPublished" className="ml-3 text-sm font-medium text-gray-700 dark:text-gray-300">
              Publish this quiz immediately
            </label>
          </div>

          <motion.button
            whileHover={{ scale: 1.02 }}
            whileTap={{ scale: 0.98 }}
            type="submit"
            disabled={isLoading}
            className="w-full btn-primary disabled:opacity-50"
          >
            {isLoading ? 'Creating...' : 'Create Quiz'}
          </motion.button>
        </form>
      </div>
    </motion.div>
  );
};
