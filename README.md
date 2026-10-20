# QR Kod Üreteci

React (Vite) arayüz + Java (Spring Boot 2.7) + **ZXing** ile sunucu tarafında PNG QR üretimi.
Kodlar tarayıcıda değil, API'de rasterize edilir (`image/png` döner); üretim kayıtları H2'de tutulur.

## Özellikler

- Metin/link (≤500 karakter), boyut 200-800 px, marj 2-10 modül
- 4 hata düzeltme düzeyi: L (%7) · M (%15) · Q (%25) · H (%30)
- Anında önizleme (`<img>` doğrudan API'yi yükler) + **PNG indir** (blob downoad)
- Üretim geçmişi: son 10 kayıt (`/api/qr/recent`) + toplam sayaç (`/api/qr/stats`)
- Doğrulama: boş metin / geçersiz boyut / geçersiz marj → 400

## Çalıştırma

```bash
cd backend && mvn spring-boot:run     # API :8080
cd frontend && npm install && npm run dev   # UI :5173 (proxy → 8080)
```

## API

| Metot | Yol | Açıklama |
|---|---|---|
| GET | `/api/qr?text=&size=&ec=&margin=` | PNG döner (`image/png`), üretim kaydı işlenir |
| GET | `/api/qr/recent` | Son 10 üretim kaydı |
| GET | `/api/qr/stats` | `{totalRendered}` — toplam üretim sayısı |

Not: istatistik için üretim kaydı, hafif veullanış — tekils izleme yok, kayıtlar bellek içi H2'de tutulup kalıcı değildir.

Duman testi: PNG magic bytes doğrulandı (`89 50 4E 47 0D 0A 1A 0A`) — geçerli PNG üretimi ✓
