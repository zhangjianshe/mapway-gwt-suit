package cn.mapway.ui.server.apt;


import com.google.auto.service.AutoService;
import org.nutz.lang.Files;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.logging.Logger;

/**
 * JsStoreAnnotationProcessor
 * 处理 @JsStoreAnnotation
 *
 * @author zhang
 */
@SupportedAnnotationTypes(
        "cn.mapway.ui.server.apt.jsstore.JsStoreTable")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
@AutoService(Process.class)
public class JsStoreAnnotationProcessor extends AbstractProcessor {
    private static final Logger logger = Logger.getLogger(JsStoreAnnotationProcessor.class.getName());

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        StringBuilder sb = new StringBuilder();
        for (TypeElement t : annotations) {
            sb.append("\t").append(t.getQualifiedName());
            Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(t);
            if (elements != null) {
                for (Element e : elements) {
                    sb.append("\n\t").append(e.toString());
                    handleClass(e);
                }
            }
        }

        System.out.println("Processing " + sb);
        return true;
    }

    private void handleClass(Element e) {
        if (e.getKind() != ElementKind.CLASS) {
            return;
        }
        TypeElement typeElement = (TypeElement) e;
        String className = typeElement.getQualifiedName().toString();
        String packageName = null;
        int lastDot = className.lastIndexOf('.');
        if (lastDot > 0) {
            packageName = className.substring(0, lastDot);
        }

        String simpleClassName = className.substring(lastDot + 1);
        String builderClassName = className + "Builder";
        String builderSimpleClassName = builderClassName
                .substring(lastDot + 1);

        JavaFileObject builderFile = null;
        try {
            builderFile = processingEnv.getFiler()
                    .createSourceFile(builderClassName);
        } catch (IOException ex) {
            ex.printStackTrace();
            return;
        }

        try (PrintWriter out = new PrintWriter(builderFile.openWriter())) {
            if (packageName != null) {
                out.print("package ");
                out.print(packageName);
                out.println(";");
                out.println();
            }

            out.print("public class ");
            out.print(builderSimpleClassName);
            out.println(" {");
            out.println("}");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
