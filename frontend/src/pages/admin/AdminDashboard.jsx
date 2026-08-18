import React from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../../utils/api';

export const AdminDashboard = () => {
  const { data: stats } = useQuery({
    queryKey: ['admin-stats'],
    queryFn: async () => {
      const res = await api.get('/admin/dashboard');
      return res.data;
    }
  });

  const { data: usersPage, refetch } = useQuery({
    queryKey: ['admin-users'],
    queryFn: async () => {
      const res = await api.get('/admin/users');
      return res.data;
    }
  });

  const users = usersPage?.content || [];

  const handleToggleStatus = async (userId, currentStatus) => {
    const nextStatus = currentStatus === 'ACTIVE' ? 'SUSPENDED' : 'ACTIVE';
    try {
      await api.patch(`/admin/users/${userId}/status`, null, { params: { status: nextStatus } });
      refetch();
    } catch (err) {
      alert('Failed to change user status');
    }
  };

  return (
    <div>
      <h1 className="text-3xl font-bold tracking-tight text-white mb-8">Admin Control Console</h1>

      <div className="grid grid-cols-2 md:grid-cols-4 gap-6 mb-10">
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Total Users</div>
          <div className="text-3xl font-bold text-white mt-2">{stats?.totalUsers || 0}</div>
        </div>
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Total Providers</div>
          <div className="text-3xl font-bold text-white mt-2">{stats?.totalProviders || 0}</div>
        </div>
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Active Bookings</div>
          <div className="text-3xl font-bold text-emerald-400 mt-2">{stats?.totalActiveBookings || 0}</div>
        </div>
        <div className="bg-slate-900/50 border border-slate-800 p-5 rounded-xl">
          <div className="text-xs text-slate-500 font-semibold uppercase">Total Slots</div>
          <div className="text-3xl font-bold text-white mt-2">{stats?.totalSlots || 0}</div>
        </div>
      </div>

      <div className="bg-slate-900/50 border border-slate-800 rounded-xl p-6">
        <h3 className="text-xl font-bold text-white mb-6">User Accounts</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="border-b border-slate-800 text-slate-400 text-sm">
                <th className="pb-3 font-semibold">User</th>
                <th className="pb-3 font-semibold">Role</th>
                <th className="pb-3 font-semibold">Action</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-850">
              {users.map(u => (
                <tr key={u.id} className="text-sm">
                  <td className="py-3.5 text-white">{u.email}</td>
                  <td className="py-3.5 text-slate-300">{u.role}</td>
                  <td className="py-3.5">
                    <button
                      onClick={() => handleToggleStatus(u.id, 'ACTIVE')}
                      className="text-xs bg-slate-800 hover:bg-slate-700 text-slate-300 px-3 py-1.5 rounded-lg"
                    >
                      Suspend / Activate
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
