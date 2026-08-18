import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../../store/AuthContext';

export const Layout = ({ children }) => {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100 font-sans">
      <nav className="border-b border-slate-800 bg-slate-900/50 backdrop-blur sticky top-0 z-50">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 justify-between items-center">
            <div className="flex items-center space-x-8">
              <Link to="/" className="text-xl font-bold bg-gradient-to-r from-emerald-400 to-teal-500 bg-clip-text text-transparent">
                SlotSync
              </Link>
              {user && (
                <div className="hidden md:flex space-x-4">
                  {user.role === 'CUSTOMER' && (
                    <>
                      <Link to="/customer" className="hover:text-emerald-400 transition-colors">Find Providers</Link>
                      <Link to="/bookings" className="hover:text-emerald-400 transition-colors">My Bookings</Link>
                    </>
                  )}
                  {user.role === 'PROVIDER' && (
                    <>
                      <Link to="/provider" className="hover:text-emerald-400 transition-colors">Dashboard</Link>
                    </>
                  )}
                  {user.role === 'ADMIN' && (
                    <>
                      <Link to="/admin" className="hover:text-emerald-400 transition-colors">Console</Link>
                    </>
                  )}
                </div>
              )}
            </div>
            <div className="flex items-center space-x-4">
              {user ? (
                <>
                  <Link to="/profile" className="hover:text-emerald-400 transition-colors text-sm">
                    {user.firstName} ({user.role})
                  </Link>
                  <button onClick={handleLogout} className="text-sm bg-slate-800 hover:bg-slate-700 px-3 py-1.5 rounded-lg transition-colors">
                    Logout
                  </button>
                </>
              ) : (
                <>
                  <Link to="/login" className="text-sm hover:text-emerald-400 transition-colors">Sign In</Link>
                  <Link to="/register" className="text-sm bg-gradient-to-r from-emerald-500 to-teal-600 hover:opacity-90 px-4 py-2 rounded-lg transition-opacity font-semibold">
                    Sign Up
                  </Link>
                </>
              )}
            </div>
          </div>
        </div>
      </nav>
      <main className="mx-auto max-w-7xl px-4 sm:px-6 lg:px-8 py-8">
        {children}
      </main>
    </div>
  );
};
