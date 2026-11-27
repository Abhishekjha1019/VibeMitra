import React from 'react';
import { Button, Text, View } from 'react-native';

import { useSession } from '../session/SessionContext';
import { styles } from './styles';

export function HomeScreen() {
  const { account, signOut } = useSession();

  return (
    <View style={styles.container}>
      <Text style={styles.title}>Hi, {account?.displayName ?? 'there'}</Text>
      <Text style={styles.body}>City: {account?.city ?? '-'}</Text>
      <Text style={styles.body}>Discovery and matching arrive in the next milestone.</Text>
      <View style={styles.spacer} />
      <Button title="Sign out" onPress={signOut} />
    </View>
  );
}
