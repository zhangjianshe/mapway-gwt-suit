package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.fonts.Fonts;

public enum CodeLanguage {

    JAVA("Java", "java", Fonts.JAVA),
    PYTHON("Python", "py", Fonts.PYTHON),
    JS("JavaScript", "js", Fonts.JAVASCRIPT),
    JSON("JSON", "json", Fonts.JSON),
    HTML("HTML", "html", Fonts.HTML),
    CSS("CSS", "css", Fonts.CSS),
    XML("XML", "xml", Fonts.XML),
    SHELL("BASH", "sh", Fonts.SHELL),
    YML("YAML", "yml", Fonts.YAML),
    SQL("SQL", "sql", Fonts.SFILE),
    TEXT("文本", "txt", Fonts.FILE);

    private String code;
    private String suffix;
    private String unicode;

    CodeLanguage(String code, String suffix, String unicode) {
        this.code = code;
        this.suffix = suffix;
        this.unicode = unicode;
    }

    public static CodeLanguage fromName(String dataType) {
        if(dataType==null)
        {
            return TEXT;
        }
        for (CodeLanguage codeLanguage : CodeLanguage.values()) {
            if(codeLanguage.name().endsWith(dataType))
            {
                return codeLanguage;
            }
        }
        return TEXT;
    }

    public String getCode() {
        return code;
    }

    public String getSuffix() {
        return suffix;
    }

    public String getUnicode() {
        return unicode;
    }
}
