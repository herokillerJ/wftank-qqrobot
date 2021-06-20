package cn.wftank.search;

import cn.wftank.search.analyzer.ExtIKAnalyzer;
import cn.wftank.search.analyzer.IKConfig;
import cn.wftank.search.index.WFtankDocument;
import cn.wftank.search.index.parser.JsonDocumentParser;
import com.google.common.collect.Lists;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 搜索引擎类，单个应用应该保证单例，直接new来使用
 */
public class WFtankSearcher implements DisposableBean {

    private static final Logger log = LoggerFactory.getLogger(WFtankSearcher.class);

    private AtomicReference<DirectoryReader> directoryReaderAtomicReference = new AtomicReference<>();
    private AtomicReference<IndexSearcher> indexSearcherAtomicReference = new AtomicReference<>();
    private AtomicReference<Analyzer> analyzerAtomicReference = new AtomicReference<>();
    /**
     * 索引存放路径
     */
    private String indexPath;
    private static final String DEFAULT_INDEX_PATH = "./search/index-files";

    /**
     * 分词器配置文件路径
     */
    private String analyzerConfigPath;
    private static final String DEFAULT_ANALYZER_CONFIG_PATH = "./search/ik-analyzer/IKAnalyzer.cfg.xml";

    /**
     * 由于构造函数入参冲突,所以文件以数组的形式传入了
     * @param indexPath
     * @param analyzerConfigPath
     * @param indexFiles
     */
    public WFtankSearcher(String indexPath, String analyzerConfigPath,File[] indexFiles) {
        this.indexPath = indexPath;
        this.analyzerConfigPath = analyzerConfigPath;
        initFromfile(Lists.newArrayList(indexFiles));
    }

    public WFtankSearcher(String indexPath, String analyzerConfigPath,List<String> indexString) {
        this.indexPath = indexPath;
        this.analyzerConfigPath = analyzerConfigPath;
        initFromString(indexString);
    }

    public WFtankSearcher(File[] indexFiles) {
        this(DEFAULT_INDEX_PATH,DEFAULT_ANALYZER_CONFIG_PATH, indexFiles);
    }

    public WFtankSearcher(List<String> indexString) {
        this(DEFAULT_INDEX_PATH,DEFAULT_ANALYZER_CONFIG_PATH, indexString);
    }

    /**
     * 刷新索引
     */
    public void reloadIndexFromFile(File[] indexFiles){
       synchronized (this){
           this.loadIndexFile(Lists.newArrayList(indexFiles));
           DirectoryReader oldReader = directoryReaderAtomicReference.get();
           try {
               DirectoryReader newReader = DirectoryReader.openIfChanged(oldReader);
               directoryReaderAtomicReference.set(newReader);
               indexSearcherAtomicReference.set(new IndexSearcher(newReader));
           } catch (IOException e) {
               log.error("reload index error,ex:"+ExceptionUtils.getStackTrace(e));
           }
       }
    }

    /**
     * 刷新索引
     */
    public void reloadIndexFromString(List<String> indexList){
        synchronized (this){
            this.loadIndexString(indexList);
            DirectoryReader oldReader = directoryReaderAtomicReference.get();
            try {
                DirectoryReader newReader = DirectoryReader.openIfChanged(oldReader);
                directoryReaderAtomicReference.set(newReader);
                indexSearcherAtomicReference.set(new IndexSearcher(newReader));
            } catch (IOException e) {
                log.error("reload index error,ex:"+ExceptionUtils.getStackTrace(e));
            }
        }
    }

    /**
     * 查询
     */
    public List<Document> search(Query query, int n) {
        LinkedList<Document> documents = new LinkedList<>();
        try {
            IndexSearcher indexSearcher = indexSearcherAtomicReference.get();
            TopDocs result = indexSearcher.search(query, n);
            for (ScoreDoc scoreDoc : result.scoreDocs) {
                Document doc = indexSearcher.doc(scoreDoc.doc);
                documents.add(doc);
            }
        }catch (IOException e){
            log.error("搜索时出现异常:"+ExceptionUtils.getStackTrace(e));
        }
        return documents;
    }

    /**
     * 排序查询
     */
    public List<Document> search(Query query, int n, Sort sort) throws IOException {
        LinkedList<Document> documents = new LinkedList<>();
        IndexSearcher indexSearcher = indexSearcherAtomicReference.get();
        TopFieldDocs result = indexSearcher.search(query, n, sort);
        for (ScoreDoc scoreDoc : result.scoreDocs) {
            Document doc = indexSearcher.doc(scoreDoc.doc);
            documents.add(doc);
        }
        return documents;
    }

    public List<String> analizeString(String queryWords){
        List<String> list = new LinkedList<>();
        //参数一：域名    参数二：要分析的文本内容
        try (TokenStream tokenStream=getAnalizer().tokenStream("token",queryWords)) {
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

    private void initFromfile(List<File> indexFiles) {
        try {
            this.loadIndexFile(indexFiles);
            //先提前创建reader,等加载完后再更新
            Directory indexDir = FSDirectory.open(Paths.get(indexPath));
            DirectoryReader reader = DirectoryReader.open(indexDir);
            directoryReaderAtomicReference.set(reader);
            indexSearcherAtomicReference.set(new IndexSearcher(reader));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new IllegalArgumentException("索引路径初始化异常，请检查程序是否有["+indexPath+"]目录的操作权限");
        }
    }

    private void initFromString(List<String> indexList) {
        try {
            loadIndexString(indexList);
            Directory indexDir = FSDirectory.open(Paths.get(indexPath));
            DirectoryReader reader = DirectoryReader.open(indexDir);
            directoryReaderAtomicReference.set(reader);
            indexSearcherAtomicReference.set(new IndexSearcher(reader));
        } catch (IOException e) {
            log.error(ExceptionUtils.getStackTrace(e));
            throw new IllegalArgumentException("索引路径初始化异常，请检查程序是否有["+indexPath+"]目录的操作权限");
        }
    }

    public Analyzer getAnalizer(){
        return analyzerAtomicReference.get();
    }

    /**
     * 从文件加载索引
     * @param indexFiles
     */
    private void loadIndexFile(List<File> indexFiles){
        IndexWriter indexWriter = null;
        loadAnalyzer(analyzerConfigPath);
        try {
            Directory indexDir = FSDirectory.open(Paths.get(indexPath));
            IndexWriterConfig iwc = new IndexWriterConfig(analyzerAtomicReference.get());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(indexDir, iwc);
            indexWriter.deleteAll();
            handleIndexFile(indexWriter, indexFiles);
            indexWriter.commit();
        } catch (IOException e) {
            log.error("load index failed,will use old index,ex:"+ ExceptionUtils.getStackTrace(e));
        }finally {
            if (null != indexWriter){
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * 从json字符串加载索引
     * @param indexList
     */
    private void loadIndexString(List<String> indexList){
        IndexWriter indexWriter = null;
        loadAnalyzer(analyzerConfigPath);
        try {
            Directory indexDir = FSDirectory.open(Paths.get(indexPath));
            IndexWriterConfig iwc = new IndexWriterConfig(analyzerAtomicReference.get());
            iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            indexWriter = new IndexWriter(indexDir, iwc);
            indexWriter.deleteAll();
            handleIndexList(indexWriter, indexList);
            indexWriter.commit();
        } catch (IOException e) {
            log.error("load index failed,will use old index,ex:"+ ExceptionUtils.getStackTrace(e));
        }finally {
            if (null != indexWriter){
                try {
                    indexWriter.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void handleIndexFile(IndexWriter indexWriter, List<File> indexFiles) {
        for (File indexfile : indexFiles) {
            try {
                LineIterator lineIterator = FileUtils.lineIterator(indexfile);
                while (lineIterator.hasNext()){
                    String jsonIndex = lineIterator.next();
                    WFtankDocument document = JsonDocumentParser.parseJsonDocument(jsonIndex);
                    document.updateDocument(indexWriter);
                }
            } catch (IOException e) {
                log.error("load index file: {} exception:{}",indexfile.getName(), ExceptionUtils.getStackTrace(e));
            }
        }
    }

    private void handleIndexList(IndexWriter indexWriter, List<String> indexList) {
        for (String jsonIndex : indexList) {
            try {
                WFtankDocument document = JsonDocumentParser.parseJsonDocument(jsonIndex);
                document.updateDocument(indexWriter);
            } catch (IOException e) {
                log.error("load index string: {} exception:{}",jsonIndex, ExceptionUtils.getStackTrace(e));
            }
        }
    }


    private void loadAnalyzer(String analyzerConfigPath) {
        IKConfig ikConfig = new IKConfig(analyzerConfigPath);
        Analyzer analyzer = new ExtIKAnalyzer(ikConfig);
        analyzerAtomicReference.set(analyzer);
    }


    @Override
    public void destroy() throws Exception {
        directoryReaderAtomicReference.get().close();
    }
}
