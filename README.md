# 
基于Apache tika和Apache lucene的全文检索系统  
  项目基本功能：  
（一）使用 Aache Tika 对文档内容进行提取和解析  
（二）对提取出来的文本进行格式处理  
（三）使用 Apache Lucene 进行建立索引并建立相对应的 searcher  
（四）在前端页面实现基本的查询功能  


  项目结构介绍：  
·beans：
StringBag: 待处理文档的模型类
StringMode: 系统工作模式的枚举类
·config：
Sysconfig: 工作目录的配置类
lucene：
LuceneDao:用于建立 lucene 索引和 lucene searcher
·parser：
TikaParserPdf: 使用 tika 循环读取目录下的所有待处理文件，并提取内容存入
String 数组中
·processr:
Process： 将提取的内容进行格式处理
·util:
FileUtil:文档处理工具 判断文件格式是否是指定的 doc docx pdf 文档
·web:
·controller:
QueryController：处理前端发送的请求，并显示结果于结果页面
·model:
ErrorMsg:错误信息的模型类
QueryDetail:检索详情的模型类
QueryListResult:检索列表的模型类
QuerreRequest:检索请求的模型类
SearchResult:搜索结果的模型类
