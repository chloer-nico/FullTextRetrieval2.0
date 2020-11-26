package com.example.demo.lucene;

import com.example.demo.beans.StringBag;
import com.example.demo.config.Sysconfig;
import com.example.demo.parser.TikaParsePdf;
import com.example.demo.util.FileUtil;
import com.hankcs.hanlp.dependency.nnparser.util.Log;
import com.hankcs.lucene.HanLPAnalyzer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
@Component
public class LuceneDao implements DisposableBean {

    private final Sysconfig sysconfig;

    public LuceneDao(@Autowired Sysconfig sysconfig){
        this.sysconfig = sysconfig;
    }

    /**
     * 待处理文件：sysconfig.getDocs()
     * 建立索引的路径：sysconfig.getIdx()
     * */
    @PostConstruct
    public void init() throws IOException {
        //首先读取文件路径下所有的doc，docx，pdf文件，存入一个文件链表中
        List<File> fileList = new ArrayList<>();

        fileList= FileUtil.getFileList(sysconfig.getDocs());

        //循环处理所有的文件
        for(int i=0;i<fileList.size();i++){
            File file=fileList.get(i);
            //调用tika解析
            String content= TikaParsePdf.parse(file);

            //将文章类实例化，设置内容及标题
            BufferedReader br = new BufferedReader(new StringReader(content));
            try {
                StringBuffer sb = new StringBuffer();
                //逐行读取
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);

                    StringBag stringBag = new StringBag();
                    stringBag.setContent(sb.toString());
                    stringBag.setTitle(file.getName());

                    //此处为建立索引
                    Directory directory = FSDirectory.open(Paths.get(sysconfig.getIdx()));
                    //标准中文分词器
                    Analyzer analyzer = new HanLPAnalyzer();

                    IndexWriterConfig config = new IndexWriterConfig(analyzer);
                    IndexWriter writer = new IndexWriter(directory, config);
                    Document doc = new Document();
                    doc.add(new TextField("contents", stringBag.getContent(), Field.Store.YES));
                    doc.add(new StringField("title", stringBag.getTitle(), Field.Store.YES));
                    Log.INFO_LOG("文章的标题为："+stringBag.getTitle()+"\n");
                    writer.addDocument(doc);
                    //writer不能放到下面去？
                    writer.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                    br.close();
            }
        }

    }

    @Override
    public void destroy() throws Exception {
    }
}
