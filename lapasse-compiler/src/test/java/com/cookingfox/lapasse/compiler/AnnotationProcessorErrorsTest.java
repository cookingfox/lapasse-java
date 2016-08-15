package com.cookingfox.lapasse.compiler;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Integration tests for {@link LaPasseAnnotationProcessor} with scenarios which produce errors.
 */
public class AnnotationProcessorErrorsTest {

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD NOT ACCESSIBLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_not_accessible() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import fixtures.example2.command.ExampleCommand;",
                "import fixtures.example2.event.ExampleEvent;",
                "import fixtures.example2.state.ExampleState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    private CountIncremented handle(CountState state, IncrementCount command) {",
                "        return new CountIncremented(command.getCount());",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("has private access");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLERS TARGET STATE CONFLICT
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handlers_target_state_conflict() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import fixtures.example2.command.ExampleCommand;",
                "import fixtures.example2.event.ExampleEvent;",
                "import fixtures.example2.state.ExampleState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public CountIncremented handle(CountState state, IncrementCount command) {",
                "        return new CountIncremented(command.getCount());",
                "    }",
                "",
                "    @HandleCommand",
                "    public ExampleEvent handle(ExampleState state, ExampleCommand command) {",
                "        return new ExampleEvent();",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Mapped command handler does not match expected concrete State");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD NOT ACCESSIBLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_not_accessible() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    private State handle(CountState state, CountIncremented event) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Method is not accessible");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER RETURN TYPE DOES NOT EXTEND STATE (raw State type)
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_return_type_does_not_extend_state() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public State handle(CountState state, CountIncremented event) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Return type of @HandleEvent annotated method must extend");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLERS TARGET STATE CONFLICT
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handlers_target_state_conflict() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import fixtures.example2.event.ExampleEvent;",
                "import fixtures.example2.state.ExampleState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state, CountIncremented event) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "",
                "    @HandleEvent",
                "    public ExampleState handle(ExampleState state, ExampleEvent event) {",
                "        return new ExampleState();",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Mapped event handler does not match expected concrete State");
    }

}
