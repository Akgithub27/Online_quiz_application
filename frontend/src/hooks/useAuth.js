import React from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../context/authStore';

export const useAuth = () => {
  const authStore = useAuthStore();
  const navigate = useNavigate();

  const logout = () => {
    authStore.logout();
    navigate('/login');
  };

  return {
    ...authStore,
    logout,
  };
};
