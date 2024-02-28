package cn.mapway.util.console;

import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * AsciiTable
 *
 * @author zhang
 */
public class AsciiTable {
    List<List<String>> lines;

    List<Integer> colMaxLength = new ArrayList<Integer>();
    String leading = "";
    private int prefixCount = 0;

    public AsciiTable() {
        this.lines = new ArrayList<List<String>>();
    }

    public static int calLength(String txt) {
        if (txt == null) {
            return 0;
        }
        int length = 0;
        for (int i = 0; i < txt.length(); i++) {
            int codepoint = txt.codePointAt(i);
            if (codepoint < 255) {
                length += 1;
            } else {
                length += 2;
            }
        }
        return length;
    }

    public static void main(String[] args) {
        AsciiTable table = new AsciiTable();
        int col = 0;
        table.setCell(0, col++, "A");
        table.setCell(0, col++, "我的人民");
        table.setCell(0, col++, "B");
        table.setPrefixCount(8);
        System.out.print(table);
    }

    public AsciiTable setPrefixCount(int prefixCount) {
        this.prefixCount = prefixCount;
        leading = Strings.dup(" ", prefixCount);
        return this;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        //输出表头

        for (int i = 0; i < colMaxLength.size(); i++) {
            if (i == 0) {
                sb.append(leading);
                sb.append("┌");
            }
            sb.append(Strings.dup('─', colMaxLength.get(i)));
            if (i < colMaxLength.size() - 1) {
                sb.append("┬");
            } else {
                sb.append("┐");
            }
        }
        sb.append("\r\n");
        for (int i = 0; i < lines.size(); i++) {
            List<String> line = lines.get(i);
            for (int j = 0; j < line.size(); j++) {
                if (j == 0) {
                    sb.append(leading);
                    sb.append("│");
                }
                sb.append(line.get(j));
                sb.append(Strings.dup(' ', colMaxLength.get(j) - calLength(line.get(j))));
                sb.append("│");
            }
            sb.append("\r\n");
            if (i < lines.size() - 1) {
                //在输出间隔行
                for (int j = 0; j < colMaxLength.size(); j++) {
                    if (j == 0) {
                        sb.append(leading);
                        sb.append("├");
                    }
                    sb.append(Strings.dup('─', colMaxLength.get(j)));
                    if (j < colMaxLength.size() - 1) {
                        sb.append("┼");
                    } else {
                        sb.append("┤");
                    }
                }
                sb.append("\r\n");
            }
        }
        for (int i = 0; i < colMaxLength.size(); i++) {
            if (i == 0) {
                sb.append(leading);
                sb.append("└");
            }
            sb.append(Strings.dup('─', colMaxLength.get(i)));
            if (i < colMaxLength.size() - 1) {
                sb.append("┴");
            } else {
                sb.append("┘");
            }
        }
        sb.append("\r\n");
        return sb.toString();
    }

    public void setCell(int row, int col, String str) {
        List<String> line = getLine(row);
        if (line.size() <= col) {
            for (int i = line.size(); i <= col; i++) {
                line.add("");
            }
            line.set(col, str);
            int length = calLength(str);
            while (colMaxLength.size() <= col) {
                colMaxLength.add(0);
            }
            int oldLength = colMaxLength.get(col);
            if (oldLength < length) {
                colMaxLength.set(col, length);
            }
        }
    }

    private List<String> getLine(int index) {
        if (index < 0) {
            index = 0;
        }
        // 0            1
        // 2(3)            1
        if (index >= lines.size()) {
            for (int i = lines.size(); i <= index; i++) {
                lines.add(new ArrayList<>());
            }
        }
        return lines.get(index);

    }
}
