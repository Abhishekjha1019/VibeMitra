import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { Button, Text, TextInput, View } from 'react-native';

import { authApi } from '../api/auth';
import { ApiError } from '../api/client';
import { useSession } from '../session/SessionContext';
import type { RootStackParamList } from '../navigation/RootNavigator';
import { styles } from './styles';

type Props = NativeStackScreenProps<RootStackParamList, 'Login'>;

export function LoginScreen({ navigation }: Props) {
  const { signIn } = useSession();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit() {
    setError(null);
    setSubmitting(true);
    try {
      const response = await authApi.login(email.trim(), password);
      await signIn(response.accessToken);
    } catch (e) {
      setError(e instanceof ApiError ? e.message : 'Something went wrong. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Welcome back</Text>

      <TextInput
        style={styles.input}
        placeholder="Email"
        autoCapitalize="none"
        autoComplete="email"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />
      <TextInput
        style={styles.input}
        placeholder="Password"
        secureTextEntry
        autoComplete="password"
        value={password}
        onChangeText={setPassword}
      />

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <Button title={submitting ? 'Signing in…' : 'Sign in'} onPress={onSubmit} disabled={submitting} />
      <View style={styles.spacer} />
      <Button title="Create an account" onPress={() => navigation.navigate('Register')} />
    </View>
  );
}
