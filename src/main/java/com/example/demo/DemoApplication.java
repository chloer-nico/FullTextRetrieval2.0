package com.example.demo;

import com.example.demo.beans.StringBag;
import com.example.demo.config.Sysconfig;
import com.example.demo.lucene.LuceneDao;
import com.example.demo.parser.Searcher;
import com.example.demo.processr.Process;
import com.example.demo.util.FileUtil;
import com.hankcs.hanlp.dependency.nnparser.util.Log;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.tika.Tika;
import org.apache.lucene.search.*;
import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dhx
 */
@SpringBootApplication
@EnableConfigurationProperties({Sysconfig.class})
public class DemoApplication {
    private static Sysconfig sysconfig;
    @Autowired  Sysconfig ssysconfig;

    /**
     * indexPath:用于存放索引的路径
     * target:待检索文件路径
     * */
    private static String indexPath = "D:\\大三上课程\\TextIndex";
    private static String target="D:\\大三上课程\\testFile\\1apdf";

    public static void main(String[] args) throws Exception {

        //处理文件，建立索引
        LuceneDao dao=new LuceneDao(sysconfig);
        dao.init();
        //查询
        Searcher searcher=new Searcher(sysconfig);
        searcher.init();

        SpringApplication app = new SpringApplication(DemoApplication.class);
        app.run(args);
    }

    }
