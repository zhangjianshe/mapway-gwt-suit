package cn.mapway.spring.processor.module;


import cn.mapway.util.console.Ansi;
import cn.mapway.util.console.AnsiColor;

import java.util.ArrayList;
import java.util.List;

public class ApiModuleDefine {
    public String qname;
    public List<ModulePara> parameters;
    public List<FieldDefine> fields;

    public ApiModuleDefine(String qname) {
        this.qname = qname;
        parameters = new ArrayList<>();
        fields = new ArrayList<>();
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        Ansi boldFormat = Ansi.from(AnsiColor.FG_BLUE).set(AnsiColor.STYLE_HIGH_INTENSITY);
        Ansi yellow = Ansi.from(AnsiColor.FG_YELLOW);
        Ansi green = Ansi.from(AnsiColor.FG_GREEN);
        Ansi lightBlue = Ansi.from(AnsiColor.FG_CYAN);
        sb.append(boldFormat.val(qname));
        if (parameters.size() > 0) {
            sb.append(yellow.val(" <"));
            int i = 0;
            for (ModulePara p : parameters) {
                if (i++ > 0) {
                    sb.append(" , ");
                }
                sb.append(lightBlue.val(p.toString()));
            }
            sb.append(yellow.val(" > "));
        }
        sb.append("\r\n");

        for (FieldDefine field : fields) {
            sb.append(green.val(field.name) + "\t\t" + lightBlue.val(field.qTypeName));
            sb.append("\r\n");
        }
        return sb.toString();
    }
}
