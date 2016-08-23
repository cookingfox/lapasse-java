package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.compiler.exception.AnnotationProcessorException;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandMethodParams;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventMethodParams;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.annotation.processing.Filer;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link LaPasseAnnotationProcessor}.
 */
public class LaPasseAnnotationProcessorTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: generateCommandHandlers
    //----------------------------------------------------------------------------------------------

    @Test
    public void generateCommandHandlers_should_support_enums() throws Exception {
        for (HandleCommandMethodParams value : HandleCommandMethodParams.values()) {
            HandleCommandMethodParams.valueOf(value.name());
        }
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: generateEventHandlers
    //----------------------------------------------------------------------------------------------

    @Test
    public void generateEventHandlers_should_support_enums() throws Exception {
        for (HandleEventMethodParams value : HandleEventMethodParams.values()) {
            HandleEventMethodParams.valueOf(value.name());
        }
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: writeJavaFile
    //----------------------------------------------------------------------------------------------

    @Test(expected = AnnotationProcessorException.class)
    public void writeJavaFile_should_throw_if_invalid() throws Exception {
        String pkg = "1";
        String cls = "A";

        Filer filer = mock(Filer.class);
        when(filer.createSourceFile(String.format("%s.%s", pkg, cls))).thenThrow(new IOException());

        JavaFile javaFile = JavaFile.builder(pkg, TypeSpec.classBuilder(cls).build()).build();

        LaPasseAnnotationProcessor processor = new LaPasseAnnotationProcessor();
        processor.filer = filer;
        processor.writeJavaFile(javaFile, null);
    }

}
