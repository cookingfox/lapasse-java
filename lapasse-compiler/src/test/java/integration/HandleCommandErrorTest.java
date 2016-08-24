package integration;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import fixtures.example2.command.ExampleCommand;
import fixtures.example2.event.ExampleEvent;
import fixtures.example2.state.ExampleState;
import org.junit.Test;
import rx.Observable;

import javax.lang.model.element.Modifier;
import java.util.Collection;
import java.util.concurrent.Callable;

import static integration.IntegrationTestHelper.*;

/**
 * Integration tests for {@link LaPasseAnnotationProcessor} and {@link HandleCommand} processor
 * errors.
 */
public class HandleCommandErrorTest {

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD NOT ACCESSIBLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_not_accessible() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addModifiers(Modifier.PRIVATE)
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .build();

        assertCompileFails(createSource(method),
                "Method is not accessible");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER THROWS
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_throws() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addException(Exception.class)
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .build();

        assertCompileFails(createSource(method),
                "Handler methods are not allowed to declare a `throws` clause.");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS INVALID NUMBER
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_invalid_number() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .addParameter(String.class, "foo")
                .returns(ClassName.get(CountIncremented.class))
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Command");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS BOTH INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_both_invalid_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(String.class, "foo")
                .addParameter(Object.class, "bar")
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Command");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS COMMAND AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_command_and_invalid_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(IncrementCount.class, "command")
                .addParameter(String.class, "foo")
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Command");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS STATE AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_state_and_invalid_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(String.class, "foo")
                .build();

        assertCompileFails(createSource(method),
                "Method parameters are invalid (expected State and Command");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE NOT DECLARED
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_not_declared() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ClassName.bestGuess("FooBarBaz"))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ClassName.get(Boolean.class))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID COLLECTION TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_collection_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ParameterizedTypeName.get(Collection.class, String.class))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID CALLABLE TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_callable_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ParameterizedTypeName.get(Callable.class, String.class))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID OBSERVABLE TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_observable_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ParameterizedTypeName.get(Observable.class, String.class))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID CALLABLE COLLECTION TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_callable_collection_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ParameterizedTypeName.get(ClassName.get(Callable.class),
                        ParameterizedTypeName.get(Collection.class, String.class)))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID OBSERVABLE COLLECTION TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_observable_collection_type() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(ParameterizedTypeName.get(ClassName.get(Observable.class),
                        ParameterizedTypeName.get(Collection.class, String.class)))
                .build();

        assertCompileFails(createSource(method),
                "Command handler has an invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER NO METHOD OR ANNOTATION PARAMS
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_no_method_or_annotation_params() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .build();

        assertCompileFails(createSource(method),
                "Could not determine command type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER STATE NOT DETERMINABLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_state_not_determinable() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(IncrementCount.class, "command")
                .build();

        assertCompileFails(createSource(method),
                "Can not determine target state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER COMMAND NOT DETERMINABLE ONLY STATE METHOD PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_command_not_determinable_only_state_method_param() throws Exception {
        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .build();

        assertCompileFails(createSource(method),
                "Could not determine command type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER CONFLICT ANNOTATION METHOD COMMAND PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_conflict_annotation_method_command_param() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember("command", "$T.class", ExampleCommand.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .build();

        assertCompileFails(createSource(method),
                "Annotation parameter for command");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER CONFLICT ANNOTATION METHOD STATE PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_conflict_annotation_method_state_param() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember("state", "$T.class", ExampleState.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .build();

        assertCompileFails(createSource(method),
                "Annotation parameter for state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLERS TARGET STATE CONFLICT
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handlers_target_state_conflict() throws Exception {
        MethodSpec method1 = createHandleCommandMethod()
                .addParameter(CountState.class, "state")
                .addParameter(IncrementCount.class, "command")
                .returns(CountIncremented.class)
                .build();

        MethodSpec method2 = createHandleCommandMethod()
                .addParameter(ExampleState.class, "state")
                .addParameter(ExampleCommand.class, "command")
                .returns(ExampleEvent.class)
                .build();

        assertCompileFails(createSource(method1, method2),
                "Mapped command handler does not match expected concrete State");
    }

}
