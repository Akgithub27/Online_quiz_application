import apiClient from './apiClient';

// Auth Services
export const authService = {
  register: (email, password, name, role) =>
    apiClient.post('/auth/register', { email, password, name, role }),
  
  login: (email, password) =>
    apiClient.post('/auth/login', { email, password }),
};

// Quiz Services
export const quizService = {
  getPublishedQuizzes: () =>
    apiClient.get('/quizzes'),
  
  getQuizById: (quizId) =>
    apiClient.get(`/quiz/${quizId}`),
  
  createQuiz: (data) =>
    apiClient.post('/admin/quiz', data),
  
  updateQuiz: (quizId, data) =>
    apiClient.put(`/admin/quiz/${quizId}`, data),
  
  deleteQuiz: (quizId) =>
    apiClient.delete(`/admin/quiz/${quizId}`),
  
  getAdminQuizzes: () =>
    apiClient.get('/admin/quiz'),

  getQuizResults: (quizId) =>
    apiClient.get(`/admin/quiz/${quizId}/results`),
};

// Question Services
export const questionService = {
  createQuestion: (data) =>
    apiClient.post('/admin/question', data),
  
  updateQuestion: (questionId, data) =>
    apiClient.put(`/admin/question/${questionId}`, data),
  
  deleteQuestion: (questionId) =>
    apiClient.delete(`/admin/question/${questionId}`),
  
  getQuestionsByQuiz: (quizId) =>
    apiClient.get(`/admin/quiz/${quizId}/questions`),
};

// Quiz Attempt Services
export const quizAttemptService = {
  submitQuiz: (data) =>
    apiClient.post('/quiz/submit', data),
  
  getUserHistory: () =>
    apiClient.get('/user/history'),
  
  getAttemptDetails: (attemptId) =>
    apiClient.get(`/attempt/${attemptId}`),
  
  getQuizResults: (quizId) =>
    apiClient.get(`/admin/quiz/${quizId}/results`),
};
