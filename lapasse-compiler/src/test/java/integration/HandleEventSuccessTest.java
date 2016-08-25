package integration;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import fixtures.example2.event.ExampleEvent;
import fixtures.example2.state.ExampleState;
import org.junit.Test;

import static com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.*;
import static integration.IntegrationTestHelper.*;

/**
 * Integration tests for {@link LaPasseAnnotationProcessor} and successful {@link HandleEvent}
 * processor results.
 */
public class HandleEventSuccessTest {

    //----------------------------------------------------------------------------------------------
    // VALID EVENT HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler() throws Exception {
        MethodSpec sourceHandler = createHandleEventMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createEventHandler(CountIncremented.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_EVENT);

        generation.addEventHandler(CountIncremented.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VALID EVENT HANDLER: EXAMPLE 2
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_example_2() throws Exception {
        MethodSpec sourceHandler = createHandleEventMethod()
                .addParameter(ExampleState.class, VAR_STATE)
                .addParameter(ExampleEvent.class, VAR_EVENT)
                .addStatement("return new $T()", ExampleState.class)
                .returns(ExampleState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create(ExampleState.class);

        MethodSpec.Builder generatedHandler = generation.createEventHandler(ExampleEvent.class, ExampleState.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_EVENT);

        generation.addEventHandler(ExampleEvent.class, generatedHandler, ExampleState.class);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VALID EVENT HANDLER: METHOD PARAMS SWITCHED
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_method_params_switched() throws Exception {
        MethodSpec sourceHandler = createHandleEventMethod()
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addParameter(CountState.class, VAR_STATE)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createEventHandler(CountIncremented.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_EVENT, VAR_STATE);

        generation.addEventHandler(CountIncremented.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VALID EVENT HANDLER NO STATE PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_no_state_param() throws Exception {
        MethodSpec sourceHandler = createHandleEventMethod()
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createEventHandler(CountIncremented.class)
                .addStatement("return $N.$N($N)", VAR_ORIGIN, METHOD_HANDLE, VAR_EVENT);

        generation.addEventHandler(CountIncremented.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VALID EVENT HANDLER NO PARAMS AND EVENT IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_no_params_and_event_in_annotation() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleEvent.class)
                .addMember(VAR_EVENT, "$T.class", CountIncremented.class)
                .build();

        MethodSpec sourceHandler = createHandlerMethod(annotation)
                .addStatement("return new $T(1)", CountState.class)
                .returns(CountState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createEventHandler(CountIncremented.class)
                .addStatement("return $N.$N()", VAR_ORIGIN, METHOD_HANDLE);

        generation.addEventHandler(CountIncremented.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VALID EVENT HANDLER STATE PARAM AND EVENT IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_state_param_and_event_in_annotation() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleEvent.class)
                .addMember(VAR_EVENT, "$T.class", CountIncremented.class)
                .build();

        MethodSpec sourceHandler = createHandlerMethod(annotation)
                .addParameter(CountState.class, VAR_STATE)
                .addStatement("return new $T(1)", CountState.class)
                .returns(CountState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createEventHandler(CountIncremented.class)
                .addStatement("return $N.$N($N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE);

        generation.addEventHandler(CountIncremented.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER DETERMINES CONCRETE STATE OF COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_determines_concrete_state_of_command_handler() throws Exception {
        MethodSpec handleCommand = createHandleCommandMethod()
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T($N.getCount())", CountIncremented.class, VAR_COMMAND)
                .returns(CountIncremented.class)
                .build();

        MethodSpec handleEvent = createHandleEventMethod()
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        /* ADD COMMAND HANDLER */

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(SyncCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder commandHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N)", VAR_ORIGIN, METHOD_HANDLE, VAR_COMMAND)
                .returns(CountIncremented.class);

        generation.addCommandHandler(IncrementCount.class, handlerType, commandHandler);

        /* ADD EVENT HANDLER */

        MethodSpec.Builder eventHandler = generation.createEventHandler(CountIncremented.class)
                .addStatement("return $N.$N($N)", VAR_ORIGIN, METHOD_HANDLE, VAR_EVENT);

        generation.addEventHandler(CountIncremented.class, eventHandler);

        assertCompileSuccess(createSource(handleCommand, handleEvent), generation.generateExpected());
    }

}
