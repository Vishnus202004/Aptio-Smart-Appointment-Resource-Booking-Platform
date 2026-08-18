import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import api from '../../utils/api';

const statusColors = {
  CONFIRMED: 'bg-emerald-950/50 border-emerald-800 text-emerald-400',
  CANCELLED: 'bg-slate-900/60 border-slate-700 text-slate-500',
  PENDING: 'bg-amber-950/50 border-amber-800 text-amber-400',
};

export const BookingHistory = () => {
  const [cancellingId, setCancellingId] = useState(null);
  const [toast, setToast] = useState(null);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4000);
  };

  const { data: bookings = [], isLoading, refetch } = useQuery({
    queryKey: ['my-bookings'],
    queryFn: async () => {
      const res = await api.get('/bookings');
      return res.data;
    }
  });

  const handleCancel = async (bookingId) => {
    if (!window.confirm('Cancel this booking? The slot will be released to the waitlist.')) return;
    setCancellingId(bookingId);
    try {
      await api.delete(`/bookings/${bookingId}`);
      showToast('Booking cancelled successfully.', 'success');
      refetch();
    } catch (err) {
      showToast(err.response?.data?.message || 'Failed to cancel. Try again.', 'error');
    } finally {
      setCancellingId(null);
    }
  };

  const confirmed = bookings.filter(b => b.status === 'CONFIRMED');
  const cancelled = bookings.filter(b => b.status === 'CANCELLED');

  return (
    <div className="max-w-3xl mx-auto">
      <div className="flex items-center justify-between mb-8">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-white">My Bookings</h1>
          <p className="text-slate-400 mt-1 text-sm">Manage your upcoming and past appointments.</p>
        </div>
        <Link
          to="/customer"
          className="text-sm bg-gradient-to-r from-emerald-500 to-teal-600 text-white font-semibold px-4 py-2 rounded-xl hover:opacity-90 transition-opacity"
        >
          + New Booking
        </Link>
      </div>

      {isLoading ? (
        <div className="space-y-4">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-24 bg-slate-900/50 border border-slate-800 rounded-xl animate-pulse" />
          ))}
        </div>
      ) : bookings.length === 0 ? (
        <div className="text-center py-20">
          <div className="text-5xl mb-4">📅</div>
          <h2 className="text-xl font-bold text-slate-300 mb-2">No bookings yet</h2>
          <p className="text-slate-500 text-sm mb-6">Browse providers and book your first appointment.</p>
          <Link
            to="/customer"
            className="inline-block bg-gradient-to-r from-emerald-500 to-teal-600 text-white font-semibold px-6 py-3 rounded-xl hover:opacity-90 transition-opacity text-sm"
          >
            Browse Providers
          </Link>
        </div>
      ) : (
        <div className="space-y-8">
          {/* Confirmed bookings */}
          {confirmed.length > 0 && (
            <section>
              <h2 className="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-4">
                Upcoming ({confirmed.length})
              </h2>
              <div className="space-y-4">
                {confirmed.map(b => (
                  <div
                    key={b.id}
                    className="bg-slate-900/60 border border-slate-800 hover:border-slate-700 p-5 rounded-2xl flex flex-col sm:flex-row sm:items-center justify-between gap-4 transition-colors"
                  >
                    <div className="flex items-start gap-4">
                      <div className="w-12 h-12 rounded-xl bg-emerald-950/50 border border-emerald-900 flex flex-col items-center justify-center shrink-0">
                        <span className="text-emerald-400 font-bold text-base leading-none">
                          {new Date(b.slot?.startTime || b.bookedAt).getDate()}
                        </span>
                        <span className="text-emerald-600 text-xs">
                          {new Date(b.slot?.startTime || b.bookedAt).toLocaleDateString('en-US', { month: 'short' })}
                        </span>
                      </div>
                      <div>
                        <div className="flex items-center gap-2 mb-1">
                          <span className={`px-2 py-0.5 text-xs font-bold rounded-full border ${statusColors[b.status] || statusColors.CONFIRMED}`}>
                            {b.status}
                          </span>
                        </div>
                        <h3 className="text-white font-bold text-base leading-tight">
                          {b.slot?.title || 'Appointment'}
                        </h3>
                        <p className="text-slate-400 text-sm mt-0.5">
                          {b.slot?.startTime
                            ? new Date(b.slot.startTime).toLocaleString('en-US', {
                                weekday: 'short', month: 'short', day: 'numeric',
                                hour: '2-digit', minute: '2-digit'
                              })
                            : 'Date unavailable'}
                          {b.slot?.durationMinutes && ` · ${b.slot.durationMinutes} min`}
                        </p>
                        {b.notes && (
                          <p className="text-slate-500 text-xs mt-1.5 italic">"{b.notes}"</p>
                        )}
                      </div>
                    </div>
                    <button
                      onClick={() => handleCancel(b.id)}
                      disabled={cancellingId === b.id}
                      className="shrink-0 text-sm bg-red-950/30 hover:bg-red-950/60 border border-red-900 text-red-400 px-4 py-2 rounded-xl transition-colors disabled:opacity-50"
                    >
                      {cancellingId === b.id ? 'Cancelling...' : 'Cancel'}
                    </button>
                  </div>
                ))}
              </div>
            </section>
          )}

          {/* Cancelled bookings */}
          {cancelled.length > 0 && (
            <section>
              <h2 className="text-sm font-semibold text-slate-500 uppercase tracking-widest mb-4">
                Cancelled ({cancelled.length})
              </h2>
              <div className="space-y-3">
                {cancelled.map(b => (
                  <div
                    key={b.id}
                    className="bg-slate-900/30 border border-slate-800/50 p-5 rounded-2xl flex items-start gap-4 opacity-60"
                  >
                    <div className="w-12 h-12 rounded-xl bg-slate-800/50 border border-slate-700 flex flex-col items-center justify-center shrink-0">
                      <span className="text-slate-500 font-bold text-base leading-none">
                        {new Date(b.slot?.startTime || b.bookedAt).getDate()}
                      </span>
                      <span className="text-slate-600 text-xs">
                        {new Date(b.slot?.startTime || b.bookedAt).toLocaleDateString('en-US', { month: 'short' })}
                      </span>
                    </div>
                    <div>
                      <span className={`px-2 py-0.5 text-xs font-bold rounded-full border ${statusColors.CANCELLED}`}>
                        CANCELLED
                      </span>
                      <h3 className="text-slate-400 font-semibold text-sm mt-1">
                        {b.slot?.title || 'Appointment'}
                      </h3>
                      <p className="text-slate-600 text-xs mt-0.5">
                        {b.slot?.startTime
                          ? new Date(b.slot.startTime).toLocaleDateString('en-US', {
                              weekday: 'short', month: 'short', day: 'numeric'
                            })
                          : 'Date unavailable'}
                      </p>
                      {b.cancellationReason && (
                        <p className="text-slate-600 text-xs mt-1">Reason: {b.cancellationReason}</p>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </section>
          )}
        </div>
      )}

      {/* Toast */}
      {toast && (
        <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-4 rounded-xl shadow-2xl border text-sm font-semibold ${
          toast.type === 'success'
            ? 'bg-emerald-950 border-emerald-700 text-emerald-300'
            : 'bg-red-950 border-red-800 text-red-300'
        }`}>
          <span>{toast.type === 'success' ? '✅' : '❌'}</span>
          <span>{toast.message}</span>
          <button onClick={() => setToast(null)} className="ml-2 opacity-60 hover:opacity-100">✕</button>
        </div>
      )}
    </div>
  );
};
