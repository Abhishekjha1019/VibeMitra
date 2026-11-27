import { StatusBar } from 'expo-status-bar';
import React from 'react';
import { SafeAreaProvider } from 'react-native-safe-area-context';

import { RootNavigator } from './src/navigation/RootNavigator';
import { SessionProvider } from './src/session/SessionContext';

export default function App() {
  return (
    <SafeAreaProvider>
      <SessionProvider>
        <RootNavigator />
      </SessionProvider>
      <StatusBar style="dark" />
    </SafeAreaProvider>
  );
}
