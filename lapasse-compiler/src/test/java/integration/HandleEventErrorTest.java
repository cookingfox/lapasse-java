package integration;

import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

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
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    private CountState handle(CountState state, CountIncremented event) {",
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
    // EVENT HANDLER ANNOTATION ON FIELD
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_annotation_on_field() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountIncremented handle;",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Annotation can only be applied to a method");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER THROWS
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_throws() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state, CountIncremented event) throws Exception {",
                "        throw new Exception(\"example error\");",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Handler methods are not allowed to declare a `throws` clause.");
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
    // EVENT HANDLER NO METHOD OR ANNOTATION PARAMS
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_no_method_or_annotation_params() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle() {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Could not determine the target event type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER NO EVENT METHOD PARAM NO ANNOTATION PARAMS
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_no_event_method_param_no_annotation_params() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Could not determine the target event type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER EVENT TYPE NOT DETERMINABLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_event_type_not_determinable() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Could not determine the target event type");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS INVALID NUMBER
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_invalid_number() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state, CountIncremented event, Object foo) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS INVALID TYPES
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_both_invalid_types() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(Integer foo, String bar) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS EVENT AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_event_and_invalid_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountIncremented event, String foo) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER METHOD PARAMS STATE AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_method_params_state_and_invalid_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleEvent",
                "    public CountState handle(CountState state, String foo) {",
                "        return new CountState(0);",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Method parameters are invalid (expected State and Event");
    }

    //----------------------------------------------------------------------------------------------
    // EVENT HANDLER CONFLICT EVENT ANNOTATION METHOD PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void event_handler_conflict_annotation_method_event_param() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import fixtures.example2.event.ExampleEvent;",
                "",
                "public class Test {",
                "    @HandleEvent(event = ExampleEvent.class)",
                "    public CountState handle(CountState state, CountIncremented event) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Annotation parameter for event");
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
