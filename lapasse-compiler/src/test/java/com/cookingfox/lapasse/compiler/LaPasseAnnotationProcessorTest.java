package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.GenerationModel;
import com.cookingfox.lapasse.compiler.exception.AnnotationProcessorException;
import com.cookingfox.lapasse.compiler.processor.ProcessorResults;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandResult;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventResult;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
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

    @Test(expected = AnnotationProcessorException.class)
    public void generateCommandHandlers_should_throw_if_method_params_null() throws Exception {
        HandleCommandResult handleCommandResult = mock(HandleCommandResult.class);
        when(handleCommandResult.getCommandTypeName()).thenReturn(TypeName.get(IncrementCount.class));
        when(handleCommandResult.getReturnTypeName()).thenReturn(TypeName.get(CountIncremented.class));

        ProcessorResults processorResults = new ProcessorResults();
        processorResults.addHandleCommandResult(handleCommandResult);

        GenerationModel model = new GenerationModel();
        model.processorResults = processorResults;

        LaPasseAnnotationProcessor processor = new LaPasseAnnotationProcessor();
        processor.generateCommandHandlers(model);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: generateEventHandlers
    //----------------------------------------------------------------------------------------------

    @Test(expected = AnnotationProcessorException.class)
    public void generateEventHandlers_should_throw_if_method_params_null() throws Exception {
        HandleEventResult handleEventResult = mock(HandleEventResult.class);
        when(handleEventResult.getEventTypeName()).thenReturn(TypeName.get(CountIncremented.class));

        ProcessorResults processorResults = new ProcessorResults();
        processorResults.addHandleEventResult(handleEventResult);

        GenerationModel model = new GenerationModel();
        model.processorResults = processorResults;

        LaPasseAnnotationProcessor processor = new LaPasseAnnotationProcessor();
        processor.generateEventHandlers(model);
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
