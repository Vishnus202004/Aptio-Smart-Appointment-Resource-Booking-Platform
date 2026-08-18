import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../../utils/api';
import { useAuth } from '../../store/AuthContext';

export const ProviderDashboard = () => {
  const { user } = useAuth();
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [title, setTitle] = useState('');
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);

  // Load stats
  const { data: stats } = useQuery({
    queryKey: ['provider-stats'],
    queryFn: async () => {
      // Get profile first to find profileId
      const profileRes = await api.get('/providers/profile');
      const statsRes = await api.get(`/providers/${profileRes.data.id}/dashboard`);
      return statsRes.data;
    }
  });

  const handleCreateSlot = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const profileRes = await api.get('/providers/profile');
      await api.post('/slots', {
        providerProfileId: profileRes.data.id,
        startTime: new Date(startTime).toISOString(),
        endTime: new Date(endTime).toISOString(),
        title,
        notes
      });
      alert('Slot Created!');
      setTitle('');
      setStartTime('');
      setEndTime('');
      setNotes('');
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to create slot');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1 className="text-3xl font-bold tracking-tight text-white mb-8">Provider Workspace</h1>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-10">
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Total Slots</div>
          <div className="text-3xl font-bold text-white mt-2">{stats?.totalSlots || 0}</div>
        </div>
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Occupied Slots</div>
          <div className="text-3xl font-bold text-emerald-400 mt-2">{stats?.bookedSlots || 0}</div>
        </div>
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Occupancy Rate</div>
          <div className="text-3xl font-bold text-emerald-400 mt-2">
            {stats?.occupancyRate ? stats.occupancyRate.toFixed(1) : 0}%
          </div>
        </div>
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Total Bookings</div>
          <div className="text-3xl font-bold text-white mt-2">{stats?.totalBookings || 0}</div>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
        <div className="bg-slate-900/50 border border-slate-800 p-6 rounded-xl">
          <h3 className="text-xl font-bold text-white mb-6">Create Booking Slot</h3>
          <form onSubmit={handleCreateSlot} className="space-y-4">
            <div>
              <label className="block text-sm text-slate-300 mb-1">Slot Title</label>
              <input type="text" required value={title} onChange={e => setTitle(e.target.value)} placeholder="e.g. Consulting Hour" className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-slate-100 focus:outline-none focus:border-emerald-500" />
            </div>
            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm text-slate-300 mb-1">Start Time</label>
                <input type="datetime-local" required value={startTime} onChange={e => setStartTime(e.target.value)} className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-slate-100 focus:outline-none" />
              </div>
              <div>
                <label className="block text-sm text-slate-300 mb-1">End Time</label>
                <input type="datetime-local" required value={endTime} onChange={e => setEndTime(e.target.value)} className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-slate-100 focus:outline-none" />
              </div>
            </div>
            <div>
              <label className="block text-sm text-slate-300 mb-1">Notes</label>
              <textarea value={notes} onChange={e => setNotes(e.target.value)} placeholder="Provide special instructions..." className="w-full bg-slate-950 border border-slate-800 rounded-lg p-2.5 text-sm text-slate-100 focus:outline-none" />
            </div>
            <button type="submit" disabled={loading} className="w-full bg-gradient-to-r from-emerald-500 to-teal-600 text-white font-bold p-3 rounded-lg hover:opacity-90 transition-opacity">
              {loading ? 'Creating...' : 'Create Slot'}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
};
