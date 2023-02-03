import org.nutz.lang.Files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CompareTest {
    public static void main(String[] args) {
        List<String> olds=Files.readLines(new File("D:\\data\\old.txt"));
        List<String> news=Files.readLines(new File("D:\\data\\new.txt"));

        List<String> temp=new ArrayList<String>();

        for(String old:olds) {
            boolean find=false;
            for (String n:news) {
                if(old.equals(n)) {
                    find=true;
                    System.out.println(old+"\t"+n);
                    break;
                }
            }
            if(!find){
                temp.add(old);
            }
        }

        System.out.println("================================================");

        for(String t:temp){
            System.out.println(t+"");
        }

        System.out.println("================="+temp.size()+"============================");

    }
}
