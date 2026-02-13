import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';
import { useAuthStore } from '../../context/authStore';

export const LandingPage = () => {
  const { user } = useAuthStore();

  const containerVariants = {
    hidden: { opacity: 0 },
    visible: {
      opacity: 1,
      transition: {
        staggerChildren: 0.2,
        delayChildren: 0.3,
      },
    },
  };

  const itemVariants = {
    hidden: { opacity: 0, y: 20 },
    visible: {
      opacity: 1,
      y: 0,
      transition: { duration: 0.5 },
    },
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-purple-600 via-blue-600 to-pink-600">
      {/* Hero Section */}
      <section className="min-h-screen flex items-center justify-center px-4">
        <motion.div
          variants={containerVariants}
          initial="hidden"
          animate="visible"
          className="max-w-4xl mx-auto text-center"
        >
          <motion.h1
            variants={itemVariants}
            className="text-5xl md:text-7xl font-bold text-white mb-6"
          >
            Welcome to <span className="bg-gradient-to-r from-yellow-200 to-pink-200 bg-clip-text text-transparent">QuizApp</span>
          </motion.h1>

          <motion.p
            variants={itemVariants}
            className="text-xl md:text-2xl text-white/90 mb-8 max-w-2xl mx-auto"
          >
            Master your knowledge with interactive quizzes. Create, take, and track your progress with our modern quiz platform.
          </motion.p>

          <motion.div
            variants={itemVariants}
            className="flex flex-col sm:flex-row gap-4 justify-center"
          >
            {user ? (
              <>
                {user.role === 'ADMIN' ? (
                  <Link to="/admin/dashboard" className="btn-primary">
                    Go to Admin Dashboard
                  </Link>
                ) : (
                  <Link to="/participant/quizzes" className="btn-primary">
                    Browse Quizzes
                  </Link>
                )}
              </>
            ) : (
              <>
                <Link to="/register" className="btn-primary">
                  Get Started
                </Link>
                <Link to="/login" className="btn-secondary">
                  Login
                </Link>
              </>
            )}
          </motion.div>
        </motion.div>
      </section>

      {/* Features Section */}
      <section className="py-20 px-4 bg-white dark:bg-gray-900">
        <div className="max-w-6xl mx-auto">
          <h2 className="text-4xl font-bold text-center gradient-text mb-16">Features</h2>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[
              {
                icon: '📝',
                title: 'Create Quizzes',
                description: 'Admins can easily create and manage quizzes with multiple-choice questions.',
              },
              {
                icon: '🎯',
                title: 'Take Tests',
                description: 'Participants can take quizzes with timers and get instant feedback on their performance.',
              },
              {
                icon: '📊',
                title: 'Track Progress',
                description: 'View detailed analytics and history of quiz attempts to monitor your improvement.',
              },
            ].map((feature, index) => (
              <motion.div
                key={index}
                initial={{ opacity: 0, y: 20 }}
                whileInView={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.2 }}
                className="card text-center"
              >
                <div className="text-5xl mb-4">{feature.icon}</div>
                <h3 className="text-2xl font-bold mb-2">{feature.title}</h3>
                <p className="text-gray-600 dark:text-gray-400">{feature.description}</p>
              </motion.div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
};
