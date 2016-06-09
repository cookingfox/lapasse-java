package com.cookingfox.lapasse.compiler;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by abeldebeer on 08/06/16.
 */
public class LaPasseAnnotationProcessorTest {

    @Test
    public void test() throws Exception {
        String sourceFqcn = "test.Test";
        String expectedFqcn = sourceFqcn + LaPasseAnnotationProcessor.CLASS_SUFFIX;

        //------------------------------------------------------------------------------------------
        // SOURCE
        //------------------------------------------------------------------------------------------

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
                "    public CountIncremented handle(CountState state, IncrementCount command) {",
                "        return new CountIncremented(command.getCount());",
                "    }",
                "",
                "    @HandleEvent",
                "    public CountState handle(CountState state, CountIncremented event) {",
                "        return new CountState(state.getCount() + event.getCount());",
                "    }",
                "}"
        );

        //------------------------------------------------------------------------------------------
        // EXPECTED
        //------------------------------------------------------------------------------------------

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
                "    final Facade facade;",
                "",
                "    final SyncCommandHandler<CountState, IncrementCount, CountIncremented> _1 = new SyncCommandHandler<CountState, IncrementCount, CountIncremented>() {",
                "        @Override",
                "        public CountIncremented handle(CountState state, IncrementCount command) {",
                "            return origin.handle(state, command);",
                "        }",
                "    };",
                "",
                "    final EventHandler<CountState, CountIncremented> _2 = new EventHandler<CountState, CountIncremented>() {",
                "        @Override",
                "        public CountState handle(CountState state, CountIncremented event) {",
                "            return origin.handle(state, event);",
                "        }",
                "    };",
                "",
                "    public Test$$LaPasseGenerated(T origin, Facade facade) {",
                "        this.origin = origin;",
                "        this.facade = facade;",
                "    }",
                "",
                "    @Override",
                "    public void mapHandlers() {",
                "        facade.mapCommandHandler(IncrementCount.class, _1);",
                "        facade.mapEventHandler(CountIncremented.class, _2);",
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
