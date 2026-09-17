import { useCallback, useEffect, useMemo, useState } from 'react';
import {
  AlertTriangle,
  Ban,
  CheckCircle2,
  Clock,
  FileWarning,
  RefreshCw,
  ShieldAlert,
  ShieldCheck,
} from 'lucide-react';

import { agentguardApi } from '../api/agentguardApi';

const decisionStyles = {
  ALLOW: 'border-signal/35 bg-signal/10 text-signal',
  REVIEW: 'border-alert/35 bg-alert/10 text-alert',
  BLOCK: 'border-danger/35 bg-danger/10 text-danger',
};

const threatLabels = {
  PROMPT_INJECTION: 'Prompt Injection',
  JAILBREAK: 'Jailbreak',
  SYSTEM_PROMPT_EXTRACTION: 'System Prompt Extraction',
  SUSPICIOUS_INSTRUCTION: 'Suspicious Instruction',
  PII: 'PII',
  SECRET: 'Secret',
};

const emptyStats = {
  totalRequests: 0,
  allowedRequests: 0,
  reviewRequests: 0,
  blockedRequests: 0,
  totalThreats: 0,
  pendingReviews: 0,
  threatCounts: {},
  recentRequests: [],
};

function formatDate(value) {
  if (!value) return 'Not recorded';
  return new Intl.DateTimeFormat(undefined, {
    dateStyle: 'medium',
    timeStyle: 'short',
  }).format(new Date(value));
}

function formatThreatName(type) {
  return threatLabels[type] || type.replaceAll('_', ' ').toLowerCase().replace(/\b\w/g, (letter) => letter.toUpperCase());
}

function StatCard({ title, value, helper, icon: Icon, tone = 'default' }) {
  const toneClass = {
    default: 'bg-white/5 text-muted',
    signal: 'bg-signal/10 text-signal',
    alert: 'bg-alert/10 text-alert',
    danger: 'bg-danger/10 text-danger',
  }[tone];

  return (
    <section className="border border-line bg-panel p-5">
      <div className="flex items-start justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-[0.14em] text-muted">{title}</p>
          <p className="mt-3 text-3xl font-semibold text-ink">{value}</p>
        </div>
        <span className={`grid h-10 w-10 shrink-0 place-items-center ${toneClass}`}>
          <Icon size={19} />
        </span>
      </div>
      <p className="mt-4 text-sm text-muted">{helper}</p>
    </section>
  );
}

function LoadingState() {
  return (
    <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
      {Array.from({ length: 6 }).map((_, index) => (
        <div key={index} className="h-36 animate-pulse border border-line bg-panel/70" />
      ))}
    </div>
  );
}

function EmptyState() {
  return (
    <section className="border border-line bg-panel p-8 text-center">
      <div className="mx-auto grid h-12 w-12 place-items-center bg-white/5 text-muted">
        <ShieldCheck size={22} />
      </div>
      <h2 className="mt-4 text-lg font-semibold text-ink">No request activity yet</h2>
      <p className="mx-auto mt-2 max-w-xl text-sm text-muted">
        Send prompts through the Demo Agent or analysis API to populate this dashboard with live audit data.
      </p>
    </section>
  );
}

function ErrorState({ message, onRetry }) {
  return (
    <section className="border border-danger/40 bg-danger/10 p-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-danger">Dashboard data unavailable</p>
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

export function DashboardPage() {
  const [stats, setStats] = useState(emptyStats);
  const [status, setStatus] = useState('loading');
  const [error, setError] = useState('');

  const loadStats = useCallback(async () => {
    setStatus((currentStatus) => (currentStatus === 'success' ? 'refreshing' : 'loading'));
    setError('');

    try {
      const data = await agentguardApi.getDashboardStats();
      setStats({ ...emptyStats, ...data });
      setStatus('success');
    } catch (apiError) {
      setError(apiError.message || 'Unable to reach the backend API.');
      setStatus('error');
    }
  }, []);

  useEffect(() => {
    loadStats();
  }, [loadStats]);

  const threatRows = useMemo(() => {
    return Object.entries(stats.threatCounts || {})
      .map(([type, count]) => ({ type, count }))
      .sort((first, second) => second.count - first.count || first.type.localeCompare(second.type));
  }, [stats.threatCounts]);

  const hasData = stats.totalRequests > 0 || stats.totalThreats > 0 || stats.pendingReviews > 0;
  const maxThreatCount = Math.max(1, ...threatRows.map((row) => row.count));

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
        <div>
          <p className="text-xs font-medium uppercase tracking-[0.16em] text-muted">Overview</p>
          <h1 className="mt-2 text-2xl font-semibold text-ink">Security Dashboard</h1>
          <p className="mt-2 max-w-3xl text-sm text-muted">
            Live operational view of AgentGuard request decisions, detected threats, pending reviews, and recent audit activity.
          </p>
        </div>
        <button
          type="button"
          className="inline-flex h-10 items-center justify-center gap-2 border border-line px-4 text-sm font-medium text-muted hover:border-muted hover:text-ink disabled:cursor-not-allowed disabled:opacity-60"
          onClick={loadStats}
          disabled={status === 'loading' || status === 'refreshing'}
        >
          <RefreshCw size={16} className={status === 'refreshing' ? 'animate-spin' : ''} />
          Refresh
        </button>
      </div>

      {status === 'error' ? <ErrorState message={error} onRetry={loadStats} /> : null}
      {status === 'loading' ? <LoadingState /> : null}

      {status !== 'loading' && status !== 'error' && !hasData ? <EmptyState /> : null}

      {status !== 'loading' && status !== 'error' && hasData ? (
        <>
          <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">
            <StatCard title="Total Requests" value={stats.totalRequests} helper="All prompts inspected by AgentGuard." icon={ShieldAlert} />
            <StatCard title="Allowed" value={stats.allowedRequests} helper="Requests sent to the Demo Agent." icon={CheckCircle2} tone="signal" />
            <StatCard title="Review" value={stats.reviewRequests} helper="Requests awaiting analyst attention." icon={Clock} tone="alert" />
            <StatCard title="Blocked" value={stats.blockedRequests} helper="Requests stopped before agent contact." icon={Ban} tone="danger" />
            <StatCard title="Total Threats" value={stats.totalThreats} helper="Persisted category-safe findings." icon={FileWarning} tone="alert" />
            <StatCard title="Pending Reviews" value={stats.pendingReviews} helper="Human decisions still required." icon={AlertTriangle} tone="danger" />
          </div>

          <div className="grid gap-6 xl:grid-cols-[minmax(0,0.9fr)_minmax(0,1.1fr)]">
            <section className="border border-line bg-panel p-5">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h2 className="text-base font-semibold text-ink">Threat Distribution</h2>
                  <p className="mt-1 text-sm text-muted">Counts grouped by persisted threat type.</p>
                </div>
                <span className="text-sm font-semibold text-ink">{stats.totalThreats}</span>
              </div>
              <div className="mt-5 space-y-4">
                {threatRows.length === 0 ? (
                  <p className="border border-line bg-canvas p-4 text-sm text-muted">No threats have been detected yet.</p>
                ) : (
                  threatRows.map(({ type, count }) => (
                    <div key={type} className="space-y-2">
                      <div className="flex items-center justify-between gap-3 text-sm">
                        <span className="font-medium text-ink">{formatThreatName(type)}</span>
                        <span className="text-muted">{count}</span>
                      </div>
                      <div className="h-2 bg-canvas">
                        <div className="h-2 bg-alert" style={{ width: `${(count / maxThreatCount) * 100}%` }} />
                      </div>
                    </div>
                  ))
                )}
              </div>
            </section>

            <section className="border border-line bg-panel p-5">
              <div className="flex items-center justify-between gap-3">
                <div>
                  <h2 className="text-base font-semibold text-ink">Recent Requests</h2>
                  <p className="mt-1 text-sm text-muted">Latest decisions from the audit trail.</p>
                </div>
                <span className="text-sm text-muted">{stats.recentRequests?.length || 0} shown</span>
              </div>
              <div className="mt-5 overflow-x-auto">
                <table className="min-w-full border-collapse text-left text-sm">
                  <thead className="border-b border-line text-xs uppercase tracking-[0.12em] text-muted">
                    <tr>
                      <th className="pb-3 pr-4 font-medium">Request</th>
                      <th className="pb-3 pr-4 font-medium">Decision</th>
                      <th className="pb-3 pr-4 font-medium">Risk</th>
                      <th className="pb-3 pr-4 font-medium">LLM</th>
                      <th className="pb-3 font-medium">Created</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-line">
                    {(stats.recentRequests || []).map((request) => (
                      <tr key={request.requestId}>
                        <td className="py-3 pr-4 font-mono text-xs text-ink">{request.requestId}</td>
                        <td className="py-3 pr-4">
                          <span className={`inline-flex border px-2 py-1 text-xs font-semibold ${decisionStyles[request.decision] || 'border-line text-muted'}`}>
                            {request.decision}
                          </span>
                        </td>
                        <td className="py-3 pr-4 text-ink">{request.riskScore}</td>
                        <td className="py-3 pr-4 text-muted">{request.llmContacted ? 'YES' : 'NO'}</td>
                        <td className="py-3 text-muted">{formatDate(request.createdAt)}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </section>
          </div>
        </>
      ) : null}
    </div>
  );
}
