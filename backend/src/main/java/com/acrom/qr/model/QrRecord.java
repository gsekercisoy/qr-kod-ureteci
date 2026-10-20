package com.acrom.qr.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "qr_records")
public class QrRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty("id")
    public Long id;

    @Column(nullable = false, length = 500)
    @JsonProperty("text")
    public String text;

    @Column(nullable = false)
    @JsonProperty("size")
    public int size;

    @Column(nullable = false, length = 1)
    @JsonProperty("ec")
    public String ec = "M";

    @JsonProperty("createdAt")
    public LocalDateTime createdAt = LocalDateTime.now();
}
