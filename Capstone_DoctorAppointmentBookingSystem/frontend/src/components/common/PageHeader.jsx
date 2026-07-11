import React from 'react';

/** Consistent page title + subtitle + optional right-side action slot. */
export default function PageHeader({ title, subtitle, action }) {
  return (
    <div className="page-header d-flex flex-wrap align-items-start justify-content-between gap-2">
      <div>
        <h1>{title}</h1>
        {subtitle && <p>{subtitle}</p>}
      </div>
      {action && <div>{action}</div>}
    </div>
  );
}