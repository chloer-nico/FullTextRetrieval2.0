package com.example.demo.parser;

import com.example.demo.util.FileUtil;
import com.example.demo.processr.Process;
import com.hankcs.hanlp.dependency.nnparser.util.Log;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dhx
 * tika的解析，传入一个文件，将其解析为string类型
 * return:处理完成的String类型
 */
@Component
public class TikaParsePdf {
    public static String  parse(File file) {
        Tika tika = new Tika();
        String result = "";
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
                    result = tika.parseToString(file);
                    if(result!=null){
                        //处理格式
                        result = Process.delete(result);
                        Log.INFO_LOG("tika解析成功！\n");
                        Log.INFO_LOG(result);
                    }
                } catch (Exception e) {
                    result="";
                    Log.INFO_LOG("tika解析失败！\n");
                    e.printStackTrace();
                }
        return result;
        }
}


