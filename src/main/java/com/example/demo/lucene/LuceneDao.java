package com.example.demo.lucene;

import com.example.demo.beans.StringBag;
import com.example.demo.config.Sysconfig;
import com.example.demo.parser.TikaParsePdf;
import com.example.demo.util.FileUtil;
import com.hankcs.hanlp.dependency.nnparser.util.Log;
import com.hankcs.lucene.HanLPIndexAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dhx
 * maxLength:解析出来的文件个数
 */
@Component
public class LuceneDao implements DisposableBean {

    private final Sysconfig sysconfig;
    private List<File> fileList;
    private Directory directory;
    private IndexWriterConfig writerConfig;
    private HanLPIndexAnalyzer analyzer;
    private QueryParser parser;
    private IndexReader reader;
    private IndexWriter writer;
    private IndexSearcher searcher;
    private int maxLength;

    public LuceneDao(@Autowired Sysconfig sysconfig) throws IOException {
        this.sysconfig = sysconfig;
        //初始化索引
        analyzer = new HanLPIndexAnalyzer();
        //首先读取文件路径下所有的doc，docx，pdf文件，存入一个文件链表中
        fileList = new ArrayList<>();
        //索引路径
        directory = FSDirectory.open(Paths.get(sysconfig.getIdx()));

        writerConfig= new IndexWriterConfig(analyzer);;
//        writer=new IndexWriter(directory, writerConfig);
        fileList= FileUtil.getFileList(sysconfig.getDocs());
    }

    /**
     * 构建索引
     * */
    public void indexDoc() throws IOException {


        Log.INFO_LOG("sysconfig的文章路径————————————"+sysconfig.getDocs()+"\n");
        writer = new IndexWriter(directory, writerConfig);
        List<StringBag> list=new ArrayList<>();
        //循环处理所有的文件
        for(int i=0;i<fileList.size();i++){
            File file=fileList.get(i);
            //调用tika解析
            String content= TikaParsePdf.parse(file);
            //content为空表示没有解析成功
            if(content==""){
                continue;
            }
            else{
                //将文章类实例化，设置内容及标题
                BufferedReader br = new BufferedReader(new StringReader(content));
                try {
                    StringBuffer sb = new StringBuffer();
                    //逐行读取
                    String line = null;
                    StringBag stringBag = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    stringBag= new StringBag();
                    stringBag.setContent(sb.toString());
                    stringBag.setTitle(file.getName());
//                    Log.INFO_LOG("stingbag的title"+file.getName()+"\n");
                    list.add(stringBag);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
        for(StringBag stringBag : list){
            Document doc = new Document();
            doc.add(new TextField("contents", stringBag.getContent(), Field.Store.YES));
            doc.add(new StringField("title", stringBag.getTitle(), Field.Store.YES));
            writer.addDocument(doc);
            Log.INFO_LOG("stingbag的title"+stringBag.getContent()+"\n");
        }
        writer.close();
    }

    /**
     * 搜索标题带有tittle,内容含有content的模糊度为0.4的记录
     * */
    public List<Document> search(String tittle,String content) throws IOException, ParseException {
        maxLength=fileList.size();
        //读取索引
        reader= DirectoryReader.open(directory);
        searcher=new IndexSearcher(reader);
        //对content查询
        parser = new QueryParser("contents", analyzer);
        Query query = parser.parse(content+"~0.4") ;

        //对tittle查询
        QueryParser parser1 = new QueryParser("title", analyzer);
        Query query1 = parser1.parse(tittle+"~0.4") ;

        BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
        booleanQueryBuilder.add(query, BooleanClause.Occur.SHOULD);
        booleanQueryBuilder.add(query1, BooleanClause.Occur.SHOULD);
        BooleanQuery booleanQuery=booleanQueryBuilder.build();

        TopDocs topdocs = searcher.search(booleanQuery,maxLength);
        ScoreDoc[] scoreDocs=topdocs.scoreDocs;
        Log.INFO_LOG("共有数据:" +  topdocs.scoreDocs.length + "条\n");
        List<Document> documentList=new ArrayList<>();
        for(int i=0; i < scoreDocs.length; i++) {
            int doc= scoreDocs[i].doc;
            Document document = searcher.doc(doc);
            documentList.add(document);
//            Log.INFO_LOG("第" + (i+1) + "条文本的标题是:  \n" + document.get("title")+"\n");
//            Log.INFO_LOG("第" + (i+1) + "条文本内容是:   \n" + document.get("contents")+"\n");
        }
        return documentList;
    }

    @Override
    public void destroy() throws Exception {
        if(writer!=null){
            writer.close();
        }
        if (reader!=null){
            reader.close();
        }
    }
}
