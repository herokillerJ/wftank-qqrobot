package cn.wftank.qqrobot.common.translate;

import java.util.List;

/**
 * @author: wftank
 * @create: 2021-03-11 14:27
 * @description: 翻译器
 **/
public interface TranslatorApi {

    /**
     * 翻译source
     * @param source
     * @param from 源语种
     * @param to 目标语种
     * @return
     */
    String translate(String source,String from,String to);

    /**
     * 批量翻译
     * @param sourceList
     * @param from
     * @param to
     * @return
     */
    List<String> batchTranslate(List<String> sourceList, String from, String to);

}
