package cn.mapway.ui.client.mvc.attribute.editor.textbox;

public enum TextInputKind {
    TEXT,
    EMAIL,
    NUMBER,
    DATE,
    TIME,
    PHONE,
    URL,
    DATE_TIME;

    public static TextInputKind valueOfName(String name)
    {
        if(name == null || name.length() == 0)
        {
            return TEXT;
        }
        for(TextInputKind kind:TextInputKind.values())
        {
            if(kind.name().equals(name))
            {
                return kind;
            }
        }
        return TEXT;
    }

}
