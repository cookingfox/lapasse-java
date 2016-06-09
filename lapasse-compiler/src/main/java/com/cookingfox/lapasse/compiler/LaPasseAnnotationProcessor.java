package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.Diagnostic;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by abeldebeer on 08/06/16.
 */
public class LaPasseAnnotationProcessor extends AbstractProcessor {

    private Elements elements;
    private Messager messager;
    private Types types;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elements = processingEnv.getElementUtils();
        messager = processingEnv.getMessager();
        types = processingEnv.getTypeUtils();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                HandleCommand.class.getCanonicalName(),
                HandleEvent.class.getCanonicalName()
        ));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleCommand.class)) {
            HandleCommandInfo info = new HandleCommandInfo(element);
            info.process();

            System.out.println(info);

            if (info.isValid()) {
                System.out.println("HANDLE COMMAND IS VALID!");
            } else {
                error(element, info.getError());
            }
        }

        for (Element element : roundEnv.getElementsAnnotatedWith(HandleEvent.class)) {
            HandleEventInfo info = new HandleEventInfo(element);
            info.process();

            System.out.println(info);

            if (info.isValid()) {
                System.out.println("HANDLE EVENT IS VALID!");
            } else {
                error(element, info.getError());
            }
        }

        return false;
    }

    private void error(Element element, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    }


}
