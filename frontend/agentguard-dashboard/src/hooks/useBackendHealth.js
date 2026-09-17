import { useCallback, useEffect, useState } from 'react';

import { agentguardApi } from '../api/agentguardApi';

export function useBackendHealth() {
  const [status, setStatus] = useState('checking');

  const checkHealth = useCallback(async () => {
    setStatus('checking');
    try {
      await agentguardApi.getHealth();
      setStatus('online');
    } catch {
      setStatus('offline');
    }
  }, []);

  useEffect(() => {
    checkHealth();
  }, [checkHealth]);

  return { status, checkHealth };
}
