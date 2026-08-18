import React, { useState } from 'react';
import { useAuth } from '../store/AuthContext';
import api from '../utils/api';

export const ProfilePage = () => {
  const { user } = useAuth();
  const [firstName, setFirstName] = useState(user?.firstName || '');
  const [lastName, setLastName] = useState(user?.lastName || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [loading, setLoading] = useState(false);

  const handleUpdate = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await api.put('/providers/profile', {
        name: firstName + ' ' + lastName,
        category: 'OTHER',
        timezone: 'UTC'
      });
      alert('Profile updated successfully!');
    } catch (err) {
      alert('Failed to update profile details');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-xl mx-auto bg-slate-900/50 border border-slate-800 p-8 rounded-2xl">
      <h2 className="text-2xl font-bold text-white mb-6">User Profile & Info</h2>
      <form onSubmit={handleUpdate} className="space-y-6">
        <div>
          <label className="block text-sm text-slate-300 mb-1">Email Address</label>
          <input type="text" disabled value={user?.email || ''} className="w-full bg-slate-950/50 border border-slate-800 rounded-lg p-3 text-slate-500 cursor-not-allowed" />
        </div>
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm text-slate-300 mb-1">First Name</label>
            <input type="text" required value={firstName} onChange={e => setFirstName(e.target.value)} className="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 text-slate-100 focus:outline-none focus:border-emerald-500" />
          </div>
          <div>
            <label className="block text-sm text-slate-300 mb-1">Last Name</label>
            <input type="text" required value={lastName} onChange={e => setLastName(e.target.value)} className="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 text-slate-100 focus:outline-none focus:border-emerald-500" />
          </div>
        </div>
        <div>
          <label className="block text-sm text-slate-300 mb-1">Phone Number</label>
          <input type="text" value={phone} onChange={e => setPhone(e.target.value)} className="w-full bg-slate-950 border border-slate-800 rounded-lg p-3 text-slate-100 focus:outline-none focus:border-emerald-500" />
        </div>
        <button type="submit" disabled={loading} className="w-full bg-gradient-to-r from-emerald-500 to-teal-600 text-white font-bold p-3 rounded-lg hover:opacity-90 transition-opacity">
          {loading ? 'Saving...' : 'Save Settings'}
        </button>
      </form>
    </div>
  );
};
