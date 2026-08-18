import React, { useState, useEffect } from 'react';
import api from '../utils/api';
import { useAuth } from '../store/AuthContext';

export const NotificationsPage = () => {
  const { user } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (user) {
      loadNotifications();
    }
  }, [user]);

  const loadNotifications = async () => {
    try {
      const res = await api.get('/notifications');
      setNotifications(res.data.content || []);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleMarkRead = async (id) => {
    try {
      await api.patch(`/notifications/${id}/read`);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, isRead: true } : n));
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-3xl font-bold tracking-tight text-white mb-8">Notifications</h1>

      {loading ? (
        <div className="space-y-4 animate-pulse">
          {[1, 2].map(i => <div key={i} className="h-16 bg-slate-900 border border-slate-800 rounded-lg" />)}
        </div>
      ) : notifications.length === 0 ? (
        <div className="text-slate-400">No notifications yet.</div>
      ) : (
        <div className="space-y-4">
          {notifications.map(n => (
            <div key={n.id} className={`p-4 rounded-xl border flex justify-between items-center ${
              n.isRead ? 'bg-slate-900/30 border-slate-850 opacity-70' : 'bg-slate-900 border-slate-800'
            }`}>
              <div>
                <h4 className="font-semibold text-white text-sm">{n.title}</h4>
                <p className="text-slate-400 text-xs mt-1">{n.message}</p>
                <span className="text-[10px] text-slate-500 mt-2 block">{new Date(n.createdAt).toLocaleString()}</span>
              </div>
              {!n.isRead && (
                <button
                  onClick={() => handleMarkRead(n.id)}
                  className="text-xs text-emerald-400 hover:underline shrink-0"
                >
                  Mark read
                </button>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
