package pl.technic404.cjs;

import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.EditorColorsManager;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;

import java.awt.*;

public class CjsHighlightKeys {

    public static final TextAttributesKey CJS_METHOD =
            TextAttributesKey.createTextAttributesKey(
                    "CJS_METHOD",
                    DefaultLanguageHighlighterColors.FUNCTION_CALL
            );

    static {
//        TextAttributes attrs = new TextAttributes();
//        attrs.setForegroundColor(new Color(12, 56, 100));
//        attrs.setFontType(Font.BOLD);

        TextAttributes attrs = new TextAttributes();
        attrs.setForegroundColor(new Color(108, 166, 216)); // DodgerBlue
//        attrs.setFontType(Font.BOLD);

        EditorColorsManager.getInstance()
                .getGlobalScheme()
                .setAttributes(CJS_METHOD, attrs);
    }
}