/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        'baidu-blue': '#4E6EF2',
        'baidu-red': '#FD5A5A',
        'baidu-green': '#4CAF50',
      },
      fontFamily: {
        'baidu': ['Arial', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
