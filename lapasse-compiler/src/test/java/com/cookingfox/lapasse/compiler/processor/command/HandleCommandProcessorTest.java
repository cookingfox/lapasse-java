package com.cookingfox.lapasse.compiler.processor.command;

import com.cookingfox.lapasse.annotation.HandleCommand;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.Element;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link HandleCommandProcessor}.
 */
public class HandleCommandProcessorTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    HandleCommandProcessor processor;

    @Before
    public void setUp() throws Exception {
        processor = new HandleCommandProcessor(mock(Element.class), mock(Types.class));
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: determineAnnotationParams
    //----------------------------------------------------------------------------------------------

    @Test
    public void determineAnnotationParams_should_throw_if_command_does_not_throw() throws Exception {
        HandleCommand annotation = mock(HandleCommand.class);
        MirroredTypeException exception = mock(MirroredTypeException.class);
        when(exception.getTypeMirror()).thenReturn(mock(TypeMirror.class));

        // don't throw for command
        when(annotation.command()).thenReturn(null);
        when(annotation.state()).thenThrow(exception);

        try {
            processor.determineAnnotationParams(annotation);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Could not extract command or state type from annotation"));
        }
    }

    @Test
    public void determineAnnotationParams_should_throw_if_state_does_not_throw() throws Exception {
        HandleCommand annotation = mock(HandleCommand.class);
        MirroredTypeException exception = mock(MirroredTypeException.class);
        when(exception.getTypeMirror()).thenReturn(mock(TypeMirror.class));

        // don't throw for state
        when(annotation.command()).thenThrow(exception);
        when(annotation.state()).thenReturn(null);

        try {
            processor.determineAnnotationParams(annotation);
            fail("Expected exception");
        } catch (Exception e) {
            assertTrue(e.getMessage().contains("Could not extract command or state type from annotation"));
        }
    }

}
