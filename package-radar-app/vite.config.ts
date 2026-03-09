import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/bend': {
        target: 'http://localhost:8080',  // Spring Boot backend
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/bend/, ''),  // REMOVE /bend before proxying
      },
    },
  },
});