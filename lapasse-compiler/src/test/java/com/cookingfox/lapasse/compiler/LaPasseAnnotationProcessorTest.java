package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.command.handler.SyncCommandHandler;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.*;
import fixtures.example.command.IncrementCount;
import fixtures.example.event.CountIncremented;
import fixtures.example.state.CountState;
import org.junit.Test;

import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Created by abeldebeer on 08/06/16.
 */
public class LaPasseAnnotationProcessorTest {

    @Test
    public void test() throws Exception {
        Class<CountState> stateClass = CountState.class;
        Class<IncrementCount> commandClass = IncrementCount.class;
        Class<CountIncremented> eventClass = CountIncremented.class;

        String packageName = "test";
        String sourceClassName = "Test";
        String sourceFqcn = String.format("%s.%s", packageName, sourceClassName);

        MethodSpec sourceCommandHandler = MethodSpec.methodBuilder("handle")
                .addAnnotation(HandleCommand.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(stateClass, "state")
                .addParameter(commandClass, "command")
                .addStatement("return new $T(command.getCount())", eventClass)
                .returns(eventClass)
                .build();

        MethodSpec sourceEventHandler = MethodSpec.methodBuilder("handle")
                .addAnnotation(HandleEvent.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(stateClass, "state")
                .addParameter(eventClass, "event")
                .addStatement("return new $T(state.getCount() + event.getCount())", stateClass)
                .returns(stateClass)
                .build();

        TypeSpec sourceType = TypeSpec.classBuilder(sourceClassName)
                .addMethod(sourceCommandHandler)
                .addMethod(sourceEventHandler)
                .addModifiers(Modifier.PUBLIC)
                .build();

        String sourceString = JavaFile.builder(packageName, sourceType).build().toString();
        JavaFileObject source = JavaFileObjects.forSourceString(sourceFqcn, sourceString);

//        System.out.println(sourceString);

        // >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>

        String expectedSuffix = "$$LaPasseGenerated";
        String expectedClassName = sourceClassName + expectedSuffix;
        String expectedFqcn = String.format("%s.%s", packageName, expectedClassName);

        TypeVariableName variableTarget = TypeVariableName.get("T", ClassName.get(packageName, sourceClassName));

        /* COMMAND HANDLER */

        TypeSpec expectedCommandHandler = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(SyncCommandHandler.class,
                        stateClass, commandClass, eventClass))
                .addMethod(MethodSpec.methodBuilder("handle")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(stateClass, "state")
                        .addParameter(commandClass, "command")
                        .returns(eventClass)
                        .addStatement("return $N.handle($N, $N)", "target", "state", "command")
                        .build())
                .build();

        FieldSpec expectedCommandHandlerField = FieldSpec.builder(
                ParameterizedTypeName.get(SyncCommandHandler.class,
                        stateClass, commandClass, eventClass), "_1")
                .addModifiers(Modifier.FINAL)
                .initializer("$L", expectedCommandHandler)
                .build();

        /* EVENT HANDLER */

        TypeSpec expectedEventHandler = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(ParameterizedTypeName.get(EventHandler.class,
                        stateClass, eventClass))
                .addMethod(MethodSpec.methodBuilder("handle")
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(stateClass, "state")
                        .addParameter(eventClass, "event")
                        .returns(stateClass)
                        .addStatement("return $N.handle($N, $N)", "target", "state", "event")
                        .build())
                .build();

        FieldSpec expectedEventHandlerField = FieldSpec.builder(ParameterizedTypeName.get(EventHandler.class,
                stateClass, eventClass), "_2")
                .addModifiers(Modifier.FINAL)
                .initializer("$L", expectedEventHandler)
                .build();

        /* CONSTRUCTOR */

        MethodSpec expectedConstructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(variableTarget, "target")
                .addParameter(Facade.class, "facade")
                .addStatement("this.$N = $N", "target", "target")
                .addStatement("$N.mapCommandHandler($T.class, _1)", "facade", commandClass)
                .addStatement("$N.mapEventHandler($T.class, _2)", "facade", eventClass)
                .build();

        TypeSpec expectedType = TypeSpec.classBuilder(expectedClassName)
                .addField(FieldSpec.builder(variableTarget, "target", Modifier.FINAL).build())
                .addField(expectedCommandHandlerField)
                .addField(expectedEventHandlerField)
                .addMethod(expectedConstructor)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(variableTarget)
                .build();

        String expectedString = JavaFile.builder(packageName, expectedType).build().toString();
        JavaFileObject expected = JavaFileObjects.forSourceString(expectedFqcn, expectedString);

        JavaFileObject testSource = JavaFileObjects.forSourceLines("test.Test",
                "package test;",
                "",
                "import com.cookingfox.lapasse.annotation.HandleCommand;",
                "import com.cookingfox.lapasse.annotation.HandleEvent;",
                "import com.cookingfox.lapasse.api.command.Command;",
                "import com.cookingfox.lapasse.api.event.Event;",
                "import com.cookingfox.lapasse.api.state.State;",
                "import com.cookingfox.lapasse.api.state.State;",
                "import fixtures.example.command.IncrementCount;",
                "import fixtures.example.event.CountIncremented;",
                "import fixtures.example.state.CountState;",
                "import java.util.Collection;",
                "import java.util.concurrent.Callable;",
                "",
                "public class Test {",
                "   @HandleCommand",
                "   public CountIncremented handle(CountState state, IncrementCount command) {",
                "       return null;",
                "   }",
                "   @HandleEvent",
                "   public CountState handle(CountState state, CountIncremented event) {",
                "       return null;",
                "   }",
                "}"
        );

        assertAbout(javaSource()).that(testSource)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError();

        if (true) {
            return;
        }

        assertAbout(javaSource()).that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .compilesWithoutError();
//                .and()
//                .generatesSources(expected);
    }

}
