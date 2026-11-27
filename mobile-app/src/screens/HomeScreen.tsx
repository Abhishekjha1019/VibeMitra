import React, { useState } from 'react';
import { Button, Text, TextInput, View } from 'react-native';

import { ApiError } from '../api/client';
import { useSession } from '../session/SessionContext';
import { styles } from './styles';

export function HomeScreen() {
  const { account, signOut, deleteAccount } = useSession();
  const [confirming, setConfirming] = useState(false);
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [busy, setBusy] = useState(false);

  async function confirmDelete() {
    setError(null);
    setBusy(true);
    try {
      // On success the session is cleared and the app returns to the sign-in screen.
      await deleteAccount(password);
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'Could not delete the account. Try again.');
      setBusy(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Hi, {account?.displayName ?? 'there'}</Text>
      <Text style={styles.body}>City: {account?.city ?? '-'}</Text>
      <Text style={styles.body}>Discovery and matching arrive in the next milestone.</Text>
      <View style={styles.spacer} />
      <Button title="Sign out" onPress={signOut} />
      <View style={styles.spacer} />

      {!confirming ? (
        <Button title="Delete my account" color="#B00020" onPress={() => setConfirming(true)} />
      ) : (
        <View>
          <Text style={styles.body}>
            This permanently deletes your account and personal data. It cannot be undone. Enter your password to confirm.
          </Text>
          <TextInput
            value={password}
            onChangeText={setPassword}
            secureTextEntry
            placeholder="Password"
            autoCapitalize="none"
            style={{ borderWidth: 1, borderColor: '#ccc', borderRadius: 8, padding: 12, marginVertical: 8 }}
          />
          {error ? <Text style={{ color: '#B00020' }}>{error}</Text> : null}
          <Button
            title={busy ? 'Deleting…' : 'Permanently delete account'}
            color="#B00020"
            disabled={busy || password.length === 0}
            onPress={confirmDelete}
          />
          <View style={styles.spacer} />
          <Button
            title="Cancel"
            disabled={busy}
            onPress={() => {
              setConfirming(false);
              setPassword('');
              setError(null);
            }}
          />
        </View>
      )}
    </View>
  );
}
