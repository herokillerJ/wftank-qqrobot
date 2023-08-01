package cn.wftank;


import cn.wftank.search.WFtankSearcher;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexableField;
import org.apache.lucene.queryparser.flexible.standard.StandardQueryParser;
import org.apache.lucene.search.Query;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class SearchEngineTest{

    private static final String DEFAULT_VERSION = "latest";
    private static final String URL_PREFIX = "https://cdn.jsdelivr.net/gh/herokillerJ/starcitizen-data@";


    @Test
    public void test() throws Exception {
        File extFile = new File("./download_index_file/ext.json");
        File inFile = new File("./download_index_file/index.json");
        List<File> list =  new LinkedList<>();
        list.add(extFile);
        list.add(inFile);
        WFtankSearcher wFtankSearcher = new WFtankSearcher(list.toArray(new File[list.size()]));
        wFtankSearcher.destroy();
    }

    @Test
    public void queryTest() throws Exception{
        File extFile = new File("./download_index_file/ext.json");
        File inFile = new File("./download_index_file/index.json");
        List<File> list =  new LinkedList<>();
        list.add(extFile);
        list.add(inFile);
        WFtankSearcher wFtankSearcher = new WFtankSearcher(list.toArray(new File[list.size()]));
        StandardQueryParser parser = new StandardQueryParser();
        Analyzer analizer = wFtankSearcher.getAnalizer();
        List<String> strings = analizeString(analizer, "\"响尾蛇\"");
        parser.setAnalyzer(analizer);
//        Query query1 = parser.parse("grade:[1 TO 10]", "");
//        BooleanQuery.Builder builder = new BooleanQuery.Builder();
//        strings.forEach(str -> {
//            builder
//                    .add(new TermQuery(new Term("name", str)), BooleanClause.Occur.SHOULD)
//                    .add(new TermQuery(new Term("名称", str)),BooleanClause.Occur.SHOULD);
//        });
//        BooleanQuery query1 = builder.build();
        Query query1 = parser.parse("name_cn:响尾蛇", "");
        long start = System.currentTimeMillis();
//        Sort sort = new Sort(new SortedNumericSortField("age", SortField.Type.LONG,false));
        List<Document> result = wFtankSearcher.search(query1, 100);
        for (Document doc: result) {
            List<IndexableField> fields = doc.getFields();
            for (IndexableField field : fields) {
                System.out.print(field.name()+":"+field.stringValue()+ " ");
            }
            System.out.println();
        }
        long end = System.currentTimeMillis();
        System.out.println("查询耗时: "+(end-start)+" ms");
        wFtankSearcher.destroy();
    }

    public List<String> analizeString(Analyzer analyzer,String queryWords){
        List<String> list = new LinkedList<>();
        //参数一：域名    参数二：要分析的文本内容
        try (TokenStream tokenStream=analyzer.tokenStream("token",queryWords)) {
            //添加一个引用，可以获得每个关键词
            CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);
            //将指针调整到列表的头部
            tokenStream.reset();
            //遍历关键词列表，通过incrementToken方法判断列表是否结束
            while(tokenStream.incrementToken()){
                //取关键词
                list.add(charTermAttribute.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
}
