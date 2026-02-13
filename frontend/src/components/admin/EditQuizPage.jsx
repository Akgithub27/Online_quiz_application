import React from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { quizService, questionService } from '../../services/apiService';

export const EditQuizPage = () => {
  const { quizId } = useParams();
  const navigate = useNavigate();
  const [quiz, setQuiz] = React.useState(null);
  const [questions, setQuestions] = React.useState([]);
  const [isLoading, setIsLoading] = React.useState(true);
  const [newQuestion, setNewQuestion] = React.useState({
    questionText: '',
    options: ['', '', '', ''],
    correctAnswerIndex: 0,
  });
  const [showQuestionForm, setShowQuestionForm] = React.useState(false);
  const [formData, setFormData] = React.useState({
    title: '',
    description: '',
    timeLimit: 30,
    isPublished: false,
  });

  React.useEffect(() => {
    fetchQuizData();
  }, [quizId]);

  const fetchQuizData = async () => {
    try {
      setIsLoading(true);
      const quizResponse = await quizService.getAdminQuizzes();
      const currentQuiz = quizResponse.data.find(q => q.id === parseInt(quizId));
      
      if (!currentQuiz) {
        toast.error('Quiz not found');
        navigate('/admin/dashboard');
        return;
      }

      setQuiz(currentQuiz);
      setFormData({
        title: currentQuiz.title,
        description: currentQuiz.description || '',
        timeLimit: currentQuiz.timeLimit,
        isPublished: currentQuiz.isPublished,
      });

      const questionsResponse = await questionService.getQuestionsByQuiz(quizId);
      setQuestions(questionsResponse.data);
    } catch (error) {
      toast.error('Failed to load quiz details');
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: type === 'checkbox' ? checked : (name === 'timeLimit' ? parseInt(value) || 0 : value),
    }));
  };

  const handleOptionChange = (index, value) => {
    const newOptions = [...newQuestion.options];
    newOptions[index] = value;
    setNewQuestion(prev => ({
      ...prev,
      options: newOptions,
    }));
  };

  const handleAddQuestion = async (e) => {
    e.preventDefault();

    if (!newQuestion.questionText.trim()) {
      toast.error('Question text is required');
      return;
    }

    if (newQuestion.options.some(opt => !opt.trim())) {
      toast.error('All options must be filled');
      return;
    }

    try {
      const response = await questionService.createQuestion({
        quizId: parseInt(quizId),
        questionText: newQuestion.questionText,
        options: newQuestion.options,
        correctAnswerIndex: newQuestion.correctAnswerIndex,
        questionOrder: questions.length,
      });

      toast.success('Question added successfully');
      setQuestions([...questions, response.data]);
      setNewQuestion({
        questionText: '',
        options: ['', '', '', ''],
        correctAnswerIndex: 0,
      });
      setShowQuestionForm(false);
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to add question');
      console.error(error);
    }
  };

  const handleDeleteQuestion = async (questionId) => {
    if (!window.confirm('Are you sure you want to delete this question?')) return;

    try {
      await questionService.deleteQuestion(questionId);
      toast.success('Question deleted');
      setQuestions(questions.filter(q => q.id !== questionId));
    } catch (error) {
      toast.error('Failed to delete question');
      console.error(error);
    }
  };

  const handleUpdateQuiz = async (e) => {
    e.preventDefault();

    if (!formData.title.trim()) {
      toast.error('Quiz title is required');
      return;
    }

    if (!formData.timeLimit || formData.timeLimit < 1) {
      toast.error('Time limit must be at least 1 minute');
      return;
    }

    try {
      await quizService.updateQuiz(quizId, formData);
      toast.success('Quiz updated successfully');
      navigate('/admin/dashboard');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Failed to update quiz');
      console.error(error);
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
      <h1 className="text-3xl font-bold text-white mb-8">Edit Quiz</h1>

      {/* Quiz Details Form */}
      <motion.form
        onSubmit={handleUpdateQuiz}
        className="bg-gray-800 rounded-lg p-6 mb-8 border border-gray-700"
      >
        <h2 className="text-2xl font-bold text-white mb-4">Quiz Details</h2>

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-300 mb-2">
            Quiz Title *
          </label>
          <input
            type="text"
            name="title"
            value={formData.title}
            onChange={handleChange}
            placeholder="Enter quiz title"
            className="w-full px-4 py-2 bg-gray-700 text-white rounded-lg border border-gray-600 focus:border-blue-500 focus:outline-none"
            required
          />
        </div>

        <div className="mb-4">
          <label className="block text-sm font-medium text-gray-300 mb-2">
            Description
          </label>
          <textarea
            name="description"
            value={formData.description}
            onChange={handleChange}
            placeholder="Enter quiz description"
            rows="4"
            className="w-full px-4 py-2 bg-gray-700 text-white rounded-lg border border-gray-600 focus:border-blue-500 focus:outline-none"
          />
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4 mb-4">
          <div>
            <label className="block text-sm font-medium text-gray-300 mb-2">
              Time Limit (minutes) *
            </label>
            <input
              type="number"
              name="timeLimit"
              value={formData.timeLimit}
              onChange={handleChange}
              min="1"
              className="w-full px-4 py-2 bg-gray-700 text-white rounded-lg border border-gray-600 focus:border-blue-500 focus:outline-none"
              required
            />
          </div>
          <div className="flex items-center">
            <input
              type="checkbox"
              id="isPublished"
              name="isPublished"
              checked={formData.isPublished}
              onChange={handleChange}
              className="mr-2 w-4 h-4"
            />
            <label htmlFor="isPublished" className="text-gray-300">
              Publish this quiz
            </label>
          </div>
        </div>

        <div className="flex gap-2">
          <button
            type="submit"
            className="px-6 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
          >
            Update Quiz
          </button>
          <button
            type="button"
            onClick={() => navigate('/admin/dashboard')}
            className="px-6 py-2 bg-gray-600 text-white rounded-lg hover:bg-gray-700 transition"
          >
            Cancel
          </button>
        </div>
      </motion.form>

      {/* Questions Section */}
      <div className="bg-gray-800 rounded-lg p-6 border border-gray-700">
        <div className="flex justify-between items-center mb-6">
          <h2 className="text-2xl font-bold text-white">Questions ({questions.length})</h2>
          <button
            onClick={() => setShowQuestionForm(!showQuestionForm)}
            className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
          >
            {showQuestionForm ? 'Cancel' : 'Add Question'}
          </button>
        </div>

        {/* Add Question Form */}
        {showQuestionForm && (
          <motion.form
            initial={{ opacity: 0, height: 0 }}
            animate={{ opacity: 1, height: 'auto' }}
            onSubmit={handleAddQuestion}
            className="bg-gray-700 rounded-lg p-4 mb-6 border border-gray-600"
          >
            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Question Text *
              </label>
              <textarea
                value={newQuestion.questionText}
                onChange={(e) => setNewQuestion(prev => ({
                  ...prev,
                  questionText: e.target.value,
                }))}
                placeholder="Enter question"
                rows="3"
                className="w-full px-4 py-2 bg-gray-600 text-white rounded-lg border border-gray-500 focus:border-blue-500 focus:outline-none"
                required
              />
            </div>

            <div className="mb-4">
              <label className="block text-sm font-medium text-gray-300 mb-2">
                Options *
              </label>
              {newQuestion.options.map((option, index) => (
                <div key={index} className="mb-3 flex items-center gap-2">
                  <input
                    type="radio"
                    name="correctAnswer"
                    value={index}
                    checked={newQuestion.correctAnswerIndex === index}
                    onChange={() => setNewQuestion(prev => ({
                      ...prev,
                      correctAnswerIndex: index,
                    }))}
                    className="w-4 h-4"
                  />
                  <span className="text-gray-400">Option {String.fromCharCode(65 + index)}:</span>
                  <input
                    type="text"
                    value={option}
                    onChange={(e) => handleOptionChange(index, e.target.value)}
                    placeholder={`Option ${String.fromCharCode(65 + index)}`}
                    className="flex-1 px-4 py-2 bg-gray-600 text-white rounded-lg border border-gray-500 focus:border-blue-500 focus:outline-none"
                    required
                  />
                </div>
              ))}
            </div>

            <button
              type="submit"
              className="w-full px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
            >
              Add Question
            </button>
          </motion.form>
        )}

        {/* Questions List */}
        <div className="space-y-4">
          {questions.length === 0 ? (
            <p className="text-gray-400 text-center py-8">No questions yet. Add one to get started!</p>
          ) : (
            questions.map((question, index) => (
              <motion.div
                key={question.id}
                initial={{ opacity: 0, x: -20 }}
                animate={{ opacity: 1, x: 0 }}
                className="bg-gray-700 rounded-lg p-4 border-l-4 border-blue-500"
              >
                <div className="flex justify-between items-start mb-3">
                  <h3 className="text-lg font-semibold text-white">Q{index + 1}: {question.questionText}</h3>
                  <button
                    onClick={() => handleDeleteQuestion(question.id)}
                    className="px-3 py-1 bg-red-600 text-white rounded hover:bg-red-700 text-sm transition"
                  >
                    Delete
                  </button>
                </div>

                {question.options && (
                  <div className="ml-4 space-y-2">
                    {question.options.map((option, optIndex) => (
                      <div
                        key={optIndex}
                        className={`p-2 rounded ${optIndex === question.correctAnswerIndex ? 'bg-green-600 text-white' : 'bg-gray-600 text-gray-200'}`}
                      >
                        <span className="font-semibold">{String.fromCharCode(65 + optIndex)}:</span> {option}
                        {optIndex === question.correctAnswerIndex && ' ✓ (Correct)'}
                      </div>
                    ))}
                  </div>
                )}
              </motion.div>
            ))
          )}
        </div>
      </div>
    </motion.div>
  );
};
