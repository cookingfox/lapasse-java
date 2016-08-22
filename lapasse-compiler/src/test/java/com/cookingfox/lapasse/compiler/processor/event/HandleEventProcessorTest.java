package com.cookingfox.lapasse.compiler.processor.event;

import com.cookingfox.lapasse.annotation.HandleEvent;
import org.junit.Test;

import javax.lang.model.element.Element;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link HandleEventProcessor}.
 */
public class HandleEventProcessorTest {

    //----------------------------------------------------------------------------------------------
    // TESTS: determineAnnotationParams
    //----------------------------------------------------------------------------------------------

    @Test
    public void determineAnnotationParams_should_throw_if_mocked_annotation() throws Exception {
        Element element = mock(Element.class);
        HandleEventProcessor processor = new HandleEventProcessor(element);
        HandleEvent annotation = mock(HandleEvent.class);

        try {
            processor.determineAnnotationParams(annotation);
            fail("expected exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Could not extract event type from annotation"));
        }
    }

}
