/** @type {import('tailwindcss').Config} */
export default {
  // Dark mode via class strategy — the 'dark' class on <html> controls dark mode.
  // This allows users to toggle dark mode independently of system preference.
  darkMode: ['class'],
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}',
  ],
  theme: {
    extend: {
      // ── Design tokens ────────────────────────────────────────────────────
      colors: {
        // Semantic brand palette using HSL for easy theming
        brand: {
          50:  'hsl(225, 100%, 97%)',
          100: 'hsl(224, 95%, 93%)',
          200: 'hsl(225, 94%, 87%)',
          300: 'hsl(226, 93%, 78%)',
          400: 'hsl(228, 90%, 68%)',
          500: 'hsl(230, 85%, 60%)',  // Primary brand blue
          600: 'hsl(233, 80%, 52%)',
          700: 'hsl(235, 75%, 44%)',
          800: 'hsl(237, 70%, 36%)',
          900: 'hsl(239, 65%, 28%)',
          950: 'hsl(241, 75%, 14%)',
        },
        // Accent / highlight color
        accent: {
          50:  'hsl(270, 100%, 98%)',
          400: 'hsl(270, 90%, 70%)',
          500: 'hsl(270, 85%, 60%)',
          600: 'hsl(271, 80%, 51%)',
        },
        // Success green
        success: {
          50:  'hsl(142, 76%, 96%)',
          500: 'hsl(142, 71%, 45%)',
          600: 'hsl(142, 76%, 36%)',
        },
        // Warning amber
        warning: {
          50:  'hsl(38, 100%, 97%)',
          500: 'hsl(38, 92%, 50%)',
          600: 'hsl(32, 95%, 44%)',
        },
        // Danger red
        danger: {
          50:  'hsl(0, 100%, 97%)',
          500: 'hsl(0, 84%, 60%)',
          600: 'hsl(0, 72%, 51%)',
        },
        // Dark mode surface palette
        dark: {
          50:  'hsl(220, 16%, 96%)',
          100: 'hsl(220, 15%, 20%)',
          200: 'hsl(220, 17%, 17%)',
          300: 'hsl(220, 19%, 13%)',
          400: 'hsl(220, 21%, 10%)',
          500: 'hsl(220, 24%, 7%)',
        },
      },
      // ── Typography ───────────────────────────────────────────────────────
      fontFamily: {
        sans: ['Inter', 'system-ui', 'sans-serif'],
        display: ['Inter', 'system-ui', 'sans-serif'],
        mono: ['JetBrains Mono', 'Fira Code', 'monospace'],
      },
      // ── Border radius ────────────────────────────────────────────────────
      borderRadius: {
        xl:  '0.875rem',
        '2xl': '1rem',
        '3xl': '1.5rem',
      },
      // ── Box shadows ──────────────────────────────────────────────────────
      boxShadow: {
        glass: '0 4px 30px rgba(0, 0, 0, 0.1)',
        'glass-lg': '0 8px 60px rgba(0, 0, 0, 0.15)',
        glow: '0 0 20px rgba(99, 102, 241, 0.3)',
        'glow-lg': '0 0 40px rgba(99, 102, 241, 0.4)',
        card: '0 1px 3px 0 rgba(0,0,0,0.1), 0 1px 2px -1px rgba(0,0,0,0.1)',
        'card-hover': '0 10px 15px -3px rgba(0,0,0,0.1), 0 4px 6px -4px rgba(0,0,0,0.1)',
      },
      // ── Backdrop blur ────────────────────────────────────────────────────
      backdropBlur: {
        xs: '2px',
      },
      // ── Animations ───────────────────────────────────────────────────────
      animation: {
        'fade-in': 'fadeIn 0.3s ease-in-out',
        'slide-up': 'slideUp 0.4s ease-out',
        'slide-down': 'slideDown 0.4s ease-out',
        'scale-in': 'scaleIn 0.2s ease-out',
        shimmer: 'shimmer 2s linear infinite',
        pulse: 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'spin-slow': 'spin 3s linear infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        slideUp: {
          '0%': { opacity: '0', transform: 'translateY(10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideDown: {
          '0%': { opacity: '0', transform: 'translateY(-10px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        scaleIn: {
          '0%': { opacity: '0', transform: 'scale(0.95)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
      },
    },
  },
  plugins: [],
}
