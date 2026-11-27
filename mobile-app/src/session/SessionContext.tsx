import * as SecureStore from 'expo-secure-store';
import React, { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react';

import { authApi, Account } from '../api/auth';
import { ApiError } from '../api/client';

const TOKEN_KEY = 'vibemitra.accessToken';

type SessionState = {
  /** True while the stored token is being checked on app start. */
  loading: boolean;
  token: string | null;
  account: Account | null;
  signIn: (token: string) => Promise<void>;
  signOut: () => Promise<void>;
  /** Deletes the account on the server (password required), then clears the local session. */
  deleteAccount: (password: string) => Promise<void>;
};

const SessionContext = createContext<SessionState | undefined>(undefined);

/**
 * Keeps the JWT in the device keystore (expo-secure-store), never in AsyncStorage or plain files.
 * On start-up the stored token is validated by calling /users/me; an invalid token is discarded.
 */
export function SessionProvider({ children }: { children: React.ReactNode }) {
  const [loading, setLoading] = useState(true);
  const [token, setToken] = useState<string | null>(null);
  const [account, setAccount] = useState<Account | null>(null);

  const clear = useCallback(async () => {
    await SecureStore.deleteItemAsync(TOKEN_KEY);
    setToken(null);
    setAccount(null);
  }, []);

  const signIn = useCallback(async (newToken: string) => {
    await SecureStore.setItemAsync(TOKEN_KEY, newToken);
    const me = await authApi.me(newToken);
    setToken(newToken);
    setAccount(me);
  }, []);

  useEffect(() => {
    (async () => {
      try {
        const stored = await SecureStore.getItemAsync(TOKEN_KEY);
        if (stored) {
          const me = await authApi.me(stored);
          setToken(stored);
          setAccount(me);
        }
      } catch (error) {
        // Expired or revoked token: forget it. Network errors keep the user signed out for now.
        if (error instanceof ApiError && error.status === 401) {
          await clear();
        }
      } finally {
        setLoading(false);
      }
    })();
  }, [clear]);

  const deleteAccount = useCallback(
    async (password: string) => {
      if (!token) {
        throw new Error('Not signed in');
      }
      await authApi.deleteAccount(token, password);
      await clear();
    },
    [token, clear],
  );

  const value = useMemo<SessionState>(
    () => ({ loading, token, account, signIn, signOut: clear, deleteAccount }),
    [loading, token, account, signIn, clear, deleteAccount],
  );

  return <SessionContext.Provider value={value}>{children}</SessionContext.Provider>;
}

export function useSession(): SessionState {
  const context = useContext(SessionContext);
  if (!context) {
    throw new Error('useSession must be used inside SessionProvider');
  }
  return context;
}
