package integration;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.MethodSpec;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.*;
import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
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
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleEventMethod()
                .addParameter(CountState.class, VAR_STATE)
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.event.handler.EventHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final EventHandler<CountState, CountIncremented> handler1 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle(state, event);",
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
                "        facade.mapEventHandler(CountIncremented.class, handler1);",
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
    // VALID EVENT HANDLER: METHOD PARAMS SWITCHED
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_method_params_switched() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleEventMethod()
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addParameter(CountState.class, VAR_STATE)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.event.handler.EventHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final EventHandler<CountState, CountIncremented> handler1 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle(event, state);",
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
                "        facade.mapEventHandler(CountIncremented.class, handler1);",
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
    // VALID EVENT HANDLER NO STATE PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_no_state_param() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        MethodSpec method = createHandleEventMethod()
                .addParameter(CountIncremented.class, VAR_EVENT)
                .addStatement("return new $T($N.getCount())", CountState.class, VAR_EVENT)
                .returns(CountState.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.event.handler.EventHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final EventHandler<CountState, CountIncremented> handler1 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle(event);",
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
                "        facade.mapEventHandler(CountIncremented.class, handler1);",
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
    // VALID EVENT HANDLER NO PARAMS AND EVENT IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_no_params_and_event_in_annotation() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        AnnotationSpec annotation = AnnotationSpec.builder(HandleEvent.class)
                .addMember(VAR_EVENT, "$T.class", CountIncremented.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addStatement("return new $T(1)", CountState.class)
                .returns(CountState.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.event.handler.EventHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final EventHandler<CountState, CountIncremented> handler1 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle();",
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
                "        facade.mapEventHandler(CountIncremented.class, handler1);",
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
    // VALID EVENT HANDLER STATE PARAM AND EVENT IN ANNOTATION
    //----------------------------------------------------------------------------------------------

    @Test
    public void valid_event_handler_state_param_and_event_in_annotation() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

        AnnotationSpec annotation = AnnotationSpec.builder(HandleEvent.class)
                .addMember(VAR_EVENT, "$T.class", CountIncremented.class)
                .build();

        MethodSpec method = createHandlerMethod(annotation)
                .addParameter(CountState.class, VAR_STATE)
                .addStatement("return new $T(1)", CountState.class)
                .returns(CountState.class)
                .build();

        JavaFileObject source = createSource(method);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.event.handler.EventHandler;",
                "import com.cookingfox.lapasse.api.facade.Facade;",
                "import com.cookingfox.lapasse.impl.internal.HandlerMapper;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.lang.Override;",
                "",
                "public class Test$$LaPasseGenerated<T extends Test> implements HandlerMapper {",
                "    final T origin;",
                "",
                "    final Facade<CountState> facade;",
                "",
                "    final EventHandler<CountState, CountIncremented> handler1 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle(state);",
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
                "        facade.mapEventHandler(CountIncremented.class, handler1);",
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
    // EVENT HANDLER DETERMINES CONCRETE STATE OF COMMAND HANDLER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_determines_concrete_state_of_command_handler() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasse.GENERATED_SUFFIX;

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

        JavaFileObject source = createSource(handleCommand, handleEvent);

        JavaFileObject expected = JavaFileObjects.forSourceLines(expectedFqcn,
                "// Generated code from LaPasse - do not modify!",
                "package test;",
                "",
                "import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;",
                "import com.cookingfox.lapasse.api.event.handler.EventHandler;",
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
                "            return origin.handle(command);",
                "        }",
                "    };",
                "",
                "    final EventHandler<CountState, CountIncremented> handler2 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle(event);",
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
                "        facade.mapEventHandler(CountIncremented.class, handler2);",
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
