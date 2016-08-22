package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

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

        JavaFileObject source = JavaFileObjects.forSourceLines(sourceFqcn,
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state, CountIncremented event) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "}"
        );

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

        JavaFileObject source = JavaFileObjects.forSourceLines(sourceFqcn,
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountIncremented event, CountState state) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "}"
        );

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

        JavaFileObject source = JavaFileObjects.forSourceLines(sourceFqcn,
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountIncremented event) {",
                "        return new CountState(event.getCount());",
                "    }",
                "}"
        );

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

        JavaFileObject source = JavaFileObjects.forSourceLines(sourceFqcn,
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent(event = CountIncremented.class)",
                "    public CountState handle() {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

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

        JavaFileObject source = JavaFileObjects.forSourceLines(sourceFqcn,
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent(event = CountIncremented.class)",
                "    public CountState handle(CountState state) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

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

        JavaFileObject source = JavaFileObjects.forSourceLines(sourceFqcn,
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public CountIncremented handle(IncrementCount command) {",
                "        return new CountIncremented(command.getCount());",
                "    }",
                "",
                "    @HandleEvent",
                "    public CountState handle(CountIncremented event) {",
                "        return new CountState(event.getCount());",
                "    }",
                "}"
        );

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
