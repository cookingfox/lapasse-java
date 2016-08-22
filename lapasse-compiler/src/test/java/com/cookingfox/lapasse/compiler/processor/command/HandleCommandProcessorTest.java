package com.cookingfox.lapasse.compiler.processor.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import org.junit.Test;

import javax.lang.model.element.Element;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link HandleCommandProcessor}.
 */
public class HandleCommandProcessorTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: determineAnnotationParams
    //----------------------------------------------------------------------------------------------

    @Test
    public void determineAnnotationParams_should_throw_if_mocked_annotation() throws Exception {
        Element element = mock(Element.class);
        HandleCommandProcessor processor = new HandleCommandProcessor(element);
        HandleCommand annotation = mock(HandleCommand.class);

        try {
            processor.determineAnnotationParams(annotation);
            fail("expected exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Could not extract command or state type from annotation"));
        }
    }

}
