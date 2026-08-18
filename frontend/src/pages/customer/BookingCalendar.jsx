import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import api from '../../utils/api';

const Modal = ({ slot, notes, setNotes, loading, onConfirm, onCancel }) => {
  if (!slot) return null;
  const isBooked = slot.status === 'BOOKED';

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/70 backdrop-blur-sm"
        onClick={onCancel}
      />
      {/* Dialog */}
      <div className="relative z-10 w-full max-w-md bg-slate-900 border border-slate-700 rounded-2xl shadow-2xl p-6">
        {/* Header */}
        <div className="flex items-start justify-between mb-5">
          <div>
            <h2 className="text-xl font-bold text-white">
              {isBooked ? 'Join Waitlist' : 'Confirm Booking'}
            </h2>
            <p className="text-slate-400 text-sm mt-1">
              {isBooked
                ? 'This slot is full. You can join the waitlist.'
                : 'Review details and confirm your appointment.'}
            </p>
          </div>
          <button
            onClick={onCancel}
            className="text-slate-500 hover:text-white transition-colors text-xl leading-none ml-4 mt-0.5"
          >
            ✕
          </button>
        </div>

        {/* Slot details */}
        <div className="bg-slate-800/60 rounded-xl p-4 mb-5 space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-slate-400 text-sm">Title</span>
            <span className="text-white font-semibold text-sm">{slot.title || 'Appointment'}</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-slate-400 text-sm">Date</span>
            <span className="text-white font-semibold text-sm">
              {new Date(slot.startTime).toLocaleDateString('en-US', {
                weekday: 'short', year: 'numeric', month: 'short', day: 'numeric'
              })}
            </span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-slate-400 text-sm">Time</span>
            <span className="text-white font-semibold text-sm">
              {new Date(slot.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
              {' → '}
              {new Date(slot.endTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
            </span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-slate-400 text-sm">Duration</span>
            <span className="text-white font-semibold text-sm">{slot.durationMinutes} min</span>
          </div>
          <div className="flex justify-between items-center">
            <span className="text-slate-400 text-sm">Status</span>
            <span className={`text-xs font-bold px-2 py-0.5 rounded-full ${
              isBooked
                ? 'bg-amber-950/50 border border-amber-800 text-amber-400'
                : 'bg-emerald-950/50 border border-emerald-800 text-emerald-400'
            }`}>
              {isBooked ? 'FULL' : 'AVAILABLE'}
            </span>
          </div>
        </div>

        {/* Notes */}
        {!isBooked && (
          <div className="mb-5">
            <label className="block text-sm font-semibold text-slate-300 mb-2">
              Notes <span className="text-slate-500 font-normal">(optional)</span>
            </label>
            <textarea
              value={notes}
              onChange={e => setNotes(e.target.value)}
              placeholder="Any details for the provider..."
              rows={3}
              className="w-full bg-slate-800 border border-slate-700 rounded-lg p-3 text-sm text-slate-100 placeholder-slate-500 focus:outline-none focus:border-emerald-500 resize-none"
            />
          </div>
        )}

        {/* Actions */}
        <div className="flex gap-3">
          <button
            onClick={onConfirm}
            disabled={loading}
            className={`flex-1 font-bold py-3 rounded-xl text-sm transition-all disabled:opacity-50 disabled:cursor-not-allowed ${
              isBooked
                ? 'bg-gradient-to-r from-amber-500 to-orange-600 text-white hover:opacity-90'
                : 'bg-gradient-to-r from-emerald-500 to-teal-600 text-white hover:opacity-90'
            }`}
          >
            {loading
              ? (isBooked ? 'Joining...' : 'Booking...')
              : (isBooked ? '📋 Join Waitlist' : '✅ Confirm Booking')}
          </button>
          <button
            onClick={onCancel}
            disabled={loading}
            className="px-5 py-3 bg-slate-800 hover:bg-slate-700 text-slate-300 rounded-xl text-sm font-semibold transition-colors"
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
};

const Toast = ({ message, type, onClose }) => {
  if (!message) return null;
  return (
    <div className={`fixed bottom-6 right-6 z-50 flex items-center gap-3 px-5 py-4 rounded-xl shadow-2xl border text-sm font-semibold animate-fade-in ${
      type === 'success'
        ? 'bg-emerald-950 border-emerald-700 text-emerald-300'
        : 'bg-red-950 border-red-800 text-red-300'
    }`}>
      <span>{type === 'success' ? '✅' : '❌'}</span>
      <span>{message}</span>
      <button onClick={onClose} className="ml-2 opacity-60 hover:opacity-100">✕</button>
    </div>
  );
};

export const BookingCalendar = () => {
  const { providerId } = useParams();
  const navigate = useNavigate();
  const [selectedSlot, setSelectedSlot] = useState(null);
  const [notes, setNotes] = useState('');
  const [loading, setLoading] = useState(false);
  const [toast, setToast] = useState(null);
  const [weekOffset, setWeekOffset] = useState(0);

  const showToast = (message, type = 'success') => {
    setToast({ message, type });
    setTimeout(() => setToast(null), 4000);
  };

  const startDate = new Date();
  startDate.setDate(startDate.getDate() + weekOffset * 7);
  const endDate = new Date(startDate);
  endDate.setDate(endDate.getDate() + 14);

  const { data: provider, isLoading: providerLoading } = useQuery({
    queryKey: ['provider', providerId],
    queryFn: async () => {
      const res = await api.get(`/providers/${providerId}`);
      return res.data;
    }
  });

  const { data: slots = [], isLoading: slotsLoading, refetch: refetchSlots } = useQuery({
    queryKey: ['slots', providerId, weekOffset],
    queryFn: async () => {
      const res = await api.get(`/slots/provider/${providerId}`, {
        params: {
          start: startDate.toISOString(),
          end: endDate.toISOString()
        }
      });
      return res.data;
    }
  });

  const handleConfirm = async () => {
    if (!selectedSlot) return;
    setLoading(true);
    const isBooked = selectedSlot.status === 'BOOKED';

    try {
      if (isBooked) {
        await api.post(`/bookings/slots/${selectedSlot.id}/waitlist`);
        showToast('Joined the waitlist! You will be notified if a spot opens.', 'success');
      } else {
        await api.post('/bookings', { slotId: selectedSlot.id, notes });
        showToast('Booking confirmed! 🎉', 'success');
        refetchSlots();
      }
      setSelectedSlot(null);
      setNotes('');
      setTimeout(() => navigate('/bookings'), 1500);
    } catch (err) {
      if (err.response?.status === 409 && !isBooked) {
        showToast('This slot was just taken. Please pick another.', 'error');
        refetchSlots();
      } else {
        showToast(err.response?.data?.message || 'Something went wrong. Please try again.', 'error');
      }
    } finally {
      setLoading(false);
    }
  };

  const availableSlots = slots.filter(s => s.status === 'AVAILABLE');
  const bookedSlots = slots.filter(s => s.status === 'BOOKED');

  const SlotCard = ({ slot }) => {
    const available = slot.status === 'AVAILABLE';
    return (
      <button
        onClick={() => { setSelectedSlot(slot); setNotes(''); }}
        className={`w-full text-left p-4 rounded-xl border transition-all group ${
          available
            ? 'bg-slate-900 border-slate-700 hover:border-emerald-500 hover:bg-slate-800/80 cursor-pointer'
            : 'bg-slate-900/40 border-slate-800 hover:border-amber-600 hover:bg-slate-800/40 cursor-pointer'
        }`}
      >
        <div className="flex items-start justify-between gap-2">
          <div className="min-w-0">
            <div className="text-xs text-slate-500 mb-1">
              {new Date(slot.startTime).toLocaleDateString('en-US', {
                weekday: 'short', month: 'short', day: 'numeric'
              })}
            </div>
            <div className="text-white font-bold text-base leading-tight truncate">
              {new Date(slot.startTime).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
            </div>
            <div className="text-slate-400 text-xs mt-1 truncate">{slot.title || 'Appointment'}</div>
            <div className="text-slate-500 text-xs mt-1">{slot.durationMinutes} min</div>
          </div>
          <span className={`mt-1 shrink-0 text-xs font-bold px-2 py-0.5 rounded-full border ${
            available
              ? 'bg-emerald-950/50 border-emerald-800 text-emerald-400'
              : 'bg-amber-950/50 border-amber-800 text-amber-400'
          }`}>
            {available ? 'Open' : 'Waitlist'}
          </span>
        </div>
      </button>
    );
  };

  return (
    <div className="max-w-4xl mx-auto">
      {/* Provider header */}
      {providerLoading ? (
        <div className="h-20 bg-slate-900/50 rounded-xl animate-pulse mb-6" />
      ) : provider ? (
        <div className="mb-8">
          <div className="flex items-center gap-3 mb-1">
            <span className="px-2.5 py-0.5 text-xs font-bold text-emerald-400 bg-emerald-950/50 border border-emerald-900 rounded-full">
              {provider.category}
            </span>
            {provider.location && (
              <span className="text-slate-500 text-xs">📍 {provider.location}</span>
            )}
          </div>
          <h1 className="text-3xl font-bold text-white tracking-tight">{provider.name}</h1>
          {provider.description && (
            <p className="text-slate-400 mt-2 text-sm leading-relaxed max-w-2xl">{provider.description}</p>
          )}
        </div>
      ) : null}

      {/* Week nav */}
      <div className="flex items-center justify-between mb-6">
        <h2 className="text-lg font-semibold text-slate-200">
          Available Slots
          <span className="ml-2 text-sm font-normal text-slate-500">
            {startDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })} –{' '}
            {endDate.toLocaleDateString('en-US', { month: 'short', day: 'numeric' })}
          </span>
        </h2>
        <div className="flex gap-2">
          <button
            onClick={() => setWeekOffset(w => Math.max(0, w - 1))}
            disabled={weekOffset === 0}
            className="px-3 py-1.5 text-sm bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors disabled:opacity-40"
          >
            ← Prev
          </button>
          <button
            onClick={() => setWeekOffset(w => w + 1)}
            className="px-3 py-1.5 text-sm bg-slate-800 hover:bg-slate-700 rounded-lg transition-colors"
          >
            Next →
          </button>
        </div>
      </div>

      {/* Slots grid */}
      {slotsLoading ? (
        <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-4">
          {[...Array(8)].map((_, i) => (
            <div key={i} className="h-28 bg-slate-900/50 rounded-xl animate-pulse" />
          ))}
        </div>
      ) : slots.length === 0 ? (
        <div className="text-center py-20 text-slate-500">
          <div className="text-4xl mb-3">📅</div>
          <p className="text-lg font-semibold text-slate-400">No slots in this period</p>
          <p className="text-sm mt-1">Try the next two weeks.</p>
        </div>
      ) : (
        <div className="space-y-6">
          {availableSlots.length > 0 && (
            <div>
              <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-3">
                ✅ Available ({availableSlots.length})
              </h3>
              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                {availableSlots.map(slot => <SlotCard key={slot.id} slot={slot} />)}
              </div>
            </div>
          )}
          {bookedSlots.length > 0 && (
            <div>
              <h3 className="text-sm font-semibold text-slate-400 uppercase tracking-widest mb-3">
                📋 Join Waitlist ({bookedSlots.length})
              </h3>
              <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-3">
                {bookedSlots.map(slot => <SlotCard key={slot.id} slot={slot} />)}
              </div>
            </div>
          )}
        </div>
      )}

      {/* Booking modal */}
      <Modal
        slot={selectedSlot}
        notes={notes}
        setNotes={setNotes}
        loading={loading}
        onConfirm={handleConfirm}
        onCancel={() => { setSelectedSlot(null); setNotes(''); }}
      />

      {/* Toast */}
      <Toast
        message={toast?.message}
        type={toast?.type}
        onClose={() => setToast(null)}
      />
    </div>
  );
};
