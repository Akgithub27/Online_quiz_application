import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../../context/authStore';

export const ProtectedRoute = ({ children, requiredRole = null }) => {
  const { user, token } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  React.useEffect(() => {
    if (!token || !user) {
      navigate('/login', { state: { from: location } });
      return;
    }

    if (requiredRole && user.role !== requiredRole) {
      navigate('/unauthorized');
    }
  }, [token, user, requiredRole, navigate, location]);

  if (!token || !user) {
    return null;
  }

  if (requiredRole && user.role !== requiredRole) {
    return null;
  }

  return children;
};
