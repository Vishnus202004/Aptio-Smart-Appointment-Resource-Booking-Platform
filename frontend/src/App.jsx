import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './store/AuthContext';
import { Layout } from './components/layout/Layout';
import { ProtectedRoute } from './components/layout/ProtectedRoute';
import { LandingPage } from './pages/LandingPage';
import { Login } from './pages/auth/Login';
import { Register } from './pages/auth/Register';
import { CustomerDashboard } from './pages/customer/CustomerDashboard';
import { BookingCalendar } from './pages/customer/BookingCalendar';
import { BookingHistory } from './pages/customer/BookingHistory';
import { ProviderDashboard } from './pages/provider/ProviderDashboard';
import { AdminDashboard } from './pages/admin/AdminDashboard';
import { NotificationsPage } from './pages/NotificationsPage';
import { ProfilePage } from './pages/ProfilePage';

const queryClient = new QueryClient();

const App = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <BrowserRouter>
          <Layout>
            <Routes>
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              
              {/* Customer Routes */}
              <Route element={<ProtectedRoute allowedRoles={['CUSTOMER']} />}>
                <Route path="/customer" element={<CustomerDashboard />} />
                <Route path="/book/:providerId" element={<BookingCalendar />} />
                <Route path="/bookings" element={<BookingHistory />} />
              </Route>

              {/* Provider Routes */}
              <Route element={<ProtectedRoute allowedRoles={['PROVIDER']} />}>
                <Route path="/provider" element={<ProviderDashboard />} />
              </Route>

              {/* Admin Routes */}
              <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                <Route path="/admin" element={<AdminDashboard />} />
              </Route>

              {/* Shared Protected Routes */}
              <Route element={<ProtectedRoute />}>
                <Route path="/notifications" element={<NotificationsPage />} />
                <Route path="/profile" element={<ProfilePage />} />
              </Route>
            </Routes>
          </Layout>
        </BrowserRouter>
      </AuthProvider>
    </QueryClientProvider>
  );
};

export default App;
