import org.nutz.lang.util.Regex;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegTest {
    public static void main(String[] args) {
        String test ="nextval('layer_1_2_fid_seq'::regclass)";
        String regex="'(.*)'";
        Pattern pattern = Regex.getPattern(regex);
        Matcher matcher = pattern.matcher(test);
        if(matcher.find()){
            System.out.println(matcher.group(1));
        }
    }
    static String[] parse(String test)
    {
        if(test==null || test.length()==0){
            return new String[]{"",""};
        }
        String exp = "([+-]?\\d*\\.?\\d+)(.*)";
        Pattern pattern = Regex.getPattern(exp);
        Matcher matcher = pattern.matcher(test);
        if(matcher.find()){
            return new String[]{matcher.group(1),matcher.group(2)};
        }
        else {
            return new String[]{"",""};
        }
    }
}
