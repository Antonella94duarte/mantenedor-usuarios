import react from '@vitejs/plugin-react';
import { defineConfig, loadEnv } from 'vite';

// En desarrollo, /api se redirige al backend para evitar problemas de CORS.
export default defineConfig(({ mode }) => {
  const env = loadEnv(mode, '.', '');
  const proxy = {
    '/api': { target: env.VITE_PROXY_TARGET || 'http://localhost:8080', changeOrigin: true },
  };
  return {
    plugins: [react()],
    server: { port: 5173, proxy },
    preview: { port: 5173, proxy },
  };
});
