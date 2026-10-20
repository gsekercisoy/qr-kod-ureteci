package com.acrom.qr.controller;

import com.acrom.qr.model.QrRecord;
import com.acrom.qr.service.QrService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class QrController {

    private final QrService service;

    public QrController(QrService service) {
        this.service = service;
    }

    /** PNG üretir ve üretim kaydını işler (istatistik amacıyla). */
    @GetMapping(value = "/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qr(@RequestParam String text,
                                     @RequestParam(defaultValue = "400") int size,
                                     @RequestParam(defaultValue = "M") String ec,
                                     @RequestParam(defaultValue = "4") int margin) {
        byte[] png = service.render(text, size, ec, margin);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header("Cache-Control", "no-store")
                .body(png);
    }

    @GetMapping("/qr/recent")
    public List<QrRecord> recent() {
        return service.recent();
    }

    @GetMapping("/qr/stats")
    public Map<String, Object> stats() {
        return service.stats();
    }
}
