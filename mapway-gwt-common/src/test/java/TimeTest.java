import org.nutz.lang.Times;

import java.text.ParseException;
import java.util.Date;

public class TimeTest {

    /**
     * 格式化时间区间
     * < 60           分钟内        刚刚
     * < 60*60        小时内        n分钟前
     * < 24*60*60     天内          n小时前
     * < 7*24*60*60   周内          n天前
     * < 4*7*24*60*60 月内          n周前
     * < 365*24*60*60 年内          n月前
     * <              好几年        n年m月
     *
     * @param estTime 时间单位为秒 second
     * @return
     */
    public static String formatTimeSpan(Long estTime) {
        if (estTime == null || estTime < 0) {
            return "";
        }
        if (estTime < 60) {
            return "刚刚";
        }
        if (estTime < 60 * 60) {
            return (int) (estTime / 60) + "分钟前";
        }
        if (estTime < 24 * 60 * 60) {
            return (int) (estTime / (60 * 60)) + "小时前";
        }
        if (estTime < 7 * 24 * 60 * 60) {
            return (int) (estTime / (24 * 60 * 60)) + "天前";
        }
        if (estTime < 4 * 7 * 24 * 60 * 60) {
            return (int) (estTime / (7 * 24 * 60 * 60)) + "周前";
        }
        if (estTime < 365 * 24 * 60 * 60) {
            return (int) ((estTime / (30 * 24 * 60 * 60)) + 1) + "月前";
        }
        //estTime分鐘之前對應的時間點
        long time = System.currentTimeMillis() - estTime * 1000;
        Date then = new Date(time);
        Date now = new Date();
        int year = now.getYear() - then.getYear() - 1;
        int month = now.getMonth() + (11 - then.getMonth());
        if (month == 11) {
            year = year + 1;
            month = 0;
        }
        if (month == 0) {
            return year + "年前";
        } else {
            return year + "年" + month + "月前";
        }
    }

    public static void main(String[] args) {
        Date then = null;
        try {
            then = Times.parse("yyyy-MM-dd HH:mm:ss", "2020-09-10 10:00:00");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String s = formatTimeSpan((Times.now().getTime() - then.getTime()) / 1000);
        System.out.println(s);
    }


}
