package pl.technic404.cjs;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.javascript.psi.JSReferenceExpression;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import java.util.stream.Collectors;

import static pl.technic404.cjs.Constants.METHODS;

public class CjsAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (!(element instanceof JSReferenceExpression ref)) return;

        if (!METHODS.stream()
                .map(f -> f.apply(""))
                .map(e -> e.get(0))
                .toList()
                .contains(ref.getReferenceName())
        ) return;

        PsiElement nameElement = ref.getReferenceNameElement();
        if (nameElement == null) return;

        holder.newAnnotation(HighlightSeverity.INFORMATION, "")
                .range(nameElement) // ✅ highlight ONLY the identifier
                .textAttributes(CjsHighlightKeys.CJS_METHOD)
                .create();
    }
}
