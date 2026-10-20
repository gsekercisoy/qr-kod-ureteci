package com.acrom.qr.service;

import com.acrom.qr.model.QrRecord;
import com.acrom.qr.repository.QrRecordRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class QrService {

    public byte[] render(String text, int size, String ec, int margin) {
        if (text == null || text.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metin bos olamaz");
        }
        text = text.trim();
        if (text.length() > 500) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Metin en fazla 500 karakter olabilir");
        }
        if (size < 200 || size > 800) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "size 200-800 araliginda olmali");
        }
        if (margin < 2 || margin > 10) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "margin 2-10 araliginda olmali");
        }
        ErrorCorrectionLevel level = parseEc(ec);

        try {
            QRCodeWriter writer = new QRCodeWriter();
            Map<EncodeHintType, Object> hints = new LinkedHashMap<>();
            hints.put(EncodeHintType.ERROR_CORRECTION, level);
            hints.put(EncodeHintType.MARGIN, margin);
            BitMatrix matrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size, hints);

            // matrix -> PNG (javase modulune gerek kalmadan)
            BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    image.setRGB(x, y, matrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(image, "png", out);
            byte[] png = out.toByteArray();

            QrRecord rec = new QrRecord();
            rec.text = text;
            rec.size = size;
            rec.ec = ec == null ? "M" : ec.toUpperCase();
            repo.save(rec);

            return png;
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "QR uretilemedi: " + e.getMessage());
        }
    }

    public java.util.List<QrRecord> recent() {
        return repo.findTop10ByOrderByCreatedAtDesc();
    }

    public Map<String, Object> stats() {
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("totalRendered", repo.count());
        return out;
    }

    private ErrorCorrectionLevel parseEc(String ec) {
        if (ec == null) return ErrorCorrectionLevel.M;
        String e = ec.trim().toUpperCase();
        if ("L".equals(e)) return ErrorCorrectionLevel.L;
        if ("Q".equals(e)) return ErrorCorrectionLevel.Q;
        if ("H".equals(e)) return ErrorCorrectionLevel.H;
        return ErrorCorrectionLevel.M;
    }

    private final QrRecordRepository repo;

    public QrService(QrRecordRepository repo) {
        this.repo = repo;
    }
}
