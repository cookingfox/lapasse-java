package integration;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

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
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
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
                .withErrorContaining("Method is not accessible");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS INVALID NUMBER
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_invalid_number() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle(CountState state, IncrementCount command, String foo) {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid parameters - expected command and state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS BOTH INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_both_invalid_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle(String foo, Object bar) {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid parameters - expected command and state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS COMMAND AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_command_and_invalid_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle(IncrementCount command, String foo) {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid parameters - expected command and state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER METHOD PARAMS STATE AND INVALID TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_method_params_state_and_invalid_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle(CountState state, String foo) {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid parameters - expected command and state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE NOT DECLARED
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_not_declared() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public FooBarBaz handle(CountState state, IncrementCount command) {",
                "        return true;",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public Boolean handle(CountState state, IncrementCount command) {",
                "        return true;",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID CALLABLE TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_callable_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.util.Arrays;",
                "import java.util.concurrent.Callable;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public Callable<String> handle(CountState state, final IncrementCount command) {",
                "        return null;",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID OBSERVABLE TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_observable_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.util.Arrays;",
                "import rx.Observable;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public Observable<String> handle(CountState state, final IncrementCount command) {",
                "        return null;",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID CALLABLE COLLECTION TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_callable_collection_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.util.Arrays;",
                "import java.util.Collection;",
                "import java.util.concurrent.Callable;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public Callable<Collection<String>> handle(CountState state, final IncrementCount command) {",
                "        return null;",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER RETURN TYPE INVALID OBSERVABLE COLLECTION TYPE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_return_type_invalid_observable_collection_type() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "import java.util.Arrays;",
                "import java.util.Collection;",
                "import rx.Observable;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public Observable<Collection<String>> handle(CountState state, final IncrementCount command) {",
                "        return null;",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid return type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER NO METHOD OR ANNOTATION PARAMS
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_no_method_or_annotation_params() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle() {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Could not determine command type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER STATE NOT DETERMINABLE
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_state_not_determinable() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle(IncrementCount command) {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Can not determine target state");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER COMMAND NOT DETERMINABLE ONLY STATE METHOD PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_command_not_determinable_only_state_method_param() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.state.CountState;",
                "",
                "public class Test {",
                "    @HandleCommand",
                "    public void handle(CountState state) {",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Could not determine command type");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER CONFLICT ANNOTATION METHOD COMMAND PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_conflict_annotation_method_command_param() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import fixtures.example2.command.ExampleCommand;",
                "",
                "public class Test {",
                "    @HandleCommand(command = ExampleCommand.class)",
                "    public CountIncremented handle(CountState state, IncrementCount command) {",
                "        return new CountIncremented(command.getCount());",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Annotation parameter for command");
    }

    //----------------------------------------------------------------------------------------------
    // COMMAND HANDLER CONFLICT ANNOTATION METHOD STATE PARAM
    //----------------------------------------------------------------------------------------------

    @Test
    public void command_handler_conflict_annotation_method_state_param() throws Exception {
        JavaFileObject source = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import fixtures.example2.state.ExampleState;",
                "",
                "public class Test {",
                "    @HandleCommand(state = ExampleState.class)",
                "    public CountIncremented handle(CountState state, IncrementCount command) {",
                "        return new CountIncremented(command.getCount());",
                "    }",
                "}"
        );

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining("Annotation parameter for state");
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

}
