import { RotateCw } from 'lucide-react';

const statusStyles = {
  checking: 'border-alert/40 bg-alert/10 text-alert',
  online: 'border-signal/40 bg-signal/10 text-signal',
  offline: 'border-danger/40 bg-danger/10 text-danger',
};

export function Header({ backend }) {
  const label = backend.status === 'online' ? 'Backend online' : backend.status === 'offline' ? 'Backend offline' : 'Checking backend';

  return (
    <header className="flex min-h-16 items-center justify-between border-b border-line bg-panel/70 px-4 sm:px-6 lg:px-8">
      <div>
        <p className="text-xs font-medium uppercase tracking-[0.16em] text-muted">Security operations</p>
        <h1 className="text-sm font-semibold text-ink">AgentGuard Control Plane</h1>
      </div>
      <div className="flex items-center gap-2">
        <span className={`inline-flex items-center gap-2 border px-3 py-1.5 text-xs font-medium ${statusStyles[backend.status]}`}>
          <span className="h-1.5 w-1.5 rounded-full bg-current" />
          {label}
        </span>
        <button
          type="button"
          className="inline-flex h-8 w-8 items-center justify-center border border-line text-muted hover:border-muted hover:text-ink"
          onClick={backend.checkHealth}
          aria-label="Refresh backend connection"
          title="Refresh backend connection"
        >
          <RotateCw size={15} />
        </button>
      </div>
    </header>
  );
}
