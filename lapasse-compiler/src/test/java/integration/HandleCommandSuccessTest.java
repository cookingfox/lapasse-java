package integration;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.event.CountIncrementedCallable;
import fixtures.example.event.CountIncrementedCollectionCallable;
import fixtures.example.state.CountState;
import org.junit.Test;
import rx.Observable;

import javax.tools.JavaFileObject;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;

import static com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.VAR_COMMAND;
import static com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.VAR_STATE;
import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
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
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final VoidCommandHandler<CountState, IncrementCount> handler1 = new VoidCommandHandler<CountState, IncrementCount>() {",
                "        @Override",
                "        public void handle(CountState state, IncrementCount command) {",
                "            origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER: METHOD PARAMS SWITCHED
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_method_params_switched() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addParameter(CountState.class, VAR_STATE)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final VoidCommandHandler<CountState, IncrementCount> handler1 = new VoidCommandHandler<CountState, IncrementCount>() {",
                "        @Override",
                "        public void handle(CountState state, IncrementCount command) {",
                "            origin.handle(command, state);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER - NO COMMAND & STATE PARAMS - COMMAND & STATE IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_no_command_state_params_command_state_in_annotation() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember(VAR_COMMAND, "$T.class", IncrementCount.class)
                .addMember(VAR_STATE, "$T.class", CountState.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final VoidCommandHandler<CountState, IncrementCount> handler1 = new VoidCommandHandler<CountState, IncrementCount>() {",
                "        @Override",
                "        public void handle(CountState state, IncrementCount command) {",
                "            origin.handle();",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER - NO COMMAND PARAM - COMMAND IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_no_command_param_command_in_annotation() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember(VAR_COMMAND, "$T.class", IncrementCount.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addParameter(CountState.class, VAR_STATE)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final VoidCommandHandler<CountState, IncrementCount> handler1 = new VoidCommandHandler<CountState, IncrementCount>() {",
                "        @Override",
                "        public void handle(CountState state, IncrementCount command) {",
                "            origin.handle(state);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // VOID COMMAND HANDLER - NO STATE PARAM - STATE IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void void_command_handler_no_state_param_state_in_annotation() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        AnnotationSpec annotation = AnnotationSpec.builder(HandleCommand.class)
                .addMember(VAR_STATE, "$T.class", CountState.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final VoidCommandHandler<CountState, IncrementCount> handler1 = new VoidCommandHandler<CountState, IncrementCount>() {",
                "        @Override",
                "        public void handle(CountState state, IncrementCount command) {",
                "            origin.handle(command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // SYNC COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T($N.getCount())", CountIncremented.class, VAR_COMMAND)
                .returns(CountIncremented.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final SyncCommandHandler<CountState, IncrementCount, CountIncremented> handler1 = new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public CountIncremented handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // SYNC COMMAND HANDLER: BASE EVENT TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_command_handler_base_event_type() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T($N.getCount())", CountIncremented.class, VAR_COMMAND)
                .returns(Event.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;",
                "import com.cookingfox.lapasse.api.event.Event;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final SyncCommandHandler<CountState, IncrementCount, Event> handler1 = new SyncCommandHandler<CountState, IncrementCount, Event>() {",
                "        @Override",
                "        public Event handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // SYNC MULTI COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void sync_multi_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return $T.singleton(new $T($N.getCount()))",
                        Collections.class, CountIncremented.class, VAR_COMMAND)
                .returns(ParameterizedTypeName.get(Collection.class, CountIncremented.class))
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.SyncMultiCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "import java.util.Collection;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final SyncMultiCommandHandler<CountState, IncrementCount, CountIncremented> handler1 = new SyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public Collection<CountIncremented> handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // ASYNC COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void async_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T()", CountIncrementedCallable.class)
                .returns(ParameterizedTypeName.get(Callable.class, CountIncremented.class))
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.AsyncCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "import java.util.concurrent.Callable;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final AsyncCommandHandler<CountState, IncrementCount, CountIncremented> handler1 = new AsyncCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public Callable<CountIncremented> handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // ASYNC MULTI COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void async_multi_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return new $T()", CountIncrementedCollectionCallable.class)
                .returns(ParameterizedTypeName.get(ClassName.get(Callable.class),
                        ParameterizedTypeName.get(Collection.class, CountIncremented.class)))
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.AsyncMultiCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "import java.util.Collection;",
                "import java.util.concurrent.Callable;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final AsyncMultiCommandHandler<CountState, IncrementCount, CountIncremented> handler1 = new AsyncMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public Callable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // RX COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void rx_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                .addStatement("return $T.just(new $T($N.getCount()))",
                        Observable.class, CountIncremented.class, VAR_COMMAND)
                .returns(ParameterizedTypeName.get(Observable.class, CountIncremented.class))
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.RxCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "import rx.Observable;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final RxCommandHandler<CountState, IncrementCount, CountIncremented> handler1 = new RxCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public Observable<CountIncremented> handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

    //----------------------------------------------------------------------------------------------
    // ASYNC MULTI COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void rx_multi_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleCommandMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(IncrementCount.class, VAR_COMMAND)
                // note: requires casting of singleton result (Set) to Collection
                .addStatement("return $T.<$T<$T>>just($T.singleton(new $T($N.getCount())))",
                        Observable.class, Collection.class, CountIncremented.class,
                        Collections.class, CountIncremented.class, VAR_COMMAND)
                .returns(ParameterizedTypeName.get(ClassName.get(Observable.class),
                        ParameterizedTypeName.get(Collection.class, CountIncremented.class)))
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.RxMultiCommandHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "import java.util.Collection;",
                "import rx.Observable;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final RxMultiCommandHandler<CountState, IncrementCount, CountIncremented> handler1 = new RxMultiCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public Observable<Collection<CountIncremented>> handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade<CountState> facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, handler1);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expected);
    }

}
