export function getBaseUrl() {
   const env = import.meta.env.VITE_APP_ENV || import.meta.env.MODE
   return env === 'production' || env === 'staging' ? import.meta.env.VITE_APP_BASE_URL : import.meta.env.VITE_APP_BASE_API
}
