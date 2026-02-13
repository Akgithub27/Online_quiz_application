import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuthStore } from '../../context/authStore';

export const Header = () => {
  const { user, logout } = useAuthStore();
  const navigate = useNavigate();
  const [isMenuOpen, setIsMenuOpen] = React.useState(false);

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="sticky top-0 z-50 bg-white dark:bg-gray-800 shadow-md">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          {/* Logo */}
          <Link to="/" className="flex items-center space-x-2">
            <div className="gradient-text text-2xl font-bold">QuizApp</div>
          </Link>

          {/* Desktop Navigation */}
          <nav className="hidden md:flex space-x-8">
            {user?.role === 'ADMIN' ? (
              <>
                <Link to="/admin/dashboard" className="text-gray-700 dark:text-gray-300 hover:text-purple-600">
                  Dashboard
                </Link>
                <Link to="/admin/create-quiz" className="text-gray-700 dark:text-gray-300 hover:text-purple-600">
                  Create Quiz
                </Link>
                <Link to="/swagger-ui.html" target="_blank" className="text-gray-700 dark:text-gray-300 hover:text-purple-600">
                  API Docs
                </Link>
              </>
            ) : user?.role === 'PARTICIPANT' ? (
              <>
                <Link to="/participant/quizzes" className="text-gray-700 dark:text-gray-300 hover:text-purple-600">
                  Quizzes
                </Link>
                <Link to="/participant/history" className="text-gray-700 dark:text-gray-300 hover:text-purple-600">
                  History
                </Link>
              </>
            ) : null}
          </nav>

          {/* User Menu */}
          <div className="flex items-center space-x-4">
            {user ? (
              <div className="relative">
                <motion.button
                  onClick={() => setIsMenuOpen(!isMenuOpen)}
                  className="flex items-center space-x-2 px-4 py-2 rounded-lg bg-purple-100 dark:bg-purple-900"
                  whileHover={{ scale: 1.05 }}
                >
                  <span className="text-sm font-medium">{user.name}</span>
                  <svg className="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
                    <path fillRule="evenodd" d="M5.293 7.293a1 1 0 011.414 0L10 10.586l3.293-3.293a1 1 0 111.414 1.414l-4 4a1 1 0 01-1.414 0l-4-4a1 1 0 010-1.414z" clipRule="evenodd" />
                  </svg>
                </motion.button>

                {isMenuOpen && (
                  <motion.div
                    initial={{ opacity: 0, y: -10 }}
                    animate={{ opacity: 1, y: 0 }}
                    className="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-700 rounded-lg shadow-lg"
                  >
                    <div className="px-4 py-2 border-b dark:border-gray-600">
                      <p className="text-sm text-gray-600 dark:text-gray-300">{user.email}</p>
                    </div>
                    <button
                      onClick={handleLogout}
                      className="w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-gray-100 dark:hover:bg-gray-600"
                    >
                      Logout
                    </button>
                  </motion.div>
                )}
              </div>
            ) : (
              <div className="space-x-2">
                <Link to="/login" className="btn-secondary inline-block">
                  Login
                </Link>
                <Link to="/register" className="btn-primary inline-block">
                  Register
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
};
