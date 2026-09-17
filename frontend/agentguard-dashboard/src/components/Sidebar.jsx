import { Bot, ClipboardCheck, LayoutDashboard, ShieldAlert, X } from 'lucide-react';
import { NavLink } from 'react-router-dom';

const navigation = [
  { to: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { to: '/demo-agent', label: 'Demo Agent', icon: Bot },
  { to: '/reviews', label: 'Review Queue', icon: ClipboardCheck },
  { to: '/requests', label: 'Requests', icon: ShieldAlert },
  { to: '/threats', label: 'Threats', icon: ShieldAlert },
];

export function Sidebar({ open, onClose }) {
  return (
    <>
      {open && <button type="button" className="fixed inset-0 z-30 bg-black/60 lg:hidden" onClick={onClose} aria-label="Close navigation" />}
      <aside className={`fixed inset-y-0 left-0 z-40 flex w-64 flex-col border-r border-line bg-[#0c121d] transition-transform lg:translate-x-0 ${open ? 'translate-x-0' : '-translate-x-full'}`}>
        <div className="flex h-16 items-center justify-between border-b border-line px-5">
          <div className="flex items-center gap-3">
            <div className="grid h-8 w-8 place-items-center bg-signal text-canvas">
              <ShieldAlert size={18} strokeWidth={2.5} />
            </div>
            <div>
              <span className="block text-sm font-bold tracking-wide text-ink">AGENTGUARD</span>
              <span className="block text-[10px] font-medium uppercase tracking-[0.14em] text-muted">AI Agent Firewall</span>
            </div>
          </div>
          <button type="button" className="text-muted lg:hidden" onClick={onClose} aria-label="Close navigation" title="Close navigation">
            <X size={20} />
          </button>
        </div>
        <nav className="flex-1 space-y-1 px-3 py-5">
          {navigation.map(({ to, label, icon: Icon }) => (
            <NavLink
              key={to}
              to={to}
              onClick={onClose}
              className={({ isActive }) => `flex items-center gap-3 border-l-2 px-3 py-2.5 text-sm font-medium transition-colors ${isActive ? 'border-signal bg-signal/10 text-signal' : 'border-transparent text-muted hover:bg-white/5 hover:text-ink'}`}
            >
              <Icon size={17} />
              {label}
            </NavLink>
          ))}
        </nav>
        <div className="border-t border-line px-5 py-4 text-xs text-muted">Local security workspace</div>
      </aside>
    </>
  );
}
