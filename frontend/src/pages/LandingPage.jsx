import React from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'framer-motion';

export const LandingPage = () => {
  return (
    <div className="flex flex-col items-center justify-center min-h-[80vh] text-center px-4">
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.8 }}
      >
        <span className="px-3 py-1 text-xs font-semibold tracking-wider text-emerald-400 bg-emerald-950/50 border border-emerald-800 rounded-full uppercase">
          Production-Ready Booking Platform
        </span>
        <h1 className="mt-6 text-4xl sm:text-6xl font-extrabold tracking-tight bg-gradient-to-r from-white via-slate-200 to-slate-400 bg-clip-text text-transparent">
          Smart Booking Engine for <br />
          <span className="bg-gradient-to-r from-emerald-400 to-teal-500 bg-clip-text text-transparent">Modern Enterprise</span>
        </h1>
        <p className="mt-6 mx-auto max-w-2xl text-slate-400 text-lg sm:text-xl">
          Zero double-bookings guarantee. Powered by database locks, FIFO waitlist promotion, real-time STOMP updates, and clean architecture.
        </p>
        <div className="mt-10 flex flex-wrap justify-center gap-4">
          <Link to="/register" className="bg-gradient-to-r from-emerald-500 to-teal-600 hover:opacity-95 text-white font-bold px-8 py-3.5 rounded-xl shadow-lg shadow-emerald-500/20 transition-all transform hover:-translate-y-0.5">
            Get Started
          </Link>
          <Link to="/login" className="bg-slate-900 border border-slate-800 hover:bg-slate-800 text-slate-200 font-semibold px-8 py-3.5 rounded-xl transition-all">
            Dashboard Sign In
          </Link>
        </div>
      </motion.div>
    </div>
  );
};
