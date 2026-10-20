const BASE = '/api';

async function req(path, opts = {}) {
  const res = await fetch(BASE + path, {
    headers: { 'Content-Type': 'application/json' },
    ...opts
  });
  if (!res.ok) {
    let msg = res.statusText;
    try {
      const j = await res.json();
      msg = j.error || msg;
    } catch (e) { /* gövde boş */ }
    throw new Error(msg);
  }
  if (res.status === 204) return null;
  return res.json();
}

export const api = {
  get: (p) => req(p),
  qrUrl: (params) => {
    const q = new URLSearchParams(params).toString();
    return `${BASE}/qr?${q}`;
  },
  download: async (params, filename) => {
    const url = `${BASE}/qr?${new URLSearchParams(params).toString()}`;
    const res = await fetch(url);
    if (!res.ok) throw new Error('İndirme başarısız');
    const blob = await res.blob();
    const a = document.createElement('a');
    a.href = URL.createObjectURL(blob);
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(a.href);
  }
};
