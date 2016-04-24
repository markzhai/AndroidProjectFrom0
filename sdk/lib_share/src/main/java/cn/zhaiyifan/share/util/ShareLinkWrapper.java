package cn.zhaiyifan.share.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 处理分享链接
 */
public class ShareLinkWrapper {

    // URL正则
    private static final Pattern URL_PATTERN = Pattern.compile("[.]*(http[s]{0,1}://[\\p{Alnum}|.]+[:\\d]?[\\p{Graph}]*)[.]*");

    public static String wrapShareTextWithLink(String shareText) {
        try {
            StringBuilder wrapText = new StringBuilder();
            Matcher matcher = URL_PATTERN.matcher(shareText);
            int stIdx = 0;
            while (matcher.find()) {
                wrapText.append(shareText.substring(stIdx, matcher.start()));
                String shareLink = matcher.group();
                wrapText.append(shareLink);
                stIdx = matcher.end();
            }
            wrapText.append(shareText.substring(stIdx));
            return wrapText.toString();
        } catch (Exception e) {
            return shareText;
        }
    }
}
