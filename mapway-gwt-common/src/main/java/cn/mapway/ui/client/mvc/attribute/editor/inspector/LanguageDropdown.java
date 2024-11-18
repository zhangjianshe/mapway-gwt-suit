package cn.mapway.ui.client.mvc.attribute.editor.inspector;

import cn.mapway.ui.client.widget.Dropdown;

public class LanguageDropdown extends Dropdown {
    public LanguageDropdown() {
        for(CodeLanguage codeLanguage: CodeLanguage.values())
        {
            addItem(codeLanguage.getUnicode(),codeLanguage.getCode(),codeLanguage);
        }
    }
}
