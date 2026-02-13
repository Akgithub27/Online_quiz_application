import { create } from 'zustand';

export const useQuizStore = create((set) => ({
  quizzes: [],
  currentQuiz: null,
  quizAttempts: [],
  isLoading: false,
  error: null,

  setQuizzes: (quizzes) => set({ quizzes }),

  setCurrentQuiz: (quiz) => set({ currentQuiz: quiz }),

  setQuizAttempts: (attempts) => set({ quizAttempts: attempts }),

  setLoading: (isLoading) => set({ isLoading }),

  setError: (error) => set({ error }),

  clearError: () => set({ error: null }),
}));
