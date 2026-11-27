import { StyleSheet } from 'react-native';

export const styles = StyleSheet.create({
  container: {
    flexGrow: 1,
    padding: 24,
    justifyContent: 'center',
    gap: 12,
  },
  title: {
    fontSize: 26,
    fontWeight: '700',
    marginBottom: 8,
  },
  body: {
    fontSize: 16,
    color: '#333',
  },
  hint: {
    fontSize: 13,
    color: '#666',
  },
  label: {
    fontSize: 14,
    fontWeight: '600',
    marginTop: 4,
  },
  input: {
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    paddingHorizontal: 12,
    paddingVertical: 10,
    fontSize: 16,
    backgroundColor: '#fff',
  },
  chipRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    gap: 8,
  },
  chip: {
    borderWidth: 1,
    borderColor: '#999',
    borderRadius: 16,
    paddingHorizontal: 12,
    paddingVertical: 6,
  },
  chipSelected: {
    backgroundColor: '#B0154B',
    borderColor: '#B0154B',
  },
  chipText: {
    color: '#222',
  },
  chipTextSelected: {
    color: '#fff',
    fontWeight: '600',
  },
  error: {
    color: '#B00020',
    fontSize: 14,
  },
  spacer: {
    height: 12,
  },
});
