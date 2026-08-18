import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter } from 'react-router-dom'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { Toaster } from 'sonner'
import App from './App'
import './index.css'

/**
 * React Query client configuration.
 *
 * Design Decision:
 * - staleTime: 30s — avoids refetching fresh data on every component mount
 * - gcTime: 5min — keeps cached data in memory for background invalidation
 * - retry: 1 — retries once on failure; prevents request storms on outages
 * - refetchOnWindowFocus: true — ensures slot availability is fresh when
 *   users return to the tab (critical for concurrent booking scenarios)
 */
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30_000,
      gcTime: 5 * 60_000,
      retry: 1,
      refetchOnWindowFocus: true,
    },
    mutations: {
      retry: 0,
    },
  },
})

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <QueryClientProvider client={queryClient}>
      <App />
        {/* Toast notification system — positioned top-right, dark theme */}
        <Toaster
          position="top-right"
          theme="dark"
          richColors
          closeButton
          toastOptions={{
            duration: 4000,
            style: {
              background: 'hsl(220, 19%, 13%)',
              border: '1px solid rgba(255,255,255,0.08)',
              color: 'hsl(220, 16%, 96%)',
            },
          }}
        />
      {/* React Query Devtools — only in development */}
      {import.meta.env.DEV && <ReactQueryDevtools initialIsOpen={false} />}
    </QueryClientProvider>
  </React.StrictMode>,
)
