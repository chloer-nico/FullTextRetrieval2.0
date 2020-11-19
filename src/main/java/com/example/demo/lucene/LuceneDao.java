package com.example.demo.lucene;

import com.example.demo.config.Sysconfig;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class LuceneDao implements DisposableBean {

    private final Sysconfig sysconfig;

    public LuceneDao(@Autowired Sysconfig sysconfig){
        this.sysconfig = sysconfig;
    }

    @PostConstruct
    public void init(){

    }

    @Override
    public void destroy() throws Exception {

    }
}
