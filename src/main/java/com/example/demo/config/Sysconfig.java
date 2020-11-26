package com.example.demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "demo.dir")
public class Sysconfig {
    @Value("${demo.dir.root}")
    private String root;
    @Value("${demo.dir.idx}")
    private String idx;
    @Value("${demo.dir.docs}")
    private String docs;

    public Sysconfig() {
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
    }

    public String getDocs() {
        return docs;
    }

    public void setDocs(String docs) {
        this.docs = docs;
    }
}
