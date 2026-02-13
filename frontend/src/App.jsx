import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';

// Layout
import { Layout } from './components/layout/Layout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';

// Pages
import { LandingPage } from './components/pages/LandingPage';
import { LoginPage } from './components/pages/LoginPage';
import { RegisterPage } from './components/pages/RegisterPage';
import { NotFoundPage } from './components/pages/NotFoundPage';

// Admin Components
import { AdminDashboard } from './components/admin/AdminDashboard';
import { CreateQuizPage } from './components/admin/CreateQuizPage';
import { EditQuizPage } from './components/admin/EditQuizPage';
import { AdminResultsPage } from './components/admin/AdminResultsPage';

// Participant Components
import { QuizBrowsing } from './components/participant/QuizBrowsing';
import { QuizTakingPage } from './components/participant/QuizTakingPage';
import { ResultsPage } from './components/participant/ResultsPage';
import { HistoryPage } from './components/participant/HistoryPage';

// Styles
import './styles/index.css';

function App() {
  return (
    <BrowserRouter>
      <Layout>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<LandingPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />

          {/* Admin Routes */}
          <Route
            path="/admin/dashboard"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <AdminDashboard />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/create-quiz"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <CreateQuizPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/quiz/:quizId/edit"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <EditQuizPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/admin/quiz/:quizId/results"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <AdminResultsPage />
              </ProtectedRoute>
            }
          />

          {/* Participant Routes */}
          <Route
            path="/participant/quizzes"
            element={
              <ProtectedRoute requiredRole="PARTICIPANT">
                <QuizBrowsing />
              </ProtectedRoute>
            }
          />
          <Route
            path="/participant/quiz/:quizId"
            element={
              <ProtectedRoute requiredRole="PARTICIPANT">
                <QuizTakingPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/participant/results/:attemptId"
            element={
              <ProtectedRoute requiredRole="PARTICIPANT">
                <ResultsPage />
              </ProtectedRoute>
            }
          />
          <Route
            path="/participant/history"
            element={
              <ProtectedRoute requiredRole="PARTICIPANT">
                <HistoryPage />
              </ProtectedRoute>
            }
          />

          {/* Catch-all Route */}
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </Layout>
      
      <Toaster
        position="top-right"
        reverseOrder={false}
        toastOptions={{
          duration: 3000,
        }}
      />
    </BrowserRouter>
  );
}

export default App;
