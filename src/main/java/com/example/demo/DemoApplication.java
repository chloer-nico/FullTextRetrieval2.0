package com.example.demo;


import com.example.demo.config.Sysconfig;
import com.example.demo.lucene.LuceneDao;

import org.apache.lucene.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import java.util.List;


/**
 * @author dhx
 */
@SpringBootApplication
@EnableConfigurationProperties({Sysconfig.class})
public class DemoApplication implements CommandLineRunner {

    private final Sysconfig sysconfig;

    private final LuceneDao luceneDao;
    /**
     * DemoApplication的构造函数
     * */
    public DemoApplication(@Autowired Sysconfig sysconfig,
                           @Autowired LuceneDao luceneDao){
        this.sysconfig = sysconfig;
        this.luceneDao = luceneDao;
    }


    public static void main(String[] args){

        SpringApplication app = new SpringApplication(DemoApplication.class);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        //构建索引
//        luceneDao.indexDoc();
        //搜索
        List<Document> documentList=luceneDao.search("学院","学院");
        System.out.println("共有数据"+documentList.size());
        for(int i=0;i<documentList.size();i++){
            System.out.println("第"+i+"条内容标题为————————————\n"+documentList.get(i).get("title"));
            System.out.println("第"+i+"条内容内容为——————————\n"+documentList.get(i).get("contents"));
        }
        //关闭
        luceneDao.destroy();
    }
}
