import { Navigate, Route, Routes } from 'react-router-dom';

import { AppShell } from './components/AppShell';
import { DashboardPage } from './pages/DashboardPage';
import { DemoAgentPage } from './pages/DemoAgentPage';
import { RequestsPage } from './pages/RequestsPage';
import { ReviewQueuePage } from './pages/ReviewQueuePage';
import { ThreatsPage } from './pages/ThreatsPage';

export default function App() {
  return (
    <AppShell>
      <Routes>
        <Route path="/dashboard" element={<DashboardPage />} />
        <Route path="/demo-agent" element={<DemoAgentPage />} />
        <Route path="/reviews" element={<ReviewQueuePage />} />
        <Route path="/requests" element={<RequestsPage />} />
        <Route path="/threats" element={<ThreatsPage />} />
        <Route path="*" element={<Navigate to="/dashboard" replace />} />
      </Routes>
    </AppShell>
  );
}
