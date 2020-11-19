package com.example.demo.processr;

import org.springframework.stereotype.Component;

import java.beans.JavaBean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.regex.Pattern.compile;

/**
 * @author dhx
 */
@Component
public class Process {
    public static String  delete(String str){
        //使用stringbuilder来处理多余空行
        StringBuilder processer=new StringBuilder(str);
        String result;
        //创建pattern编译实例用于字符串验证,验证所有符号
        Pattern pattern= compile("\\p{P}");

        //i从1开始，略过第一个字符
        for(int i=1;i<processer.length()+1;i++){
//            System.out.print(i);
            //当字符串到最后一个位置时退出循环
            if(i==processer.length()-1){
//                System.out.print("最后一个字符"+i);
                break;
            }
            //遇到一个换行符处理
            if(processer.charAt(i)=='\n'){
//                System.out.println("换行符前面的内容为"+processer.charAt(i-1));
                //连续两个换行符，删除一个,随后进入下一个if条件处理拼接，否则两个\n相当于只处理了一个
                if(processer.charAt(i+1)=='\n'){
//                    processer.replace(i,i+1,"");
                    processer.deleteCharAt(i+1);
                }

                //单换行符情况，如果前面是一个标点符号则忽略，如果是字则连起来
                 if(processer.charAt(i+1)!='\n'){
                    Matcher matcher=pattern.matcher(String.valueOf(processer.charAt(i-1)));//用于验证匹配

                    //是标点符号，则\n忽略
                    if(matcher.matches()){//匹配
//                        System.out.println("换行符前面为标点符号，忽略");
                    }
                    //换行符后面不为标点符号，将该换行符删除
                    else{
//                       System.out.println("换行符前面不为标点符号，将该换行符删除");
                       processer.deleteCharAt(i);
//                       processer.replace(i,i+1,"");
                    }

                }
            }

            //连续两个空格，删一个
            else if(processer.charAt(i)==' ' && processer.charAt(i+1)==' '){
                processer.deleteCharAt(i+1);
//                processer.replace(i,i+1,"");
            }
            //空格+换行符
            else if(processer.charAt(i)==' ' && processer.charAt(i+1)=='\n'){
                //i和i+1两个一起删掉
                processer.replace(i,i+2,"");
            }
        }
        result=processer.toString();
//        System.out.println("处理完成!");
        return result;
    }
}
