import React from 'react';
import { useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import toast from 'react-hot-toast';
import { authService } from '../../services/apiService';
import { useAuthStore } from '../../context/authStore';

export const LoginPage = () => {
  const [email, setEmail] = React.useState('');
  const [password, setPassword] = React.useState('');
  const [isLoading, setIsLoading] = React.useState(false);
  const navigate = useNavigate();
  const { setUser, setToken } = useAuthStore();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setIsLoading(true);

    try {
      const response = await authService.login(email, password);
      const { token, id, name, role } = response.data;

      setToken(token);
      setUser({ id, email, name, role });

      toast.success('Login successful!');
      navigate(role === 'ADMIN' ? '/admin/dashboard' : '/participant/quizzes');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Login failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-600 to-blue-600 flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md"
      >
        <div className="bg-white dark:bg-gray-800 rounded-2xl shadow-2xl p-8">
          <h1 className="text-3xl font-bold text-center gradient-text mb-8">QuizApp</h1>

          <form onSubmit={handleSubmit} className="space-y-6">
            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Email
              </label>
              <input
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="you@example.com"
                className="input-field"
                required
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                Password
              </label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className="input-field"
                required
              />
            </div>

            <motion.button
              whileHover={{ scale: 1.02 }}
              whileTap={{ scale: 0.98 }}
              type="submit"
              disabled={isLoading}
              className="w-full btn-primary disabled:opacity-50"
            >
              {isLoading ? 'Logging in...' : 'Login'}
            </motion.button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-gray-600 dark:text-gray-400">
              Don't have an account?{' '}
              <a href="/register" className="text-purple-600 font-semibold hover:underline">
                Register here
              </a>
            </p>
          </div>

          <div className="mt-6 pt-6 border-t dark:border-gray-700">
            <p className="text-center text-sm text-gray-500 dark:text-gray-400">
              Demo credentials available in the documentation
            </p>
          </div>
        </div>
      </motion.div>
    </div>
  );
};
