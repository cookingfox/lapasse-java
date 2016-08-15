package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.command.HandleCommandInfo;
import com.cookingfox.lapasse.compiler.event.HandleEventInfo;
import com.cookingfox.lapasse.compiler.exception.HandlerTargetStateConflictException;
import com.squareup.javapoet.TypeName;
import fixtures.example.state.CountState;
import org.junit.Before;
import org.junit.Test;

import javax.lang.model.element.Element;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

/**
 * Unit test for {@link Registry}.
 */
public class RegistryTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    private Registry registry;

    @Before
    public void setUp() throws Exception {
        registry = new Registry();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addHandleCommandInfo
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addHandleCommandInfo_should_throw_if_null() throws Exception {
        registry.addHandleCommandInfo(null);
    }

    @Test(expected = IllegalStateException.class)
    public void addHandleCommandInfo_should_throw_if_already_present() throws Exception {
        HandleCommandInfo handleCommandInfo = new HandleCommandInfo(mock(Element.class));

        registry.addHandleCommandInfo(handleCommandInfo);
        registry.addHandleCommandInfo(handleCommandInfo);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: addHandleEventInfo
    //----------------------------------------------------------------------------------------------

    @Test(expected = NullPointerException.class)
    public void addHandleEventInfo_should_throw_if_null() throws Exception {
        registry.addHandleEventInfo(null);
    }

    @Test(expected = IllegalStateException.class)
    public void addHandleEventInfo_should_throw_if_already_present() throws Exception {
        HandleEventInfo handleEventInfo = new HandleEventInfo(mock(Element.class));

        registry.addHandleEventInfo(handleEventInfo);
        registry.addHandleEventInfo(handleEventInfo);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: detectTargetStateConflict
    //----------------------------------------------------------------------------------------------

    @Test
    public void detectTargetStateConflict_should_not_throw_if_no_handlers() throws Exception {
        registry.detectTargetStateConflict();
    }

    @Test
    public void detectTargetStateConflict_should_not_throw_if_no_conflict() throws Exception {
        TypeName expected = TypeName.get(CountState.class);

        // add command info

        HandleCommandInfo firstCommandInfo = mock(HandleCommandInfo.class);
        when(firstCommandInfo.getStateName()).thenReturn(expected);
        registry.addHandleCommandInfo(firstCommandInfo);

        HandleCommandInfo secondCommandInfo = mock(HandleCommandInfo.class);
        when(secondCommandInfo.getStateName()).thenReturn(expected);
        registry.addHandleCommandInfo(secondCommandInfo);

        // add event info

        HandleEventInfo firstEventInfo = mock(HandleEventInfo.class);
        when(firstEventInfo.getStateName()).thenReturn(expected);
        registry.addHandleEventInfo(firstEventInfo);

        HandleEventInfo secondEventInfo = mock(HandleEventInfo.class);
        when(secondEventInfo.getStateName()).thenReturn(expected);
        registry.addHandleEventInfo(secondEventInfo);

        // detect conflict

        registry.detectTargetStateConflict();

        verify(firstCommandInfo, atLeastOnce()).getStateName();
        verify(secondCommandInfo, atLeastOnce()).getStateName();
        verify(firstEventInfo, atLeastOnce()).getStateName();
        verify(secondEventInfo, atLeastOnce()).getStateName();
    }

    @Test(expected = HandlerTargetStateConflictException.class)
    public void detectTargetStateConflict_should_throw_if_command_info_conflict() throws Exception {
        TypeName expected = TypeName.get(CountState.class);

        HandleCommandInfo firstCommandInfo = mock(HandleCommandInfo.class);
        when(firstCommandInfo.getStateName()).thenReturn(expected);
        registry.addHandleCommandInfo(firstCommandInfo);

        HandleCommandInfo secondCommandInfo = mock(HandleCommandInfo.class);
        when(secondCommandInfo.getStateName()).thenReturn(TypeName.get(State.class));
        registry.addHandleCommandInfo(secondCommandInfo);

        registry.detectTargetStateConflict();
    }

    @Test(expected = HandlerTargetStateConflictException.class)
    public void detectTargetStateConflict_should_throw_if_event_info_conflict() throws Exception {
        TypeName expected = TypeName.get(CountState.class);

        HandleEventInfo firstEventInfo = mock(HandleEventInfo.class);
        when(firstEventInfo.getStateName()).thenReturn(expected);
        registry.addHandleEventInfo(firstEventInfo);

        HandleEventInfo secondEventInfo = mock(HandleEventInfo.class);
        when(secondEventInfo.getStateName()).thenReturn(TypeName.get(State.class));
        registry.addHandleEventInfo(secondEventInfo);

        registry.detectTargetStateConflict();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: getTargetStateName
    //----------------------------------------------------------------------------------------------

    @Test
    public void getTargetStateClass_should_return_null_if_no_command_event_info() throws Exception {
        TypeName result = registry.getTargetStateName();

        assertNull(result);
    }

    @Test
    public void getTargetStateClass_should_return_first_event_info_return_type() throws Exception {
        TypeName expected = TypeName.get(CountState.class);

        HandleEventInfo firstEventInfo = mock(HandleEventInfo.class);
        when(firstEventInfo.getStateName()).thenReturn(expected);

        HandleEventInfo secondEventInfo = mock(HandleEventInfo.class);
        when(secondEventInfo.getStateName()).thenReturn(TypeName.get(State.class));

        registry.addHandleEventInfo(firstEventInfo);
        registry.addHandleEventInfo(secondEventInfo);

        TypeName result = registry.getTargetStateName();

        assertEquals(expected, result);
    }

    @Test
    public void getTargetStateClass_should_return_first_command_info_state_param() throws Exception {
        TypeName expected = TypeName.get(CountState.class);

        HandleCommandInfo firstCommandInfo = mock(HandleCommandInfo.class);
        when(firstCommandInfo.getStateName()).thenReturn(expected);

        HandleCommandInfo secondCommandInfo = mock(HandleCommandInfo.class);
        when(secondCommandInfo.getStateName()).thenReturn(TypeName.get(State.class));

        registry.addHandleCommandInfo(firstCommandInfo);
        registry.addHandleCommandInfo(secondCommandInfo);

        TypeName result = registry.getTargetStateName();

        assertEquals(expected, result);
    }

}
