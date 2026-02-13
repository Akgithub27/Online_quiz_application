import { create } from 'zustand';

export const useAuthStore = create((set) => ({
  user: localStorage.getItem('user') ? JSON.parse(localStorage.getItem('user')) : null,
  token: localStorage.getItem('token') || null,
  isLoading: false,
  error: null,

  setUser: (user) => {
    set({ user });
    localStorage.setItem('user', JSON.stringify(user));
  },

  setToken: (token) => {
    set({ token });
    localStorage.setItem('token', token);
  },

  setLoading: (isLoading) => set({ isLoading }),

  setError: (error) => set({ error }),

  logout: () => {
    set({ user: null, token: null });
    localStorage.removeItem('user');
    localStorage.removeItem('token');
  },

  isLoggedIn: () => {
    const state = useAuthStore.getState();
    return !!state.token && !!state.user;
  },

  isAdmin: () => {
    const state = useAuthStore.getState();
    return state.user?.role === 'ADMIN';
  },
}));
