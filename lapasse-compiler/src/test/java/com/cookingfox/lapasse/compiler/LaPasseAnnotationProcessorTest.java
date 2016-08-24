package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.compiler.exception.AnnotationProcessorException;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandAnnotationParams;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandMethodParams;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandReturnValue;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventAnnotationParams;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventMethodParams;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import org.junit.Test;

import javax.annotation.processing.Filer;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testing.TestingUtils.superficialEnumCodeCoverage;

/**
 * Unit tests for {@link LaPasseAnnotationProcessor}.
 */
public class LaPasseAnnotationProcessorTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: enums
    //----------------------------------------------------------------------------------------------

    @Test
    public void should_cover_all_enums() throws Exception {
        superficialEnumCodeCoverage(HandleCommandAnnotationParams.class);
        superficialEnumCodeCoverage(HandleCommandMethodParams.class);
        superficialEnumCodeCoverage(HandleCommandReturnValue.class);
        superficialEnumCodeCoverage(HandleEventAnnotationParams.class);
        superficialEnumCodeCoverage(HandleEventMethodParams.class);
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
