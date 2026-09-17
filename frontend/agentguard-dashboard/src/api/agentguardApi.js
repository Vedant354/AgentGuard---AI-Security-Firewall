import { apiRequest } from './client';

export const agentguardApi = {
  getHealth: () => apiRequest('/health'),
  getDashboardStats: () => apiRequest('/dashboard/stats'),
  getRequests: ({ decision, limit } = {}) => {
    const search = new URLSearchParams();
    if (decision) search.set('decision', decision);
    if (limit) search.set('limit', String(limit));
    const query = search.toString();
    return apiRequest(`/requests${query ? `?${query}` : ''}`);
  },
  getRequest: (requestId) => apiRequest(`/requests/${encodeURIComponent(requestId)}`),
  getThreatSummary: () => apiRequest('/threats/summary'),
  getReviews: () => apiRequest('/reviews'),
  decideReview: (requestId, decision) => apiRequest(`/reviews/${encodeURIComponent(requestId)}/decision`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(decision),
  }),
  chatWithAgent: (prompt) => apiRequest('/agent/chat', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ prompt }),
  }),
};
