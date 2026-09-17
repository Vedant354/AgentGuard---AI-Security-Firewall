/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      colors: {
        canvas: '#090d14',
        panel: '#111824',
        line: '#243043',
        ink: '#e6edf7',
        muted: '#93a4ba',
        signal: '#36d399',
        alert: '#f7b955',
        danger: '#ff6b6b',
      },
    },
  },
  plugins: [],
};
