import { apiRequest } from './client';

export type City = 'DELHI' | 'NOIDA' | 'GHAZIABAD' | 'GURUGRAM' | 'FARIDABAD';

export const CITIES: { value: City; label: string }[] = [
  { value: 'DELHI', label: 'Delhi' },
  { value: 'NOIDA', label: 'Noida' },
  { value: 'GHAZIABAD', label: 'Ghaziabad' },
  { value: 'GURUGRAM', label: 'Gurugram' },
  { value: 'FARIDABAD', label: 'Faridabad' },
];

export type AuthResponse = {
  accessToken: string;
  userId: string;
};

export type RegisterInput = {
  email: string;
  password: string;
  displayName: string;
  dateOfBirth: string; // YYYY-MM-DD
  city: City;
};

export type Account = {
  id: string;
  email: string;
  displayName: string;
  dateOfBirth: string;
  city: City;
  status: string;
  createdAt: string;
};

export const authApi = {
  register: (input: RegisterInput) =>
    apiRequest<AuthResponse>('/api/v1/auth/register', { method: 'POST', body: input }),

  login: (email: string, password: string) =>
    apiRequest<AuthResponse>('/api/v1/auth/login', { method: 'POST', body: { email, password } }),

  me: (token: string) => apiRequest<Account>('/api/v1/users/me', { token }),

  /** Permanently deletes the signed-in account. The password is re-checked by the server. */
  deleteAccount: (token: string, password: string) =>
    apiRequest<void>('/api/v1/users/me', { method: 'DELETE', token, body: { password } }),
};
