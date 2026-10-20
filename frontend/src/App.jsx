import React, { useEffect, useState } from 'react';
import { api } from './api.js';

export default function App() {
  const [text, setText] = useState('https://github.com/gsekercisoy');
  const [size, setSize] = useState(400);
  const [ec, setEc] = useState('M');
  const [src, setSrc] = useState('');
  const [stats, setStats] = useState(null);
  const [recent, setRecent] = useState([]);
  const [err, setErr] = useState('');
  const [busy, setBusy] = useState(false);

  const refresh = () => {
    api.get('/qr/stats').then(setStats).catch(() => {});
    api.get('/qr/recent').then(setRecent).catch(() => {});
  };

  useEffect(() => { refresh(); }, []);

  const params = { text, size, ec };

  const generate = async () => {
    setErr('');
    if (!text.trim()) { setErr('Metin boş olamaz'); return; }
    setBusy(true);
    const url = api.qrUrl({ ...params, t: Date.now() });
    // yüklemeyi img üzerinden doğrula
    const img = new Image();
    img.onload = () => {
      setSrc(url);
      setBusy(false);
      refresh();
    };
    img.onerror = () => {
      setErr('Kod üretilemedi (metin çok uzun veya geçersiz olabilir)');
      setBusy(false);
    };
    img.src = url;
  };

  const download = async () => {
    try {
      await api.download(params, 'qr-kod.png');
      setErr('');
    } catch (e) { setErr(e.message); }
  };

  // başlangıçta bir kez üret
  useEffect(() => { generate(); }, []);

  return (
    <div className="app">
      <h1>🔳 QR Kod Üreteci</h1>
      <p className="lead">QR kodlar sunucuda ZXing ile PNG olarak üretilir; üretim kayıtları H2'ye yazılır.</p>
      {err && <div className="err">{err}</div>}

      <div className="layout">
        <div className="panel form-panel">
          <label>Metin / Link (en fazla 500 karakter)</label>
          <textarea
            value={text}
            maxLength={500}
            rows={4}
            onChange={e => setText(e.target.value)}
            placeholder="https://… veya herhangi bir metin"
          />

          <label>Boyut: <b>{size}px</b></label>
          <input type="range" min="200" max="800" step="50" value={size}
            onChange={e => setSize(Number(e.target.value))} />

          <label>Hata düzeltme düzeyi</label>
          <div className="ecd">
            {[
              ['L', 'L — %7'],
              ['M', 'M — %15'],
              ['Q', 'Q — %25'],
              ['H', 'H — %30']
            ].map(([v, t]) => (
              <button key={v} className={ec === v ? 'active' : ''} onClick={() => setEc(v)}>{t}</button>
            ))}
          </div>

          <div className="actions">
            <button className="btn primary" onClick={generate} disabled={busy}>
              {busy ? 'Üretiliyor…' : 'Üret'}
            </button>
            <button className="btn ghost" onClick={download} disabled={!src}>PNG indir</button>
          </div>
        </div>

        <div className="panel img-panel">
          <h2>Önizleme</h2>
          {src ? <img src={src} alt="QR kod" width="290" /> : <div className="muted empty">—</div>}
          {stats && <div className="muted note">Bu sunucuda toplam <b>{stats.totalRendered}</b> kod üretildi</div>}
        </div>
      </div>

      <div className="panel">
        <h2>🕘 Son 10 üretim (API'den)</h2>
        <table className="tbl">
          <thead><tr><th>#</th><th>Metin</th><th>Boyut</th><th>EC</th><th>Saat</th></tr></thead>
          <tbody>
            {recent.map((r, i) => (
              <tr key={r.id}>
                <td>{i + 1}</td>
                <td className="mono" title={r.text}>{r.text.length > 46 ? r.text.slice(0, 46) + '…' : r.text}</td>
                <td>{r.size}px</td>
                <td>{r.ec}</td>
                <td className="muted">{(r.createdAt || '').slice(11, 19)}</td>
              </tr>
            ))}
            {recent.length === 0 && <tr><td colSpan={5} className="muted">Kayıt yok</td></tr>}
          </tbody>
        </table>
      </div>
    </div>
  );
}
