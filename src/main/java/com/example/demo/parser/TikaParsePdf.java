package com.example.demo.parser;

import com.example.demo.util.FileUtil;
import com.example.demo.processr.Process;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author dhx
 */
public class TikaParsePdf {
    public static void main(final String[] args) throws IOException,TikaException {
        //新建一个list用来存放文件路径
        List<File> filepath;

        //目标文件夹路径
        String target="D:\\大三上课程\\testFile";
        //调用方法读取文件夹下所有的pdf,doc,docx文件
        filepath= FileUtil.getFileList(target);
        Tika tika = new Tika();
        System.out.println("链表长度为：" + filepath.size());
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
                    if(content!=null){
                        content = Process.delete(content);
                        System.out.println("第" + (i+1) + "个文件内容为：");
                        System.out.print(content);
                    }
                } catch (Exception e) {
                    System.out.println("tika解析失败！");
                    e.printStackTrace();
                }

        }
    }
}


