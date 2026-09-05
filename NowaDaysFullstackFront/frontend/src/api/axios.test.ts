import { describe, it, expect, beforeEach } from 'vitest';
import MockAdapter from 'axios-mock-adapter';
import api from './axios';

describe('axios api instance', () => {
  let mock: MockAdapter;

  beforeEach(() => {

    mock = new MockAdapter(api);
   
    localStorage.clear();
  });

  it('should add Authorization header when token exists in localStorage', async () => {
  
    const fakeToken = 'test-token-123';
    localStorage.setItem('token', fakeToken);

    mock.onGet('/test').reply(200);

   
    const response = await api.get('/test');

  
    expect(response.config.headers.Authorization).toBe(`Bearer ${fakeToken}`);
  });

  it('should not add Authorization header when token does not exist in localStorage', async () => {
   
    mock.onGet('/test').reply(200);


    const response = await api.get('/test');

    
    expect(response.config.headers.Authorization).toBeUndefined();
  });

  it('should reject the request when backend returns an error (e.g., 401)', async () => {
   
    mock.onGet('/test').reply(401);

  
    await expect(api.get('/test')).rejects.toThrow();
  });
});