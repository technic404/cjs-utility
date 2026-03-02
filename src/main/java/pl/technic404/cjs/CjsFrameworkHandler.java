package pl.technic404.cjs;

import com.intellij.lang.javascript.index.FrameworkIndexingHandler;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.impl.JSChangeUtil;
import com.intellij.lang.javascript.psi.jsdoc.JSDocComment;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.types.JSCompositeTypeFactory;
import com.intellij.lang.javascript.psi.types.JSTypeParser;
import com.intellij.lang.javascript.psi.types.JSTypeSource;
import com.intellij.lang.javascript.psi.types.JSTypeSourceFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil; // Standard IntelliJ class
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CjsFrameworkHandler extends FrameworkIndexingHandler {


    private static final Pattern CJS_TAG_PATTERN = Pattern.compile("@cjs\\s*\\{([^}]+)}");
    private static final Logger log = LoggerFactory.getLogger(CjsFrameworkHandler.class);


    @Override
    public boolean addTypeFromResolveResult(@NotNull JSTypeEvaluator evaluator,
                                         @NotNull JSEvaluateContext context,
                                         @NotNull PsiElement resolveResult) {

        if (!(resolveResult instanceof JSVariable)) return false;

        JSVariable variable = (JSVariable) resolveResult;

        // Look for the JSDocComment manually
        JSDocComment docComment = findJSDoc(variable);

        if (docComment == null) return false;

        String docText = docComment.getText();
        Matcher matcher = CJS_TAG_PATTERN.matcher(docText);

        if (matcher.find()) {
            System.out.println("Found cjs tag: " + matcher.group(1));
            String customType = matcher.group(1).trim();
            String functionSig = "(data: " + customType + ") => CjsComponent";
            String genericTypeString = "CjsComponent & { withData(data: " + customType + "): this }";


            JSReferenceExpression withDataRef =
                    (JSReferenceExpression) JSChangeUtil.createExpressionWithContext("withData", variable);


            JSTypeSource source = JSTypeSourceFactory.createTypeSource(variable, true);

            JSType type = JSTypeParser.createType(
                    variable.getProject(),
                    genericTypeString,
                    source
            );

            System.out.println(type.getTypeText());
            System.out.println(variable.getName());

            evaluator.addType(type);
            return true;
        }

        return false;
    }

    private JSDocComment findJSDoc(PsiElement element) {
        // 1. Try to find JSDoc inside the variable declaration itself
        JSDocComment child = PsiTreeUtil.getChildOfType(element, JSDocComment.class);
        if (child != null) return child;

        // 2. Look at the parent (e.g., JSVarStatement 'const x = ...')
        PsiElement parent = element.getParent();
        if (parent != null) {
            // Check for JSDoc attached to the statement
            JSDocComment parentDoc = PsiTreeUtil.getChildOfType(parent, JSDocComment.class);
            if (parentDoc != null) return parentDoc;

            // Check for JSDoc immediately before the statement
            PsiElement prev = parent.getPrevSibling();
            while (prev != null && !(prev instanceof JSDocComment)) {
                if (!(prev instanceof com.intellij.psi.PsiWhiteSpace)) break;
                prev = prev.getPrevSibling();
            }
            if (prev instanceof JSDocComment) return (JSDocComment) prev;
        }
        return null;
    }
}