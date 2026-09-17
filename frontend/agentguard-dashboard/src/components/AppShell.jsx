import { useState } from 'react';
import { Menu } from 'lucide-react';

import { Header } from './Header';
import { Sidebar } from './Sidebar';
import { useBackendHealth } from '../hooks/useBackendHealth';

export function AppShell({ children }) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const backend = useBackendHealth();

  return (
    <div className="min-h-screen bg-canvas text-ink">
      <Sidebar open={sidebarOpen} onClose={() => setSidebarOpen(false)} />
      <div className="lg:pl-64">
        <Header backend={backend} />
        <main className="px-4 py-6 sm:px-6 lg:px-8">
          <button
            type="button"
            className="mb-5 inline-flex h-10 w-10 items-center justify-center border border-line bg-panel text-muted lg:hidden"
            onClick={() => setSidebarOpen(true)}
            aria-label="Open navigation"
            title="Open navigation"
          >
            <Menu size={20} />
          </button>
          {children}
        </main>
      </div>
    </div>
  );
}
