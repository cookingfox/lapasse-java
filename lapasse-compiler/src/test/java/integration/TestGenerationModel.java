package integration;

import com.cookingfox.lapasse.api.command.Command;
import com.cookingfox.lapasse.api.command.handler.VoidCommandHandler;
import com.cookingfox.lapasse.api.event.Event;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.message.Message;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;
import com.google.testing.compile.JavaFileObjects;
import com.squareup.javapoet.*;
import fixtures.example.state.CountState;

import javax.lang.model.element.Modifier;
import javax.tools.JavaFileObject;

import static com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor.*;
import static integration.IntegrationTestHelper.TEST_CLASS;
import static integration.IntegrationTestHelper.TEST_PACKAGE;

/**
 * Helper class for integration tests, specifically generation of expected code after processing.
 */
public final class TestGenerationModel {

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    int fieldNameCounter = 0;
    final MethodSpec.Builder mapHandlersBuilder;
    final TypeSpec.Builder typeSpecBuilder;

    //----------------------------------------------------------------------------------------------
    // CONSTRUCTOR
    //----------------------------------------------------------------------------------------------

    public TestGenerationModel(MethodSpec.Builder mapHandlersBuilder, TypeSpec.Builder typeSpecBuilder) {
        this.mapHandlersBuilder = mapHandlersBuilder;
        this.typeSpecBuilder = typeSpecBuilder;
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Add a command handler with the provided method builder.
     *
     * @param commandType          The concrete command type that will be handled.
     * @param handlerType          The concrete command handler type.
     * @param handlerMethodBuilder A builder for the handler method implementation.
     */
    public void addCommandHandler(Class<? extends Command> commandType,
                                  ParameterizedTypeName handlerType,
                                  MethodSpec.Builder handlerMethodBuilder) {
        addMessageHandler(commandType, handlerType, handlerMethodBuilder, METHOD_MAP_COMMAND_HANDLER);
    }

    /**
     * Add an event handler with the provided method builder.
     *
     * @param eventType            The concrete event type that will be handled.
     * @param handlerMethodBuilder A builder for the handler method implementation.
     */
    public void addEventHandler(Class<? extends Event> eventType,
                                MethodSpec.Builder handlerMethodBuilder) {
        addEventHandler(eventType, handlerMethodBuilder, CountState.class);
    }

    /**
     * Add an event handler with the provided method builder.
     *
     * @param eventType            The concrete event type that will be handled.
     * @param handlerMethodBuilder A builder for the handler method implementation.
     * @param stateType            The concrete state type that will be handled.
     */
    public void addEventHandler(Class<? extends Event> eventType,
                                MethodSpec.Builder handlerMethodBuilder,
                                Class<? extends State> stateType) {
        ParameterizedTypeName handlerType = ParameterizedTypeName.get(EventHandler.class,
                stateType, eventType);

        addMessageHandler(eventType, handlerType, handlerMethodBuilder, METHOD_MAP_EVENT_HANDLER);
    }

    /**
     * Add a message handler with the provided method builder.
     *
     * @param messageType          The concrete message type that will be handled.
     * @param handlerType          The concrete handler type.
     * @param handlerMethodBuilder A builder for the handler method implementation.
     * @param mapHandlerStatement  The concrete call to map the handler.
     */
    public void addMessageHandler(Class<? extends Message> messageType,
                                  ParameterizedTypeName handlerType,
                                  MethodSpec.Builder handlerMethodBuilder,
                                  String mapHandlerStatement) {
        // create field name
        String fieldName = FIELD_PREFIX + (++fieldNameCounter);

        // add handler implementation
        TypeSpec handlerImpl = TypeSpec.anonymousClassBuilder("")
                .addSuperinterface(handlerType)
                .addMethod(handlerMethodBuilder.build())
                .build();

        // create field for handler
        FieldSpec handlerField = FieldSpec.builder(handlerType, fieldName)
                .addModifiers(Modifier.FINAL)
                .initializer("$L", handlerImpl)
                .build();

        // add field to class
        typeSpecBuilder.addField(handlerField);

        // add map handler statement
        mapHandlersBuilder.addStatement("$N.$N($T.class, $N)", VAR_FACADE, mapHandlerStatement,
                messageType, fieldName);
    }

    /**
     * Adds a void (no return type) command handler with the provided method builder.
     *
     * @param commandType          The concrete command type that will be handled.
     * @param handlerMethodBuilder A builder for the handler method implementation.
     */
    public void addVoidCommandHandler(Class<? extends Command> commandType,
                                      MethodSpec.Builder handlerMethodBuilder) {
        ParameterizedTypeName handlerType = ParameterizedTypeName.get(VoidCommandHandler.class,
                CountState.class, commandType);

        addCommandHandler(commandType, handlerType, handlerMethodBuilder);
    }

    /**
     * Creates a method builder for the default command handler implementation.
     *
     * @param commandType The command type for this handler.
     * @return The created method builder.
     */
    public MethodSpec.Builder createCommandHandler(Class<? extends Command> commandType) {
        return createCommandHandler(commandType, CountState.class);
    }

    /**
     * Creates a method builder for the default command handler implementation.
     *
     * @param commandType The command type for this handler.
     * @param stateType   The state type for this handler.
     * @return The created method builder.
     */
    public MethodSpec.Builder createCommandHandler(Class<? extends Command> commandType,
                                                   Class<? extends State> stateType) {
        return MethodSpec.methodBuilder(METHOD_HANDLE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(stateType, VAR_STATE)
                .addParameter(commandType, VAR_COMMAND);
    }

    /**
     * Creates a method builder for the default event handler implementation.
     *
     * @param eventType The event type for this handler.
     * @return The created method builder.
     */
    public MethodSpec.Builder createEventHandler(Class<? extends Event> eventType) {
        return createEventHandler(eventType, CountState.class);
    }

    /**
     * Creates a method builder for the default event handler implementation.
     *
     * @param eventType The event type for this handler.
     * @param stateType The state type for this handler.
     * @return The created method builder.
     */
    public MethodSpec.Builder createEventHandler(Class<? extends Event> eventType,
                                                 Class<? extends State> stateType) {
        return MethodSpec.methodBuilder(METHOD_HANDLE)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(stateType, VAR_STATE)
                .addParameter(eventType, VAR_EVENT)
                .returns(stateType);
    }

    /**
     * @return The expected generated java file.
     */
    public JavaFileObject generateExpected() {
        typeSpecBuilder.addMethod(mapHandlersBuilder.build());

        ClassName generatedClassName = ClassName.get(TEST_PACKAGE,
                TEST_CLASS + LaPasse.GENERATED_SUFFIX);

        JavaFile javaFile = JavaFile.builder(generatedClassName.packageName(),
                typeSpecBuilder.build())
                .addFileComment(FILE_COMMENT)
                .build();

        return JavaFileObjects.forSourceString(generatedClassName.toString(),
                javaFile.toString());
    }

    //----------------------------------------------------------------------------------------------
    // PUBLIC STATIC METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * @return A new model that is ready to have handler implementations added.
     */
    public static TestGenerationModel create() {
        return create(CountState.class);
    }

    /**
     * Create a new model that is ready to have handler implementations added.
     *
     * @param stateType The concrete state type that is used by this model's facade.
     * @return A new model that is ready to have handler implementations added.
     */
    public static TestGenerationModel create(Class<? extends State> stateType) {
        ClassName sourceClassName = ClassName.get(TEST_PACKAGE, TEST_CLASS);
        ClassName generatedClassName = ClassName.get(TEST_PACKAGE, TEST_CLASS + LaPasse.GENERATED_SUFFIX);

        // create generic type parameter for origin
        TypeVariableName originGeneric = TypeVariableName.get("T", sourceClassName);

        // create facade type with concrete state generic
        ParameterizedTypeName facadeConcrete = ParameterizedTypeName.get(Facade.class, stateType);

        // create constructor
        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)
                .addParameter(originGeneric, VAR_ORIGIN)
                .addParameter(facadeConcrete, VAR_FACADE)
                .addStatement("this.$N = $N", VAR_ORIGIN, VAR_ORIGIN)
                .addStatement("this.$N = $N", VAR_FACADE, VAR_FACADE)
                .build();

        // create class (builder)
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(generatedClassName)
                .addModifiers(Modifier.PUBLIC)
                .addTypeVariable(originGeneric)
                .addSuperinterface(HandlerMapper.class)
                .addField(originGeneric, VAR_ORIGIN, Modifier.FINAL)
                .addField(facadeConcrete, VAR_FACADE, Modifier.FINAL)
                .addMethod(constructor);

        MethodSpec.Builder mapHandlersBuilder = MethodSpec.methodBuilder(METHOD_MAP_HANDLERS)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class);

        return new TestGenerationModel(mapHandlersBuilder, typeSpecBuilder);
    }

}
