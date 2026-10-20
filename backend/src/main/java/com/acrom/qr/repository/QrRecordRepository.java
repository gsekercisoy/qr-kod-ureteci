package com.acrom.qr.repository;

import com.acrom.qr.model.QrRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QrRecordRepository extends JpaRepository<QrRecord, Long> {
    List<QrRecord> findTop10ByOrderByCreatedAtDesc();
}
