import { useCallback, useEffect, useMemo, useState } from 'react';
import { AlertTriangle, Fingerprint, KeyRound, RefreshCw, ShieldAlert, ShieldCheck, Siren, TerminalSquare } from 'lucide-react';

import { agentguardApi } from '../api/agentguardApi';

const threatMetadata = {
  PROMPT_INJECTION: {
    label: 'Prompt Injection',
    description: 'Instruction override or context manipulation attempts.',
    icon: ShieldAlert,
    tone: 'danger',
  },
  JAILBREAK: {
    label: 'Jailbreak',
    description: 'Attempts to bypass safety or activate unrestricted behavior.',
    icon: Siren,
    tone: 'danger',
  },
  SYSTEM_PROMPT_EXTRACTION: {
    label: 'System Prompt Extraction',
    description: 'Requests to expose hidden, internal, or system instructions.',
    icon: TerminalSquare,
    tone: 'alert',
  },
  SUSPICIOUS_INSTRUCTION: {
    label: 'Suspicious Instruction',
    description: 'Authority, priority, or persistence manipulation patterns.',
    icon: AlertTriangle,
    tone: 'alert',
  },
  PII: {
    label: 'PII',
    description: 'Personally identifiable information detected by category.',
    icon: Fingerprint,
    tone: 'alert',
  },
  SECRET: {
    label: 'Secret',
    description: 'Credential, token, API-key, or private-key exposure patterns.',
    icon: KeyRound,
    tone: 'danger',
  },
};

const emptySummary = {
  threatCounts: {},
};

function toneClasses(tone) {
  return {
    alert: 'bg-alert/10 text-alert',
    danger: 'bg-danger/10 text-danger',
    signal: 'bg-signal/10 text-signal',
  }[tone] || 'bg-white/5 text-muted';
}

function LoadingState() {
  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {Array.from({ length: 6 }).map((_, index) => (
        <div key={index} className="h-44 animate-pulse border border-line bg-panel/70" />
      ))}
    </div>
  );
}

function ErrorState({ message, onRetry }) {
  return (
    <section className="border border-danger/40 bg-danger/10 p-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-danger">Threat summary unavailable</p>
          <p className="mt-1 text-sm text-muted">{message}</p>
        </div>
        <button
          type="button"
          className="inline-flex h-10 items-center justify-center gap-2 border border-danger/50 px-4 text-sm font-medium text-danger hover:bg-danger/10"
          onClick={onRetry}
        >
          <RefreshCw size={16} />
          Retry
        </button>
      </div>
    </section>
  );
}

function EmptyState() {
  return (
    <section className="border border-line bg-panel p-8 text-center">
      <div className="mx-auto grid h-12 w-12 place-items-center bg-signal/10 text-signal">
        <ShieldCheck size={22} />
      </div>
      <h2 className="mt-4 text-lg font-semibold text-ink">No persisted threats yet</h2>
      <p className="mx-auto mt-2 max-w-xl text-sm text-muted">
        Threat categories are ready, but the current audit database does not contain any threat findings.
      </p>
    </section>
  );
}

function ThreatCard({ threat, maxCount }) {
  const Icon = threat.icon;
  const width = maxCount > 0 ? (threat.count / maxCount) * 100 : 0;

  return (
    <section className="border border-line bg-panel p-5">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-sm font-semibold text-ink">{threat.label}</p>
          <p className="mt-2 text-sm text-muted">{threat.description}</p>
        </div>
        <span className={`grid h-10 w-10 shrink-0 place-items-center ${toneClasses(threat.tone)}`}>
          <Icon size={19} />
        </span>
      </div>
      <div className="mt-5 flex items-end justify-between gap-4">
        <div>
          <p className="text-xs uppercase tracking-[0.12em] text-muted">Count</p>
          <p className="mt-1 text-3xl font-semibold text-ink">{threat.count}</p>
        </div>
        <p className="text-xs text-muted">{threat.type}</p>
      </div>
      <div className="mt-4 h-2 bg-canvas">
        <div className={`h-2 ${threat.count > 0 ? 'bg-alert' : 'bg-white/10'}`} style={{ width: `${width}%` }} />
      </div>
    </section>
  );
}

export function ThreatsPage() {
  const [summary, setSummary] = useState(emptySummary);
  const [status, setStatus] = useState('loading');
  const [error, setError] = useState('');

  const loadThreatSummary = useCallback(async () => {
    setStatus((currentStatus) => (currentStatus === 'success' ? 'refreshing' : 'loading'));
    setError('');

    try {
      const data = await agentguardApi.getThreatSummary();
      setSummary({ ...emptySummary, ...data });
      setStatus('success');
    } catch (apiError) {
      setError(apiError.message || 'Unable to reach the backend API.');
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadThreatSummary();
  }, [loadThreatSummary]);

  const threats = useMemo(() => {
    const counts = summary.threatCounts || {};
    return Object.keys(threatMetadata).map((type) => ({
      type,
      count: counts[type] || 0,
      ...threatMetadata[type],
    }));
  }, [summary.threatCounts]);

  const totalThreats = threats.reduce((total, threat) => total + threat.count, 0);
  const activeThreatTypes = threats.filter((threat) => threat.count > 0).length;
  const maxCount = Math.max(1, ...threats.map((threat) => threat.count));

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs font-medium uppercase tracking-[0.16em] text-muted">Detection</p>
          <h1 className="mt-2 text-2xl font-semibold text-ink">Threats</h1>
          <p className="mt-2 max-w-3xl text-sm text-muted">
            Aggregate view of persisted AgentGuard security findings grouped by supported threat category.
          </p>
        </div>
        <button
          type="button"
          className="inline-flex h-10 items-center justify-center gap-2 border border-line px-4 text-sm font-medium text-muted hover:border-muted hover:text-ink disabled:cursor-not-allowed disabled:opacity-60"
          onClick={loadThreatSummary}
          disabled={status === 'loading' || status === 'refreshing'}
        >
          <RefreshCw size={16} className={status === 'refreshing' ? 'animate-spin' : ''} />
          Refresh
        </button>
      </div>

      {status === 'error' ? <ErrorState message={error} onRetry={loadThreatSummary} /> : null}
      {status === 'loading' ? <LoadingState /> : null}

      {status !== 'loading' && status !== 'error' ? (
        <>
          <div className="grid gap-4 md:grid-cols-2">
            <section className="border border-line bg-panel p-5">
              <p className="text-xs font-medium uppercase tracking-[0.14em] text-muted">Total Findings</p>
              <p className="mt-3 text-3xl font-semibold text-ink">{totalThreats}</p>
              <p className="mt-3 text-sm text-muted">Persisted threat records across all categories.</p>
            </section>
            <section className="border border-line bg-panel p-5">
              <p className="text-xs font-medium uppercase tracking-[0.14em] text-muted">Active Categories</p>
              <p className="mt-3 text-3xl font-semibold text-ink">{activeThreatTypes}</p>
              <p className="mt-3 text-sm text-muted">Threat types with at least one persisted finding.</p>
            </section>
          </div>

          {totalThreats === 0 ? <EmptyState /> : null}

          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            {threats.map((threat) => (
              <ThreatCard key={threat.type} threat={threat} maxCount={maxCount} />
            ))}
          </div>

          <section className="border border-line bg-panel p-5">
            <div className="flex flex-col gap-2 border-b border-line pb-4 sm:flex-row sm:items-center sm:justify-between">
              <div>
                <h2 className="text-base font-semibold text-ink">Threat Counts</h2>
                <p className="mt-1 text-sm text-muted">Exact aggregate values returned by the backend summary endpoint.</p>
              </div>
              <span className="text-sm text-muted">{threats.length} categories</span>
            </div>
            <div className="mt-5 overflow-x-auto">
              <table className="min-w-full border-collapse text-left text-sm">
                <thead className="border-b border-line text-xs uppercase tracking-[0.12em] text-muted">
                  <tr>
                    <th className="pb-3 pr-4 font-medium">Threat Type</th>
                    <th className="pb-3 pr-4 font-medium">Category</th>
                    <th className="pb-3 font-medium">Count</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-line">
                  {threats.map((threat) => (
                    <tr key={threat.type}>
                      <td className="py-3 pr-4 font-mono text-xs text-ink">{threat.type}</td>
                      <td className="py-3 pr-4 text-muted">{threat.label}</td>
                      <td className={`py-3 font-semibold ${threat.count > 0 ? 'text-alert' : 'text-muted'}`}>{threat.count}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </section>
        </>
      ) : null}
    </div>
  );
}
