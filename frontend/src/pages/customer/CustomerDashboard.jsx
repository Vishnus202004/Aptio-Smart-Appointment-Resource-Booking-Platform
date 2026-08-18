import React, { useState } from 'react';
import { useQuery as useReactQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import api from '../../utils/api';

export const CustomerDashboard = () => {
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('');

  const { data, isLoading } = useReactQuery({
    queryKey: ['providers', search, category],
    queryFn: async () => {
      const res = await api.get('/providers', {
        params: { search, category, page: 0, size: 12 }
      });
      return res.data;
    }
  });

  const providers = data?.content || [];

  return (
    <div>
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4 mb-8">
        <div>
          <h1 className="text-3xl font-bold tracking-tight text-white">Find a Provider</h1>
          <p className="text-slate-400 mt-2">Browse categories or search name/description to book custom slots.</p>
        </div>
        <div className="flex gap-3">
          <input
            type="text"
            placeholder="Search providers..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="bg-slate-900 border border-slate-800 rounded-lg px-4 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 text-sm"
          />
          <select
            value={category}
            onChange={e => setCategory(e.target.value)}
            className="bg-slate-900 border border-slate-800 rounded-lg px-4 py-2 text-slate-100 focus:outline-none focus:border-emerald-500 text-sm"
          >
            <option value="">All Categories</option>
            <option value="HEALTHCARE">Healthcare</option>
            <option value="COWORKING">Coworking</option>
            <option value="CONSULTING">Consulting</option>
            <option value="EVENTS">Events</option>
          </select>
        </div>
      </div>

      {isLoading ? (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {[1, 2, 3].map(i => (
            <div key={i} className="h-48 bg-slate-900/50 border border-slate-800 rounded-xl animate-pulse" />
          ))}
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {providers.map(p => (
            <div key={p.id} className="bg-slate-900/50 border border-slate-800 p-6 rounded-xl hover:border-slate-700 transition-colors flex flex-col justify-between">
              <div>
                <span className="px-2 py-0.5 text-xs font-semibold text-emerald-400 bg-emerald-950/50 border border-emerald-900 rounded-full">
                  {p.category}
                </span>
                <h3 className="text-xl font-bold text-white mt-3">{p.name}</h3>
                <p className="text-slate-400 text-sm mt-2 line-clamp-3">{p.description || 'No description provided.'}</p>
                {p.location && <p className="text-slate-500 text-xs mt-3 flex items-center gap-1">📍 {p.location}</p>}
              </div>
              <Link to={`/book/${p.id}`} className="mt-6 w-full text-center bg-slate-800 hover:bg-slate-700 text-slate-100 font-semibold py-2.5 rounded-lg transition-colors block text-sm">
                View Available Slots
              </Link>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};
