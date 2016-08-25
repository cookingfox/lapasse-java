package integration;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.event.CountIncrementedCallable;
import fixtures.example.event.CountIncrementedCollectionCallable;
import fixtures.example.state.CountState;
import fixtures.example2.command.ExampleCommand;
import fixtures.example2.event.ExampleEvent;
import fixtures.example2.state.ExampleState;
import org.junit.Test;
import rx.Observable;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import static com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.*;
import static integration.IntegrationTestHelper.*;

/**
 * Integration tests for {@link LaPasseAnnotationProcessor} and successful {@link HandleCommand}
 * processor results.
 */
public class HandleCommandSuccessTest {

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER: DEFAULT
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_default() throws Exception {
        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("$N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND);

        generation.addVoidCommandHandler(IncrementCount.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER: METHOD PARAMS SWITCHED
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_method_params_switched() throws Exception {
        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addParameter(CountState.class, VAR_STATE)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("$N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_COMMAND, VAR_STATE);

        generation.addVoidCommandHandler(IncrementCount.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER - NO COMMAND & STATE PARAMS - COMMAND & STATE IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_no_command_state_params_command_state_in_annotation() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember(VAR_COMMAND, "$T.class", IncrementCount.class)
                .addMember(VAR_STATE, "$T.class", CountState.class)
                .build();

        MethodSpec sourceHandler = createHandlerMethod(annotation).build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("$N.$N()", VAR_ORIGIN, METHOD_HANDLE);

        generation.addVoidCommandHandler(IncrementCount.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER - NO COMMAND PARAM - COMMAND IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_no_command_param_command_in_annotation() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember(VAR_COMMAND, "$T.class", IncrementCount.class)
                .build();

        MethodSpec sourceHandler = createHandlerMethod(annotation)
                .addParameter(CountState.class, VAR_STATE)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("$N.$N($N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE);

        generation.addVoidCommandHandler(IncrementCount.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER - NO STATE PARAM - STATE IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_no_state_param_state_in_annotation() throws Exception {
        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember(VAR_STATE, "$T.class", CountState.class)
                .build();

        MethodSpec sourceHandler = createHandlerMethod(annotation)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("$N.$N($N)", VAR_ORIGIN, METHOD_HANDLE, VAR_COMMAND);

        generation.addVoidCommandHandler(IncrementCount.class, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // SYNC COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_command_handler() throws Exception {
        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T($N.getCount())", CountIncremented.class, VAR_COMMAND)
                .returns(CountIncremented.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(SyncCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(CountIncremented.class);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // SYNC COMMAND HANDLER EXAMPLE 2
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_command_handler_example_2() throws Exception {
        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(ExampleState.class, VAR_STATE)
                .addParameter(ExampleCommand.class, VAR_COMMAND)
                .addStatement("return new $T()", ExampleEvent.class)
                .returns(ExampleEvent.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create(ExampleState.class);

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(SyncCommandHandler.class,
                ExampleState.class, ExampleCommand.class, ExampleEvent.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(ExampleCommand.class, ExampleState.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(ExampleEvent.class);

        generation.addCommandHandler(ExampleCommand.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // SYNC COMMAND HANDLER: BASE EVENT TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_command_handler_base_event_type() throws Exception {
        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T($N.getCount())", CountIncremented.class, VAR_COMMAND)
                .returns(Event.class)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(SyncCommandHandler.class,
                CountState.class, IncrementCount.class, Event.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(Event.class);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // SYNC MULTI COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_multi_command_handler() throws Exception {
        ParameterizedTypeName returnType =
                ParameterizedTypeName.get(Collection.class, CountIncremented.class);

        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return $T.singleton(new $T($N.getCount()))",
                        Collections.class, CountIncremented.class, VAR_COMMAND)
                .returns(returnType)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(SyncMultiCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(returnType);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // ASYNC COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void async_command_handler() throws Exception {
        ParameterizedTypeName returnType =
                ParameterizedTypeName.get(Callable.class, CountIncremented.class);

        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T()", CountIncrementedCallable.class)
                .returns(returnType)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(AsyncCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(returnType);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // ASYNC MULTI COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void async_multi_command_handler() throws Exception {
        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Callable.class),
                ParameterizedTypeName.get(Collection.class, CountIncremented.class));

        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T()", CountIncrementedCollectionCallable.class)
                .returns(returnType)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(AsyncMultiCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(returnType);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // RX COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void rx_command_handler() throws Exception {
        ParameterizedTypeName returnType =
                ParameterizedTypeName.get(Observable.class, CountIncremented.class);

        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return $T.just(new $T($N.getCount()))",
                        Observable.class, CountIncremented.class, VAR_COMMAND)
                .returns(returnType)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(RxCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(returnType);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

    //----------------------------------------------------------------------------------------------
    // ASYNC MULTI COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void rx_multi_command_handler() throws Exception {
        ParameterizedTypeName returnType = ParameterizedTypeName.get(ClassName.get(Observable.class),
                ParameterizedTypeName.get(Collection.class, CountIncremented.class));

        MethodSpec sourceHandler = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                // note: requires casting of singleton result (Set) to Collection
                .addStatement("return $T.<$T<$T>>just($T.singleton(new $T($N.getCount())))",
                        Observable.class, Collection.class, CountIncremented.class,
                        Collections.class, CountIncremented.class, VAR_COMMAND)
                .returns(returnType)
                .build();

        TestGenerationModel generation = TestGenerationModel.create();

        ParameterizedTypeName handlerType = ParameterizedTypeName.get(RxMultiCommandHandler.class,
                CountState.class, IncrementCount.class, CountIncremented.class);

        MethodSpec.Builder generatedHandler = generation.createCommandHandler(IncrementCount.class)
                .addStatement("return $N.$N($N, $N)", VAR_ORIGIN, METHOD_HANDLE, VAR_STATE, VAR_COMMAND)
                .returns(returnType);

        generation.addCommandHandler(IncrementCount.class, handlerType, generatedHandler);

        assertCompileSuccess(createSource(sourceHandler), generation.generateExpected());
    }

}
