package cn.mapway.ui.client.util;


import cn.mapway.ui.client.event.MessageObject;
import cn.mapway.ui.client.tools.IShowMessage;
import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.i18n.client.constants.TimeZoneConstants;
import com.google.gwt.regexp.shared.MatchResult;
import com.google.gwt.regexp.shared.RegExp;
import elemental2.core.JsObject;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * StringUtil
 *
 * @author zhangjianshe@gmail.com
 */
public class StringUtil {
    public final static String STRING_DAY = "天";
    public final static String STRING_HOUR = "小时";
    public final static String STRING_MINUTE = "分";
    public final static String STRING_SECOND = "秒";

    public static final String NUMBER_PREFIX = "^\\d*\\.";
    public static final String NUMBER_PREFIX_EXTRACT = "^(\\d*)\\.";

    private static final String character = "ABCDEFGHIGKLMNOPQRSTUVWXYZabcdefghigklmnopqrstuvwxyz";
    public static DateTimeFormat df;
    public static DateTimeFormat dfS;
    public static TimeZone timeZoneShanghai;
    // 时间日格式
    public static String FULL_DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    static Random random = new Random(new Date().getTime());

    static {
        TimeZoneConstants timeZoneConstants = GWT.create(TimeZoneConstants.class);
        timeZoneShanghai = TimeZone.createTimeZone(timeZoneConstants.asiaShanghai());
        df = DateTimeFormat.getFormat(FULL_DATETIME_FORMAT);
        dfS = DateTimeFormat.getFormat(FULL_DATETIME_FORMAT + ".SSS");
    }

    public static Date parseDate(String format, String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        return DateTimeFormat.getFormat(format).parse(value);
    }

    /**
     * 去掉字符串前后空白字符。空白字符的定义由Character.isWhitespace来判断
     *
     * @param cs 字符串
     * @return 去掉了前后空白字符的新字符串
     */
    public static String trim(CharSequence cs) {
        if (null == cs)
            return null;
        int length = cs.length();
        if (length == 0)
            return cs.toString();
        int l = 0;
        int last = length - 1;
        int r = last;
        for (; l < length; l++) {
            if (!Character.isWhitespace(cs.charAt(l)))
                break;
        }
        for (; r > l; r--) {
            if (!Character.isWhitespace(cs.charAt(r)))
                break;
        }
        if (l > r)
            return "";
        else if (l == 0 && r == last)
            return cs.toString();
        return cs.subSequence(l, r + 1).toString();
    }

    public static boolean isBlank(String cs) {
        if (null == cs || cs.length() == 0)
            return true;
        int length = cs.length();
        for (int i = 0; i < length; i++) {
            if (!(Character.isWhitespace(cs.charAt(i))))
                return false;
        }
        return true;
    }

    public static boolean isNotBlank(String cs) {
        return !isBlank(cs);
    }

    public static List<String> splitIgnoreBlank(String s, String regex) {
        if (s == null || regex == null) {
            return new ArrayList<String>();
        }
        String[] ss = s.split(regex);
        List<String> list = new LinkedList<String>();
        for (String st : ss) {
            if (isBlank(st))
                continue;
            list.add(trim(st));
        }
        return list;
    }

    public static String formatFileSize(Long size) {
        if (size == null) {
            return "未知大小";
        }
        NumberFormat df = NumberFormat.getFormat("#.00");
        long SZU = 1024L;
        if (size < SZU) {
            return df.format(size) + " B";
        }
        double n = size / SZU;
        if (n < SZU) {
            return df.format(n) + " KB";
        }
        n = n / SZU;
        if (n < SZU) {
            return df.format(n) + " MB";
        }
        n = n / SZU;
        if (n < SZU) {
            return df.format(n) + " GB";
        }
        n = n / SZU;
        if (n < SZU) {
            return df.format(n) + " TB";
        }
        n = n / SZU;
        return df.format(n) + " PB";
    }

    /**
     * 字符串简化版本
     *
     * @param str
     * @return
     */
    public static String brief(String str, int len) {
        if (isBlank(str) || (str.length() + 3) <= len)
            return str;
        int w = len / 2;
        int l = str.length();
        return str.substring(0, len - w) + " ... " + str.substring(l - w);
    }

    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return df.format(date, timeZoneShanghai);
    }

    public static String formatDateS(Date date) {
        if (date == null) {
            return "";
        }
        return dfS.format(date, timeZoneShanghai);
    }

    /**
     * 2021-12-07T11:19:55.628922Z
     *
     * @param time
     * @return
     */
    public static String formIsoDate(String time) {
        if (time == null) {
            return "";
        }
        if (time.length() < 20) {
            return time;
        }
        return time.substring(0, 10) + time.substring(11, 19);
    }

    public static String formatNumber(Integer num, int length) {
        if (num == null) {
            return "";
        }
        String format = "#";
        for (int i = 0; i < length; i++) {
            format += "0";
        }
        NumberFormat df = NumberFormat.getFormat(format);
        return df.format(num);
    }

    public static String formatDouble(Double num, int precision) {
        if (num == null) {
            return "";
        }
        String format = "";
        if (precision > 0) {
            format = ".";
        }
        for (int i = 0; i < precision; i++) {
            format += "0";
        }
        NumberFormat df = NumberFormat.getFormat(format);
        return df.format(num);
    }

    public static String formatFloat(Float num, int precision) {
        if (num == null) {
            return "";
        }
        String format = "";
        if (precision > 0) {
            format = ".";
        }
        for (int i = 0; i < precision; i++) {
            format += "0";
        }
        NumberFormat df = NumberFormat.getFormat(format);
        return df.format(num);
    }

    /**
     * 格式化毫秒时间间隔
     *
     * @param numberOfMilliseconds
     * @return
     */
    public static String formatMillseconds(long numberOfMilliseconds) {

        long numberOfDays = TimeUnit.MILLISECONDS.toDays(numberOfMilliseconds);
        numberOfMilliseconds -= TimeUnit.DAYS.toMillis(numberOfDays);

        long numberOfHours = TimeUnit.MILLISECONDS.toHours(numberOfMilliseconds);
        numberOfMilliseconds -= TimeUnit.HOURS.toMillis(numberOfHours);

        long numberOfMinutes = TimeUnit.MILLISECONDS.toMinutes(numberOfMilliseconds);
        numberOfMilliseconds -= TimeUnit.MINUTES.toMillis(numberOfMinutes);

        long numberOfSeconds = TimeUnit.MILLISECONDS.toSeconds(numberOfMilliseconds);
        numberOfMilliseconds -= TimeUnit.SECONDS.toMillis(numberOfSeconds);

        StringBuilder stringBuilder = new StringBuilder();

        if (numberOfDays > 0) {
            stringBuilder.append(numberOfDays + STRING_DAY);
        }

        if (numberOfHours > 0) {
            stringBuilder.append(numberOfHours + STRING_HOUR);
        }

        if (numberOfMinutes > 0) {
            stringBuilder.append(numberOfHours + STRING_MINUTE);
        }

        if (numberOfSeconds > 0) {
            stringBuilder.append(numberOfHours + STRING_SECOND);
        }

//        if(numberOfMilliseconds > 0) {
//            if(numberOfMilliseconds == 1)
//                stringBuilder.append(String.format("%d millisecond", numberOfMilliseconds));
//            else
//                stringBuilder.append(String.format("%d milliseconds", numberOfMilliseconds));
//        }
        return stringBuilder.toString();
    }

    public static String formatTimeSpan(Long estTime) {
        return formatTimeSpan(estTime, "前");
    }

    // 判断闰年
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

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
    public static String formatTimeSpan(Long estTime, String suffix) {
        String result = "";
        if (estTime == null || estTime < 0) {
            result = "";
        }
        else if (estTime < 60) {
            result = estTime + "秒";
        }
        else if (estTime < 60 * 60) {
            result = (int) (estTime / 60) + "分钟";
        }
        else if (estTime < 24 * 60 * 60) {
            result = (int) (estTime / (60 * 60)) + "小时";
        }
        else if (estTime < 7 * 24 * 60 * 60) {
            result = (int) (estTime / (24 * 60 * 60)) + "天";
        }
        else if (estTime < 4 * 7 * 24 * 60 * 60) {
            result = (int) (estTime / (7 * 24 * 60 * 60)) + "周";
        }
        else if (estTime < 365 * 24 * 60 * 60) {
            result = (int) ((estTime / (30 * 24 * 60 * 60)) + 1) + "月";
        }else {

            long SECONDS_IN_MINUTE = 60;
            long SECONDS_IN_HOUR = 3600;
            long SECONDS_IN_DAY = 86400;
            long SECONDS_IN_YEAR = 31536000;  // 不考虑闰年，后面会调整
            long SECONDS_IN_LEAP_YEAR = 31622400;

            // 从1970年开始的基础年份
            int startYear = 1970;
            long secondsRemaining = estTime;

            // 计算年数
            int years = 0;
            while (secondsRemaining >= SECONDS_IN_YEAR) {
                // 判断是否闰年
                if (isLeapYear(startYear + years)) {
                    if (secondsRemaining >= SECONDS_IN_LEAP_YEAR) {
                        secondsRemaining -= SECONDS_IN_LEAP_YEAR;
                    } else {
                        break;
                    }
                } else {
                    if (secondsRemaining >= SECONDS_IN_YEAR) {
                        secondsRemaining -= SECONDS_IN_YEAR;
                    } else {
                        break;
                    }
                }
                years++;
            }

            // 计算月数
            int[] daysInMonths = {31, isLeapYear(startYear + years) ? 29 : 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
            int months = 0;
            for (int i = 0; i < 12; i++) {
                long secondsInMonth = daysInMonths[i] * SECONDS_IN_DAY;
                if (secondsRemaining >= secondsInMonth) {
                    secondsRemaining -= secondsInMonth;
                    months++;
                } else {
                    break;
                }
            }

            // 计算天数
            int days = (int) (secondsRemaining / SECONDS_IN_DAY);
            secondsRemaining %= SECONDS_IN_DAY;

            // 计算小时
            long hours = secondsRemaining / SECONDS_IN_HOUR;
            secondsRemaining %= SECONDS_IN_HOUR;

            // 计算分钟
            long minutes = secondsRemaining / SECONDS_IN_MINUTE;

            // 剩余秒
            long seconds = secondsRemaining % SECONDS_IN_MINUTE;

            // 输出结果
            result = years + "年" + months + "月" + days + "天" + hours + "小时" + minutes + "分钟" + seconds + "秒";
        }
        if (StringUtil.isBlank(suffix)) {
            return result;
        } else {
            return result + suffix;
        }
    }

    public static String randomColor() {
        return Colors.randomColor();
    }

    public static String randomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        int bound = character.length();
        for (int i = 0; i < length; i++) {
            sb.append(character.charAt(random.nextInt(bound)));
        }
        return sb.toString();
    }

    /**
     * 计算 total 除以 mod 后的 有效行数
     *
     * @param total
     * @param mod
     * @return
     */
    public static int calMod(int total, int mod) {
        if (mod <= 0) {
            return total;
        }
        return total / mod + (total % mod == 0 ? 0 : 1);
    }

    public static String[] departCssValue(String s) {
        if (StringUtil.isBlank(s)) {
            return new String[]{"", ""};
        }
        String exp = "([+-]?\\d*\\.?\\d+)(.*)";
        MatchResult exec = RegExp.compile(exp).exec(s);
        if (exec == null || exec.getGroupCount() == 0) {
            return new String[]{"", ""};
        }
        return new String[]{exec.getGroup(1), exec.getGroup(2)};
    }

    public static float parseCssValue(String s) {
        if (s == null || s.length() == 0) {
            return 0;
        }
        String exp = "([-]?[0-9]+\\.?[0-9]+)";
        MatchResult exec = RegExp.compile(exp).exec(s);
        if (exec == null || exec.getGroupCount() == 0) {
            return 0;
        }
        return Float.parseFloat(exec.getGroup(1));
    }

    public static boolean checkReg(String value, String pattern, String message, IShowMessage showMessage) {
        if (value == null || isBlank(pattern)) {
            if (showMessage != null) {
                showMessage.showMessage(0, MessageObject.CODE_FAIL, message == null ? "检验错误" : message);
            }
            return false;
        }
        RegExp regExp = RegExp.compile(pattern);
        return regExp.test(value);
    }

    public static boolean isNotEmpty(String value, String message, IShowMessage showMessage) {
        if (isBlank(value)) {
            if (showMessage != null) {
                showMessage.showMessage(0, MessageObject.CODE_FAIL, message == null ? "检验错误" : message);
            }
            return false;
        }
        return true;
    }

    /**
     * 检查是否是数字
     *
     * @param value
     * @param message
     * @param showMessage
     * @return
     */
    public static boolean isNumber(String value, String message, IShowMessage showMessage) {
        String numberRegex = "[0-9\\.]*";
        return checkReg(value, numberRegex, message, showMessage);
    }

    public static String format(String pattern, Object... parameters) {
        if (pattern == null || pattern.length() == 0) {
            return "";
        }
        if (parameters == null || parameters.length == 0) {
            return pattern;
        }
        for (int i = 0; i < parameters.length; i++) {
            Object obj = parameters[i];
            pattern = pattern.replaceFirst("\\{\\d\\}", obj == null ? "" : obj.toString());
        }
        return pattern;
    }

    /**
     * 去最后一段 /abc/def/j98  返回 j98
     *
     * @param name
     * @param c
     * @return
     */
    public static String last(String name, String c) {
        if (StringUtil.isBlank(name)) {
            return "";
        }
        int lastIndexOf = name.lastIndexOf(c);
        if (lastIndexOf >= 0) {
            return name.substring(lastIndexOf + 1);
        }
        return name;
    }

    /**
     * 不会产生异常的parseInt
     * @param text
     * @param defaultValue
     * @return
     */
    public static Integer parseInteger(String text, int defaultValue) {
        if(text == null|| text.length() == 0){
            return defaultValue;
        }
        try {
            return Integer.parseInt(text);
        }
        catch (Exception e){
            return defaultValue;
        }
    }

    /**
     * 将整型值 吸附到 按照step的网格上
     *
     * @param value
     * @param step
     * @return
     */
    public static int attachTo(int value, int step) {

        int temp = value % step;
        if (temp > step / 2) {
            return value + step - temp;
        } else {
            return value - temp;
        }
    }

    /**
     * 解析名字 将 名字前面的前缀 都去掉 比如 /ABC/DEF  返回 DEF
     *
     * @param name
     * @return
     */
    public static String extractName(String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        int index = name.lastIndexOf("/");
        if (index >= 0) {
            return name.substring(index + 1);
        }
        return name;
    }

    /**
     * 解析名字 将 名字前面的前缀 都去掉 比如 /ABC/DEF.git  返回 DEF
     *
     * @param location
     * @return
     */
    public static String extractBaseName(String location) {
        String temp = extractName(location);
        int index = temp.lastIndexOf(".");
        if (index >= 0) {
            return temp.substring(0, index);
        }
        return temp;
    }

    /**
     * 获取 文件的后缀名称 会转换为小写
     *
     * @param location
     * @return
     */
    public static String suffix(String location) {
        if (location == null || location.length() == 0) {
            return "";
        }
        int index = location.lastIndexOf('.');
        if (index > 0) {
            return location.substring(index + 1).toLowerCase();
        }
        return "";
    }

    public static String formatDate(Date date, String format) {
        if (date == null) {
            date = new Date();
        }
        if (format == null || format.length() == 0) {
            return formatDate(date);
        } else {
            return DateTimeFormat.getFormat(format).format(date);
        }
    }

    /**
     * 格式化进度值 [0,100]->n% 其他的值 返回 -- 或者 空字符串
     *
     * @param progress
     * @return
     */
    public static String formatProgress(Integer progress) {
        if (progress == null) {
            return "";
        }
        if (progress >= 0 && progress <= 100) {
            return progress + "%";
        } else {
            return "--";
        }
    }

    public static String extractLocation(String location) {
        if (location == null || location.length() == 0) {
            return "";
        }
        int index = location.lastIndexOf("/");
        if (index >= 0) {
            return location.substring(0, index);
        }
        return location;
    }

    /**
     * Obj对象转为字符串
     *
     * @param obj
     * @return
     */
    public static String obj2String(Object obj) {
        if (obj == null) {
            return "";
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof Long) {
            return String.valueOf(obj);
        }
        if (obj instanceof Integer) {
            return String.valueOf(obj);
        }
        if (obj instanceof Float) {
            return StringUtil.formatDouble(((Float) obj).doubleValue(), 4);
        } else if (obj instanceof JsObject) {
            JsObject jsObj = (JsObject) obj;
            return jsObj.toString();
        } else {
            return obj.toString();
        }
    }

    public static double random(double start, double end) {
        return start + random.nextDouble() * (end - start);
    }

    public static Double parseDouble(String value, double defaultValue) {
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    public static String extractSuffix(String name) {
        if (name == null) return "";
        int index = name.lastIndexOf(".");
        if (index < 0) {
            return "";
        }
        return name.substring(index + 1);
    }

    public static String orElse(String value, String defaultValue) {
        if (value == null || value.length() == 0) {
            return defaultValue;
        }
        return value;
    }

    public static String concat(String s, List<String> params) {
        if (params == null) {
            return "";
        }
        if (s == null) {
            s = "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            String item = params.get(i);
            if (item == null) {
                item = "";
            }
            if (i > 0) {
                sb.append(s);
            }
            sb.append(item);
        }
        return sb.toString();
    }

    public static String removeNumberPrefix(String str) {
        if (str == null) {
            return "";
        }
        return str.replaceFirst(NUMBER_PREFIX, "");
    }

    public static int parseNumberPrefix(String str,int defaultValue)
    {
        if (str==null || str.length()==0)
        {
            return defaultValue;
        }
        RegExp regExp=RegExp.compile(NUMBER_PREFIX_EXTRACT);
        MatchResult exec = regExp.exec(str);
        if(exec!=null)
        {
            exec.getGroup(1);
            return Integer.parseInt(exec.getGroup(1));
        }
        return defaultValue;
    }

    /**
     * Converts a Long number to a path-like string by formatting it with '/' every three characters.
     *
     * @param number the Long number to convert
     * @return the formatted path string
     * @throws IllegalArgumentException if the number is null
     */
    public static String numberToPath(Long number) {
        if (number == null) {
            throw new IllegalArgumentException("Number cannot be null");
        }
        return stringToPath(String.valueOf(number));
    }

    /**
     * Formats a string by inserting '/' every three characters.
     *
     * @param str the string to format
     * @return the formatted path string
     */
    public static String stringToPath(String str) {
        if (str == null || str.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i += 2) {
            int end = Math.min(i + 2, str.length());
            if (i > 0) {
                sb.append("/");
            }
            sb.append(str.substring(i, end));
        }
        return sb.toString();
    }

    public static String uuidToPath(String uuid)
    {
        // Validate input
        if (uuid == null || uuid.length() < 2) {
            throw new IllegalArgumentException("UUID must be non-null and at least 2 characters long");
        }

        // Remove hyphens to get a continuous string (optional, depending on desired format)
        String cleanUuid = uuid.replace("-", "");

        // Ensure the cleaned UUID is long enough
        if (cleanUuid.length() < 2) {
            throw new IllegalArgumentException("UUID after removing hyphens is too short");
        }

        // Split into directory (first 2 chars) and file (remaining chars)
        String directory = cleanUuid.substring(0, 2);
        String file = cleanUuid.substring(2);

        // Combine into path format
        return directory + "/" + file;

    }
}
