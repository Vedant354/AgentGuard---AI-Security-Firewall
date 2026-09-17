export function PagePlaceholder({ eyebrow, title, description, actionLabel }) {
  return (
    <section className="max-w-5xl">
      <p className="mb-2 text-xs font-medium uppercase tracking-[0.16em] text-signal">{eyebrow}</p>
      <h2 className="text-2xl font-semibold text-ink sm:text-3xl">{title}</h2>
      <p className="mt-3 max-w-2xl text-sm leading-6 text-muted">{description}</p>
      <div className="mt-8 border border-line bg-panel p-5">
        <div className="flex items-center gap-3">
          <span className="h-2 w-2 bg-alert" />
          <div>
            <p className="text-sm font-medium text-ink">{actionLabel}</p>
            <p className="mt-1 text-sm text-muted">This workspace is ready for the next dashboard increment.</p>
          </div>
        </div>
      </div>
    </section>
  );
}
