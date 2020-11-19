package com.example.demo;

import com.example.demo.beans.StringBag;
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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

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
public class DemoApplication {
    /**
     * indexPath:用于存放索引的路径
     * target:待检索文件路径
     * */
    private static String indexPath = "D:\\大三上课程\\TextIndex";
    private static String target="D:\\大三上课程\\testFile\\1apdf";
    public static void main(String[] args) {
        //        SpringApplication.run(DemoApplication.class, args);
        SpringApplication app = new SpringApplication(DemoApplication.class);
        //读取所有的文件
        List<File> filepath;
        //创建文件的实体类列表
        List<StringBag> list = new ArrayList<StringBag>();
        //调用方法读取文件夹下所有的pdf,doc,docx文件
        filepath= FileUtil.getFileList(target);
        Tika tika = new Tika();
        System.out.println("链表长度为：" + filepath.size());

        //开始循环目录处理,建立索引
        for(int i=0;i<filepath.size();i++) {
            //获取链表中的单个文件
            File file = filepath.get(i);
            //文件名
            String fileName=file.getName();
            //文件路径
            String filePath=file.getAbsolutePath();
            //打印文件信息
            if(fileName.endsWith(".pdf")){
                System.out.println("pdf文件为：" + filePath);
            }
            else if(fileName.endsWith(".doc")){
                System.out.println("doc文件为：" + filePath);
            }
            else if(fileName.endsWith(".docx")){
                System.out.println("docx文件为：" + filePath);
            }
            try{
                //处理,已知tika有部分文档无法解析，比如存在图片的文档
                String content = tika.parseToString(file);
                if(content!=null) {
                    //处理内容格式
                    content = Process.delete(content);

                    //将文章类实例化，设置内容及标题
                    BufferedReader br = new BufferedReader(new StringReader(content));
                    StringBuffer sb = new StringBuffer();
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    br.close();
                    StringBag stringBag = new StringBag();
                    stringBag.setContent(sb.toString());
                    stringBag.setTitle(fileName);

                    //此处为建立索引
                    Directory directory = FSDirectory.open(Paths.get(indexPath));
                    //标准中文分词器
                    Analyzer analyzer = new HanLPAnalyzer();

                    IndexWriterConfig config = new IndexWriterConfig(analyzer);
                    IndexWriter writer = new IndexWriter(directory, config);
                    Document doc = new Document();
                    doc.add(new TextField("contents", stringBag.getContent(), Field.Store.YES));
                    doc.add(new StringField("title", stringBag.getTitle(), Field.Store.YES));
                    Log.INFO_LOG("文章的标题为："+stringBag.getTitle()+"\n");
                    writer.addDocument(doc);

//                    analyzer.close();
//                    directory.close();
                    writer.close();
                    Log.INFO_LOG("tika解析成功！\n");
                }
            } catch (Exception e) {
                Log.ERROR_LOG("tika解析失败！\n");
                e.printStackTrace();
            }

        }

        //查询
        try {
            Directory dir = FSDirectory.open(Paths.get(indexPath));
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

            TopDocs topdocs = searcher.search(booleanQuery,filepath.size());
            ScoreDoc[] scoreDocs=topdocs.scoreDocs;
            Log.INFO_LOG("共有数据:" +  topdocs.scoreDocs.length + "条\n");
            for(int i=0; i < scoreDocs.length; i++) {
                int doc= scoreDocs[i].doc;
                Document document = searcher.doc(doc);
                Log.INFO_LOG("第" + (i+1) + "条文本的标题是:  \n" + document.get("title")+"\n");
                Log.INFO_LOG("第" + (i+1) + "条文本内容是:   \n" + document.get("contents")+"\n");
//                System.out.println("第" + i + "条文本的路径是:  " + document.get("path"));
//                System.out.println("第" + i + "条文本的标题是:  " + document.get("title"));
//                System.out.println("第" + i + "条文本内容是:   " + document.get("contents"));
            }
            reader.close();

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        app.run(args);
    }

    }
