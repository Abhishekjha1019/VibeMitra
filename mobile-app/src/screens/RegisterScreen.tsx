import type { NativeStackScreenProps } from '@react-navigation/native-stack';
import React, { useState } from 'react';
import { Button, ScrollView, Text, TextInput, TouchableOpacity, View } from 'react-native';

import { authApi, CITIES, City } from '../api/auth';
import { ApiError } from '../api/client';
import { useSession } from '../session/SessionContext';
import type { RootStackParamList } from '../navigation/RootNavigator';
import { styles } from './styles';

type Props = NativeStackScreenProps<RootStackParamList, 'Register'>;

const DATE_PATTERN = /^\d{4}-\d{2}-\d{2}$/;

export function RegisterScreen(_props: Props) {
  const { signIn } = useSession();
  const [displayName, setDisplayName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [dateOfBirth, setDateOfBirth] = useState(''); // YYYY-MM-DD
  const [city, setCity] = useState<City>('DELHI');
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState(false);

  async function onSubmit() {
    setError(null);
    if (!DATE_PATTERN.test(dateOfBirth)) {
      setError('Date of birth must be in the format YYYY-MM-DD');
      return;
    }
    setSubmitting(true);
    try {
      const response = await authApi.register({
        displayName: displayName.trim(),
        email: email.trim(),
        password,
        dateOfBirth,
        city,
      });
      await signIn(response.accessToken);
    } catch (e) {
      if (e instanceof ApiError && e.fields) {
        setError(Object.values(e.fields).join('\n'));
      } else {
        setError(e instanceof ApiError ? e.message : 'Something went wrong. Please try again.');
      }
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <ScrollView contentContainerStyle={styles.container}>
      <TextInput style={styles.input} placeholder="Display name" value={displayName} onChangeText={setDisplayName} />
      <TextInput
        style={styles.input}
        placeholder="Email"
        autoCapitalize="none"
        keyboardType="email-address"
        value={email}
        onChangeText={setEmail}
      />
      <TextInput
        style={styles.input}
        placeholder="Password (8 or more characters)"
        secureTextEntry
        value={password}
        onChangeText={setPassword}
      />
      <TextInput
        style={styles.input}
        placeholder="Date of birth (YYYY-MM-DD)"
        keyboardType="numbers-and-punctuation"
        value={dateOfBirth}
        onChangeText={setDateOfBirth}
      />

      <Text style={styles.label}>City (launch region: Delhi NCR)</Text>
      <View style={styles.chipRow}>
        {CITIES.map((option) => {
          const selected = option.value === city;
          return (
            <TouchableOpacity
              key={option.value}
              onPress={() => setCity(option.value)}
              style={[styles.chip, selected && styles.chipSelected]}
            >
              <Text style={selected ? styles.chipTextSelected : styles.chipText}>{option.label}</Text>
            </TouchableOpacity>
          );
        })}
      </View>

      <Text style={styles.hint}>
        VibeMitra matchmaking is for adults aged 18 and over. By creating an account you confirm this.
      </Text>

      {error ? <Text style={styles.error}>{error}</Text> : null}

      <Button title={submitting ? 'Creating account…' : 'Create account'} onPress={onSubmit} disabled={submitting} />
    </ScrollView>
  );
}
