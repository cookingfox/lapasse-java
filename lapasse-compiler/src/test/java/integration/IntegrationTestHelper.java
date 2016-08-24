package integration;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.*;

import javax.tools.JavaFileObject;
import java.util.Arrays;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

/**
 * Static helper methods for integration tests.
 */
public final class IntegrationTestHelper {

    /**
     * Asserts that compilation of the provided `source` fails with `errorContaining`.
     *
     * @param source          The source to compile.
     * @param errorContaining The text that should be included in the error message.
     */
    public static void assertCompileFails(JavaFileObject source, String errorContaining) {
        assertAbout(javaSource())
                .that(source)
                .processedWith(new LaPasseAnnotationProcessor())
                .failsToCompile()
                .withErrorContaining(errorContaining);
    }

    /**
     * @return The spec builder for a {@link HandleCommand} annotated handler method.
     */
    public static MethodSpec.Builder createHandleCommandMethod() {
        return createHandlerMethod(AnnotationSpec.builder(HandleCommand.class).build());
    }

    /**
     * @return The spec builder for a {@link HandleEvent} annotated handler method.
     */
    public static MethodSpec.Builder createHandleEventMethod() {
        return createHandlerMethod(AnnotationSpec.builder(HandleEvent.class).build());
    }

    /**
     * Creates a builder for an annotated handler method spec.
     *
     * @param annotation The annotation to add to the method.
     * @return The method spec builder.
     */
    public static MethodSpec.Builder createHandlerMethod(AnnotationSpec annotation) {
        return MethodSpec.methodBuilder("handle")
                .addAnnotation(annotation);
    }

    /**
     * Creates the default type spec for a test class, adds the provided methods and builds a source
     * java file object.
     *
     * @param methods The methods to add to class.
     * @return The created java file object.
     */
    public static JavaFileObject createSource(MethodSpec... methods) {
        ClassName className = ClassName.get("test", "Test");

        TypeSpec type = TypeSpec.classBuilder(className)
                .addMethods(Arrays.asList(methods))
                .build();

        JavaFile javaFile = JavaFile.builder(className.packageName(), type).build();

        return JavaFileObjects.forSourceString(className.toString(), javaFile.toString());
    }

}
