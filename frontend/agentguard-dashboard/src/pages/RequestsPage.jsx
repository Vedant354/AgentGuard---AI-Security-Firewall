import { useCallback, useEffect, useMemo, useState } from 'react';
import { AlertTriangle, CheckCircle2, Clock, Eye, RefreshCw, SearchX, ShieldAlert, XCircle } from 'lucide-react';

import { agentguardApi } from '../api/agentguardApi';

const REQUEST_LIMIT = 50;
const decisionOptions = ['ALL', 'ALLOW', 'REVIEW', 'BLOCK'];

const decisionStyles = {
  ALLOW: 'border-signal/35 bg-signal/10 text-signal',
  REVIEW: 'border-alert/35 bg-alert/10 text-alert',
  BLOCK: 'border-danger/35 bg-danger/10 text-danger',
};

const severityStyles = {
  LOW: 'border-muted/30 bg-white/5 text-muted',
  MEDIUM: 'border-alert/35 bg-alert/10 text-alert',
  HIGH: 'border-danger/35 bg-danger/10 text-danger',
  CRITICAL: 'border-danger/50 bg-danger/20 text-danger',
};

const threatLabels = {
  PROMPT_INJECTION: 'Prompt Injection',
  JAILBREAK: 'Jailbreak',
  SYSTEM_PROMPT_EXTRACTION: 'System Prompt Extraction',
  SUSPICIOUS_INSTRUCTION: 'Suspicious Instruction',
  PII: 'PII',
  SECRET: 'Secret',
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

function riskTone(score) {
  if (score >= 71) return 'text-danger';
  if (score >= 31) return 'text-alert';
  return 'text-signal';
}

function DecisionBadge({ decision }) {
  return (
    <span className={`inline-flex border px-2 py-1 text-xs font-semibold ${decisionStyles[decision] || 'border-line text-muted'}`}>
      {decision || 'UNKNOWN'}
    </span>
  );
}

function LoadingState() {
  return (
    <div className="space-y-3">
      {Array.from({ length: 5 }).map((_, index) => (
        <div key={index} className="h-16 animate-pulse border border-line bg-panel/70" />
      ))}
    </div>
  );
}

function ErrorState({ title, message, onRetry }) {
  return (
    <section className="border border-danger/40 bg-danger/10 p-5">
      <div className="flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
        <div>
          <p className="text-sm font-semibold text-danger">{title}</p>
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

function EmptyState({ decision }) {
  return (
    <section className="border border-line bg-panel p-8 text-center">
      <div className="mx-auto grid h-12 w-12 place-items-center bg-white/5 text-muted">
        <SearchX size={22} />
      </div>
      <h2 className="mt-4 text-lg font-semibold text-ink">No requests found</h2>
      <p className="mx-auto mt-2 max-w-xl text-sm text-muted">
        {decision === 'ALL'
          ? 'No audit records have been persisted yet.'
          : `No persisted requests currently match the ${decision} decision filter.`}
      </p>
    </section>
  );
}

function RequestDetail({ request, status, error, onRetry }) {
  if (!request && status === 'idle') {
    return (
      <section className="border border-line bg-panel p-6">
        <div className="grid h-12 w-12 place-items-center bg-white/5 text-muted">
          <Eye size={21} />
        </div>
        <h2 className="mt-4 text-base font-semibold text-ink">Select a request</h2>
        <p className="mt-2 text-sm text-muted">Choose an audit record to inspect prompts, findings, and review state.</p>
      </section>
    );
  }

  if (status === 'loading') {
    return <section className="h-96 animate-pulse border border-line bg-panel/70" />;
  }

  if (status === 'error') {
    return <ErrorState title="Request detail unavailable" message={error} onRetry={onRetry} />;
  }

  if (!request) return null;

  const threats = request.threats || [];

  return (
    <section className="space-y-5 border border-line bg-panel p-5">
      <div className="flex flex-col gap-3 border-b border-line pb-4 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="text-xs font-medium uppercase tracking-[0.14em] text-muted">Request detail</p>
          <h2 className="mt-2 font-mono text-lg font-semibold text-ink">{request.requestId}</h2>
          <p className="mt-1 text-sm text-muted">{formatDate(request.createdAt)}</p>
        </div>
        <DecisionBadge decision={request.decision} />
      </div>

      <div className="grid gap-3 sm:grid-cols-3">
        <div className="border border-line bg-canvas p-4">
          <p className="text-xs uppercase tracking-[0.12em] text-muted">Risk Score</p>
          <p className={`mt-2 text-2xl font-semibold ${riskTone(request.riskScore)}`}>{request.riskScore}</p>
        </div>
        <div className="border border-line bg-canvas p-4">
          <p className="text-xs uppercase tracking-[0.12em] text-muted">LLM Contacted</p>
          <p className="mt-2 text-2xl font-semibold text-ink">{request.llmContacted ? 'YES' : 'NO'}</p>
        </div>
        <div className="border border-line bg-canvas p-4">
          <p className="text-xs uppercase tracking-[0.12em] text-muted">Threats</p>
          <p className="mt-2 text-2xl font-semibold text-ink">{threats.length}</p>
        </div>
      </div>

      <div className="space-y-3">
        <div>
          <h3 className="text-sm font-semibold text-ink">Original Prompt</h3>
          <pre className="mt-2 max-h-44 overflow-auto whitespace-pre-wrap border border-line bg-canvas p-4 text-sm text-muted">
            {request.originalPrompt || 'Not recorded'}
          </pre>
        </div>
        <div>
          <h3 className="text-sm font-semibold text-ink">Normalized Prompt</h3>
          <pre className="mt-2 max-h-44 overflow-auto whitespace-pre-wrap border border-line bg-canvas p-4 text-sm text-muted">
            {request.normalizedPrompt || 'Not recorded'}
          </pre>
        </div>
      </div>

      <div>
        <h3 className="text-sm font-semibold text-ink">Threat Findings</h3>
        {threats.length === 0 ? (
          <p className="mt-2 border border-line bg-canvas p-4 text-sm text-muted">No persisted threat findings for this request.</p>
        ) : (
          <div className="mt-3 space-y-3">
            {threats.map((threat, index) => (
              <div key={`${threat.threatType}-${index}`} className="border border-line bg-canvas p-4">
                <div className="flex flex-col gap-2 sm:flex-row sm:items-center sm:justify-between">
                  <p className="font-medium text-ink">{formatThreatName(threat.threatType)}</p>
                  <span className={`inline-flex w-fit border px-2 py-1 text-xs font-semibold ${severityStyles[threat.severity] || 'border-line text-muted'}`}>
                    {threat.severity}
                  </span>
                </div>
                <p className="mt-2 text-sm text-muted">{threat.description}</p>
                <div className="mt-3 flex flex-wrap gap-3 text-xs text-muted">
                  <span>Confidence: {Math.round((threat.confidence || 0) * 100)}%</span>
                  <span>Created: {formatDate(threat.createdAt)}</span>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      <div>
        <h3 className="text-sm font-semibold text-ink">Review Information</h3>
        {request.review ? (
          <div className="mt-2 grid gap-3 border border-line bg-canvas p-4 text-sm sm:grid-cols-2">
            <p><span className="text-muted">Decision:</span> <span className="text-ink">{request.review.decision}</span></p>
            <p><span className="text-muted">Reviewer:</span> <span className="text-ink">{request.review.reviewer || 'Not assigned'}</span></p>
            <p><span className="text-muted">Reviewed:</span> <span className="text-ink">{formatDate(request.review.reviewedAt)}</span></p>
            <p><span className="text-muted">Reason:</span> <span className="text-ink">{request.review.reason || 'Not recorded'}</span></p>
          </div>
        ) : (
          <p className="mt-2 border border-line bg-canvas p-4 text-sm text-muted">No review record is linked to this request.</p>
        )}
      </div>
    </section>
  );
}

export function RequestsPage() {
  const [decision, setDecision] = useState('ALL');
  const [requests, setRequests] = useState([]);
  const [listStatus, setListStatus] = useState('loading');
  const [listError, setListError] = useState('');
  const [selectedRequestId, setSelectedRequestId] = useState('');
  const [selectedRequest, setSelectedRequest] = useState(null);
  const [detailStatus, setDetailStatus] = useState('idle');
  const [detailError, setDetailError] = useState('');

  const loadRequests = useCallback(async () => {
    setListStatus((currentStatus) => (currentStatus === 'success' ? 'refreshing' : 'loading'));
    setListError('');

    try {
      const data = await agentguardApi.getRequests({
        decision: decision === 'ALL' ? undefined : decision,
        limit: REQUEST_LIMIT,
      });
      setRequests(data || []);
      setListStatus('success');
    } catch (apiError) {
      setRequests([]);
      setListError(apiError.message || 'Unable to reach the backend API.');
      setListStatus('error');
    }
  }, [decision]);

  const loadRequestDetail = useCallback(async (requestId) => {
    if (!requestId) return;
    setDetailStatus('loading');
    setDetailError('');

    try {
      const data = await agentguardApi.getRequest(requestId);
      setSelectedRequest(data);
      setDetailStatus('success');
    } catch (apiError) {
      setSelectedRequest(null);
      setDetailError(apiError.status === 404 ? 'Request not found.' : apiError.message || 'Unable to load request detail.');
      setDetailStatus('error');
    }
  }, []);

  useEffect(() => {
    loadRequests();
  }, [loadRequests]);

  useEffect(() => {
    if (!selectedRequestId) return;
    loadRequestDetail(selectedRequestId);
  }, [loadRequestDetail, selectedRequestId]);

  const selectedExistsInList = useMemo(() => {
    return requests.some((request) => request.requestId === selectedRequestId);
  }, [requests, selectedRequestId]);

  useEffect(() => {
    if (selectedRequestId && listStatus === 'success' && !selectedExistsInList) {
      setSelectedRequestId('');
      setSelectedRequest(null);
      setDetailStatus('idle');
    }
  }, [listStatus, selectedExistsInList, selectedRequestId]);

  return (
    <div className="space-y-6">
      <div className="flex flex-col gap-4 xl:flex-row xl:items-end xl:justify-between">
        <div>
          <p className="text-xs font-medium uppercase tracking-[0.16em] text-muted">Audit trail</p>
          <h1 className="mt-2 text-2xl font-semibold text-ink">Requests</h1>
          <p className="mt-2 max-w-3xl text-sm text-muted">
            Inspect persisted AgentGuard request decisions, risk scores, threat findings, and review metadata.
          </p>
        </div>
        <div className="flex flex-col gap-3 sm:flex-row sm:items-center">
          <div className="grid grid-cols-4 border border-line bg-panel">
            {decisionOptions.map((option) => (
              <button
                key={option}
                type="button"
                className={`h-10 px-3 text-xs font-semibold transition-colors ${
                  decision === option ? 'bg-signal text-canvas' : 'text-muted hover:bg-white/5 hover:text-ink'
                }`}
                onClick={() => setDecision(option)}
              >
                {option}
              </button>
            ))}
          </div>
          <button
            type="button"
            className="inline-flex h-10 items-center justify-center gap-2 border border-line px-4 text-sm font-medium text-muted hover:border-muted hover:text-ink disabled:cursor-not-allowed disabled:opacity-60"
            onClick={loadRequests}
            disabled={listStatus === 'loading' || listStatus === 'refreshing'}
          >
            <RefreshCw size={16} className={listStatus === 'refreshing' ? 'animate-spin' : ''} />
            Refresh
          </button>
        </div>
      </div>

      <div className="grid gap-6 xl:grid-cols-[minmax(0,1.05fr)_minmax(380px,0.95fr)]">
        <section className="border border-line bg-panel p-5">
          <div className="flex items-center justify-between gap-3 border-b border-line pb-4">
            <div>
              <h2 className="text-base font-semibold text-ink">Recent Requests</h2>
              <p className="mt-1 text-sm text-muted">Backend limit: {REQUEST_LIMIT}</p>
            </div>
            <span className="text-sm text-muted">{requests.length} shown</span>
          </div>

          <div className="mt-5">
            {listStatus === 'error' ? <ErrorState title="Requests unavailable" message={listError} onRetry={loadRequests} /> : null}
            {listStatus === 'loading' ? <LoadingState /> : null}
            {listStatus !== 'loading' && listStatus !== 'error' && requests.length === 0 ? <EmptyState decision={decision} /> : null}

            {listStatus !== 'loading' && listStatus !== 'error' && requests.length > 0 ? (
              <div className="overflow-x-auto">
                <table className="min-w-full border-collapse text-left text-sm">
                  <thead className="border-b border-line text-xs uppercase tracking-[0.12em] text-muted">
                    <tr>
                      <th className="pb-3 pr-4 font-medium">Request</th>
                      <th className="pb-3 pr-4 font-medium">Decision</th>
                      <th className="pb-3 pr-4 font-medium">Risk</th>
                      <th className="pb-3 pr-4 font-medium">Threats</th>
                      <th className="pb-3 pr-4 font-medium">LLM</th>
                      <th className="pb-3 font-medium">Created</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-line">
                    {requests.map((request) => {
                      const isSelected = request.requestId === selectedRequestId;
                      return (
                        <tr
                          key={request.requestId}
                          className={`cursor-pointer transition-colors ${isSelected ? 'bg-signal/10' : 'hover:bg-white/5'}`}
                          onClick={() => setSelectedRequestId(request.requestId)}
                        >
                          <td className="py-3 pr-4 font-mono text-xs text-ink">{request.requestId}</td>
                          <td className="py-3 pr-4"><DecisionBadge decision={request.decision} /></td>
                          <td className={`py-3 pr-4 font-semibold ${riskTone(request.riskScore)}`}>{request.riskScore}</td>
                          <td className="py-3 pr-4 text-muted">
                            {request.threats?.length ? (
                              <span className="inline-flex items-center gap-1 text-alert">
                                <ShieldAlert size={14} />
                                {request.threats.length}
                              </span>
                            ) : (
                              <span className="inline-flex items-center gap-1 text-signal">
                                <CheckCircle2 size={14} />
                                0
                              </span>
                            )}
                          </td>
                          <td className="py-3 pr-4 text-muted">{request.llmContacted ? 'YES' : 'NO'}</td>
                          <td className="py-3 text-muted">{formatDate(request.createdAt)}</td>
                        </tr>
                      );
                    })}
                  </tbody>
                </table>
              </div>
            ) : null}
          </div>
        </section>

        <RequestDetail
          request={selectedRequest}
          status={detailStatus}
          error={detailError}
          onRetry={() => loadRequestDetail(selectedRequestId)}
        />
      </div>
    </div>
  );
}
