package org.epiclouds.host.pattern;

public class StringCompare {
	/**
	 * 判断会否match的代码，-1为不match，大于等于0为match
	 * @param s，正常字符串
	 * @param p，包含*和？的表达式字符串
	 * @return，-1不match，其他为匹配的正常字符串的数量
	 */
	public static int isMatch(String s, String p) {
		if(s==null||p==null){
			return -1;
		}
		int match=0,tmpMatch=0;
        int i = 0;
        int j = 0;
        int star = -1;
        int mark = -1;
        while (i < s.length()) {
            if (j < p.length()
                    && (p.charAt(j) == '?' || p.charAt(j) == s.charAt(i))) {
                ++i;
                ++j;
                tmpMatch++;
            } else if (j < p.length() && p.charAt(j) == '*') {
                star = j;
                j++;
                mark = i;
                match=tmpMatch;
           //这一步是关键，匹配s中当前字符与p中‘＊’后面的字符，如果匹配，则在第一个if中处理，如果不匹配，则继续比较s中的下一个字符。
            } else if (star != -1) {
                j = star + 1;
                i = ++mark;
                tmpMatch=match;
            } else {
                return -1;
            }
        }
       //最后在此处处理多余的‘＊’，因为多个‘＊’和一个‘＊’意义相同。
        while (j < p.length() && p.charAt(j) == '*') {
            ++j;
        }
        match=tmpMatch;
        if( j == p.length()){
        	return match;
        }
        return -1;
    }
}
