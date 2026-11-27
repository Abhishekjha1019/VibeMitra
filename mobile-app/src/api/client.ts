import { API_BASE_URL } from '../config';

export class ApiError extends Error {
  readonly status: number;
  readonly fields?: Record<string, string>;

  constructor(status: number, message: string, fields?: Record<string, string>) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.fields = fields;
  }
}

type RequestOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE';
  body?: unknown;
  token?: string | null;
};

/** Minimal JSON client. Throws ApiError with the server's message for non-2xx responses. */
export async function apiRequest<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers: Record<string, string> = { Accept: 'application/json' };
  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json';
  }
  if (options.token) {
    headers.Authorization = `Bearer ${options.token}`;
  }

  let response: Response;
  try {
    response = await fetch(`${API_BASE_URL}${path}`, {
      method: options.method ?? 'GET',
      headers,
      body: options.body !== undefined ? JSON.stringify(options.body) : undefined,
    });
  } catch {
    throw new ApiError(0, 'Cannot reach the VibeMitra server. Check your connection.');
  }

  const text = await response.text();
  const data = text ? safeParse(text) : null;

  if (!response.ok) {
    const { message, fields } = readErrorBody(data);
    throw new ApiError(response.status, message ?? `Request failed (${response.status})`, fields);
  }

  return data as T;
}

function safeParse(text: string): unknown {
  try {
    return JSON.parse(text);
  } catch {
    return null;
  }
}

function readErrorBody(data: unknown): { message?: string; fields?: Record<string, string> } {
  if (!data || typeof data !== 'object') {
    return {};
  }
  const body = data as { message?: unknown; fields?: unknown };
  return {
    message: typeof body.message === 'string' ? body.message : undefined,
    fields: body.fields && typeof body.fields === 'object' ? (body.fields as Record<string, string>) : undefined,
  };
}
