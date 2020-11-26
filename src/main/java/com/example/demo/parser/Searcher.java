package com.example.demo.parser;

import com.example.demo.config.Sysconfig;
import com.hankcs.hanlp.dependency.nnparser.util.Log;
import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author dhx
 */
@Component
public class Searcher implements DisposableBean {
    private final Sysconfig sysconfig;

    public Searcher(@Autowired Sysconfig sysconfig) {
        this.sysconfig = sysconfig;
    }
    @PostConstruct
    public void init() throws IOException {
        try{
            Directory dir = FSDirectory.open(Paths.get(sysconfig.getIdx()));
            IndexReader reader= DirectoryReader.open(dir);
            IndexSearcher searcher=new IndexSearcher(reader);
            //标准中文分词器
            Analyzer analyzer=new HanLPAnalyzer();
            //对contents查询
            QueryParser parser = new QueryParser("contents", analyzer);
            //模糊度0.2 content中含有学校的
            Query query = parser.parse("学~0.2") ;
            QueryParser parser1 = new QueryParser("title", analyzer);
            //模糊度0.2 title中含有学校的
            Query query1 = parser1.parse("学院~0.2") ;
            BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
            booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
            booleanQueryBuilder.add(query1, BooleanClause.Occur.SHOULD);
            BooleanQuery booleanQuery=booleanQueryBuilder.build();

            TopDocs topdocs = searcher.search(booleanQuery,800);
            ScoreDoc[] scoreDocs=topdocs.scoreDocs;
            Log.INFO_LOG("共有数据:" +  topdocs.scoreDocs.length + "条\n");
            for(int i=0; i < scoreDocs.length; i++) {
                int doc= scoreDocs[i].doc;
                Document document = searcher.doc(doc);
                Log.INFO_LOG("第" + (i+1) + "条文本的标题是:  \n" + document.get("title")+"\n");
                Log.INFO_LOG("第" + (i+1) + "条文本内容是:   \n" + document.get("contents")+"\n");
            }
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        finally{

        }


    }
    @Override
    public void destroy() throws Exception {

    }
}
