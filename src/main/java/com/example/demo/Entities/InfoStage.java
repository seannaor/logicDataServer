package com.example.demo.Entities;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name = "info_stages")
public class InfoStage extends Stage {
    @Lob
    private String info;
}
