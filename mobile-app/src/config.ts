const DEFAULT_API_URL = 'http://10.0.2.2:8080';

/** Base URL of the VibeMitra backend, set via EXPO_PUBLIC_API_URL (see .env.example). */
export const API_BASE_URL: string = (process.env.EXPO_PUBLIC_API_URL ?? DEFAULT_API_URL).replace(/\/+$/, '');
