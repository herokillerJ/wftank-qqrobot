package cn.wftank.qqrobot.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    //找出最长相同的字符串
    public static String longestCommonSubstring(String S1, String S2){
        int Start = 0;
        int Max = 0;
        for (int i = 0; i < S1.length(); i++)
        {
            for (int j = 0; j < S2.length(); j++)
            {
                int x = 0;
                while (Character.toLowerCase(S1.charAt(i + x)) == Character.toLowerCase(S2.charAt(j + x)))
                {
                    x++;
                    if (((i + x) >= S1.length()) || ((j + x) >= S2.length())) break;
                }
                if (x > Max)
                {
                    Max = x;
                    Start = i;
                }
            }
        }
        return S1.substring(Start, (Start + Max));
    }

    /**
     * 替换掉所有标点符号
     * @param source
     * @return
     */
    public static String replaceAllMarks(String source) {
        return source.replaceAll("[\\pP\\s‘’“”]", "");
    }

}
