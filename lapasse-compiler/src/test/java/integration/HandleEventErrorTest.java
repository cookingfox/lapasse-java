package integration;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import fixtures.example2.event.ExampleEvent;
import fixtures.example2.state.ExampleState;
import org.junit.Test;

import javax.lang.model.element.Modifier;

import static integration.IntegrationTestHelper.*;

/**
 * Integration tests for {@link LaPasseAnnotationProcessor} and {@link HandleEvent} processor
 * errors.
 */
public class HandleEventErrorTest {

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD NOT ACCESSIBLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_not_accessible() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .build();

        assertCompileFails(createSource(method),
                "Method is not accessible");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD ALSO HAS COMMAND ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_also_has_command_annotation() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addAnnotation(HandleCommand.class)
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Annotated handler method can not have both a command and an event annotation");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD THROWS
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_throws() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .addException(Exception.class)
                .build();

        assertCompileFails(createSource(method),
                "Handler methods are not allowed to declare a `throws` clause.");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER RETURN TYPE DOES NOT EXTEND STATE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_return_type_does_not_extend_state() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(Boolean.class)
                .build();

        assertCompileFails(createSource(method),
                "Return type of @HandleEvent annotated method must extend");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER RETURN TYPE DOES NOT EXTEND STATE (raw State type)
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_return_type_does_not_extend_state_raw_state() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(State.class)
                .build();

        assertCompileFails(createSource(method),
                "Return type of @HandleEvent annotated method must extend");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER NO METHOD OR ANNOTATION PARAMS
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_no_method_or_annotation_params() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Could not determine the target event type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER NO EVENT METHOD PARAM NO ANNOTATION PARAMS
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_no_event_method_param_no_annotation_params() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Could not determine the target event type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS INVALID NUMBER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_invalid_number() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .addParameter(Object.class, "foo")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS INVALID TYPES
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_both_invalid_types() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(Integer.class, "foo")
                .addParameter(String.class, "bar")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS EVENT AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_event_and_invalid_type() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountIncremented.class, "event")
                .addParameter(String.class, "foo")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS STATE AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_state_and_invalid_type() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(String.class, "foo")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER INVALID METHOD PARAM BASE STATE TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_invalid_method_param_base_state_type() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(State.class, "state")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "State parameter cannot be the base type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER INVALID METHOD PARAMS BASE STATE TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_invalid_method_params_base_state_type() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(State.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "State parameter cannot be the base type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER INVALID METHOD PARAMS BASE STATE TYPE DIFFERENT ORDER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_invalid_method_params_base_state_type_different_order() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountIncremented.class, "event")
                .addParameter(State.class, "state")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "State parameter cannot be the base type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER INVALID METHOD PARAM BASE EVENT TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_invalid_method_param_base_event_type() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(Event.class, "event")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Event parameter cannot be the base type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER INVALID METHOD PARAMS BASE EVENT TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_invalid_method_params_base_event_type() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(Event.class, "event")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Event parameter cannot be the base type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER INVALID METHOD PARAMS BASE EVENT TYPE DIFFERENT ORDER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_invalid_method_params_base_event_type_different_order() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(Event.class, "event")
                .addParameter(CountState.class, "state")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Event parameter cannot be the base type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER CONFLICT EVENT ANNOTATION METHOD PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_conflict_annotation_method_event_param() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleEvent.class)
                .addMember("event", "$T.class", ExampleEvent.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(CountState.class)
                .build();

        assertCompileFails(createSource(method),
                "Annotation parameter for event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER CONFLICT STATE RETURN TYPE METHOD PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_conflict_state_return_type_method_param() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(ExampleState.class)
                .build();

        assertCompileFails(createSource(method),
                "has different type than return type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER CONFLICT STATE RETURN TYPE METHOD PARAM DIFFERENT ORDER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_conflict_state_return_type_method_param_different_order() throws Exception {
        MethodSpec method = createHandleEventMethod()
                .addParameter(CountIncremented.class, "event")
                .addParameter(CountState.class, "state")
                .returns(ExampleState.class)
                .build();

        assertCompileFails(createSource(method),
                "has different type than return type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLERS TARGET STATE CONFLICT
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handlers_target_state_conflict() throws Exception {
        MethodSpec method1 = createHandleEventMethod()
                .addParameter(CountState.class, "state")
                .addParameter(CountIncremented.class, "event")
                .returns(CountState.class)
                .build();

        MethodSpec method2 = createHandleEventMethod()
                .addParameter(ExampleState.class, "state")
                .addParameter(ExampleEvent.class, "event")
                .returns(ExampleState.class)
                .build();

        assertCompileFails(createSource(method1, method2),
                "Mapped event handler does not match expected concrete State");
    }

}
