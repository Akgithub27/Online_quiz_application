import React from 'react';
import { motion } from 'framer-motion';

export const NotFoundPage = () => {
  return (
    <div className="min-h-screen flex items-center justify-center px-4">
      <motion.div
        initial={{ opacity: 0, scale: 0.9 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 0.5 }}
        className="text-center"
      >
        <h1 className="text-6xl font-bold gradient-text mb-4">404</h1>
        <p className="text-2xl font-semibold text-gray-800 dark:text-white mb-4">Page Not Found</p>
        <p className="text-gray-600 dark:text-gray-400 mb-8">
          The page you are looking for doesn't exist.
        </p>
        <a href="/" className="btn-primary inline-block">
          Go Home
        </a>
      </motion.div>
    </div>
  );
};
