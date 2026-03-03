package pl.technic404.cjs;

import com.intellij.lang.ASTNode;
import com.intellij.lang.javascript.index.FrameworkIndexingHandler;
import com.intellij.lang.javascript.index.JSIndexContentBuilder;
import com.intellij.lang.javascript.psi.*;
import com.intellij.lang.javascript.psi.ecma6.ES6Decorator;
import com.intellij.lang.javascript.psi.ecma6.impl.TypeScriptTupleMemberImpl;
import com.intellij.lang.javascript.psi.ecmal4.JSClass;
import com.intellij.lang.javascript.psi.impl.JSChangeUtil;
import com.intellij.lang.javascript.psi.jsdoc.JSDocComment;
import com.intellij.lang.javascript.psi.literal.JSLiteralImplicitElementCustomProvider;
import com.intellij.lang.javascript.psi.literal.JSLiteralImplicitElementProvider;
import com.intellij.lang.javascript.psi.resolve.JSEvaluateContext;
import com.intellij.lang.javascript.psi.resolve.JSResolveUtil;
import com.intellij.lang.javascript.psi.resolve.JSTypeEvaluator;
import com.intellij.lang.javascript.psi.resolve.JSTypeInfo;
import com.intellij.lang.javascript.psi.stubs.JSClassStub;
import com.intellij.lang.javascript.psi.stubs.JSElementIndexingData;
import com.intellij.lang.javascript.psi.stubs.JSImplicitElementStructure;
import com.intellij.lang.javascript.psi.stubs.impl.JSElementIndexingDataImpl;
import com.intellij.lang.javascript.psi.stubs.impl.JSFileCachedData;
import com.intellij.lang.javascript.psi.types.*;
import com.intellij.lang.javascript.psi.types.primitives.JSObjectType;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.util.PsiTreeUtil; // Standard IntelliJ class
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CjsFrameworkHandler extends FrameworkIndexingHandler {


    private static final Pattern CJS_TAG_PATTERN = Pattern.compile("@cjs\\s*\\{([^}]+)}");
    private static final Logger log = LoggerFactory.getLogger(CjsFrameworkHandler.class);
    private static final List<Function<String, String>> METHODS = List.of(
            (e) -> "withData(data: " + e + "): this",
            (e) -> "render(data: " + e + "): this",
            (e) -> "visualise(data: " + e + "): this"
    );

    public CjsFrameworkHandler() {
        super();
    }

    @Override
    public boolean addTypeFromResolveResult(@NotNull JSTypeEvaluator evaluator,
                                         @NotNull JSEvaluateContext context,
                                         @NotNull PsiElement resolveResult) {


        if (!(resolveResult instanceof JSVariable)) return false;

        JSVariable variable = (JSVariable) resolveResult;
        JSDocComment docComment = findJSDoc(variable);

        if (docComment == null) return false;

        String docText = docComment.getText();
        Matcher matcher = CJS_TAG_PATTERN.matcher(docText);

        if (matcher.find()) {
            String customType = matcher.group(1).trim();
//            String functionSig = "(data: " + customType + ") => CjsComponent";
//            String genericTypeString = "CjsComponent & { withData(data: " + customType + "): this }";
            String result = METHODS.stream()
                    .map(f -> f.apply(customType))
                    .collect(Collectors.joining(", "));


            JSTypeSource source = JSTypeSourceFactory.createTypeSource(variable, true);



            System.out.println(result);

            JSType type = JSTypeParser.createType(
                    variable.getProject(),
                    "class {" + result + "}",
                    source
            );

            List<JSRecordType.TypeMember> members = new ArrayList<>();


//            for (String methodName : List.of("withData", "render", "visualise")) {
//                // 1. Define the Parameter (data: MyUser)
//                JSType paramType = JSTypeParser.createType(variable.getProject(), customType, source);
//                JSParameterTypeDecoratorImpl param = new JSParameterTypeDecoratorImpl(paramType, false, false);
//
//                // 2. Define the Return Type (this)
//                JSType returnType = JSTypeParser.createType(variable.getProject(), "this", source);
//
//                // 3. Create the Function Signature
//                JSType functionSig = new JSFunctionTypeImpl(source, Collections.singletonList(param), returnType);
//
//                // 4. Create the Member
//                // The boolean 'true' at the end is the 'isFunction' flag—this is what turns it BLUE
//                members.add(new JSRecordTypeImpl.Mem(
//                        methodName,
//                        functionSig,
//                        false, // isOptional
//                        true,  // isFunction <--- THIS TRIGGERS THE BLUE COLOR
//                        null   // description
//                ));
//            }
//
//// 5. Wrap it in a Record (Object) Type
//            JSType recordType = new JSRecordTypeImpl(source, members);
//
//            evaluator.addType(recordType)

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