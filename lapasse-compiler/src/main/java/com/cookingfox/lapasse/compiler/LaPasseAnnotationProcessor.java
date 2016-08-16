package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.compiler.processor.ProcessorResults;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandMethodType;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandProcessor;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandResult;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandReturnType;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventProcessor;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventResult;
import com.cookingfox.lapasse.impl.helper.LaPasse;
import com.cookingfox.lapasse.impl.internal.HandlerMapper;
import com.squareup.javapoet.*;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.io.IOException;
import java.util.*;

import static java.util.Objects.requireNonNull;

/**
 * Processes the annotations from the LaPasse library and generates Java code.
 */
public class LaPasseAnnotationProcessor extends AbstractProcessor {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    private static final String FIELD_PREFIX = "handler";
    private static final String METHOD_HANDLE = "handle";
    private static final String VAR_COMMAND = "command";
    private static final String VAR_EVENT = "event";
    private static final String VAR_FACADE = "facade";
    private static final String VAR_ORIGIN = "origin";
    private static final String VAR_STATE = "state";

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    private Elements elements;
    private Filer filer;
    private Messager messager;

    //----------------------------------------------------------------------------------------------
    // PUBLIC METHODS
    //----------------------------------------------------------------------------------------------

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new HashSet<>(Arrays.asList(
                HandleCommand.class.getCanonicalName(),
                HandleEvent.class.getCanonicalName()
        ));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elements = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Map<TypeElement, ProcessorResults> map2 = new LinkedHashMap<>();

        // process `@HandleCommand` annotated methods
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleCommand.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            HandleCommandProcessor processor = new HandleCommandProcessor(element);

            try {
                processor.process();

                ProcessorResults processorResults = getProcessorResults(map2, enclosingElement);
                processorResults.addHandleCommandResult(processor.getResult());
            } catch (Exception e) {
                return error(enclosingElement, e.getMessage());
            }
        }

        // process `@HandleEvent` annotated methods
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleEvent.class)) {
            TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
            HandleEventProcessor processor = new HandleEventProcessor(element);

            try {
                processor.process();

                ProcessorResults processorResults = getProcessorResults(map2, enclosingElement);
                processorResults.addHandleEventResult(processor.getResult());
            } catch (Exception e) {
                return error(enclosingElement, e.getMessage());
            }
        }

        for (Map.Entry<TypeElement, ProcessorResults> entry : map2.entrySet()) {
            TypeElement origin = entry.getKey();
            ProcessorResults processorResults = entry.getValue();
            TypeName targetStateName = processorResults.getTargetStateName();

            // get original package and class name
            String packageName = elements.getPackageOf(origin).getQualifiedName().toString();
            String originClassName = getClassName(origin, packageName);

            // create new class name
            String generatedClassName = originClassName + LaPasse.GENERATED_SUFFIX;
            ClassName className = ClassName.get(packageName, generatedClassName);

            //--------------------------------------------------------------------------------------
            // CREATE CLASS SPECIFICATION
            //--------------------------------------------------------------------------------------

            // create generic type parameter for origin
            TypeVariableName originGeneric = TypeVariableName.get("T",
                    ClassName.get(packageName, originClassName));

            // create facade type with concrete state generic
            ParameterizedTypeName facadeConcrete =
                    ParameterizedTypeName.get(ClassName.get(Facade.class), targetStateName);

            // create constructor
            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(originGeneric, VAR_ORIGIN)
                    .addParameter(facadeConcrete, VAR_FACADE)
                    .addStatement("this.$N = $N", VAR_ORIGIN, VAR_ORIGIN)
                    .addStatement("this.$N = $N", VAR_FACADE, VAR_FACADE)
                    .build();

            // create class (builder)
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(originGeneric)
                    .addSuperinterface(HandlerMapper.class)
                    .addField(originGeneric, VAR_ORIGIN, Modifier.FINAL)
                    .addField(facadeConcrete, VAR_FACADE, Modifier.FINAL)
                    .addMethod(constructor);

            // create HandlerMapper (builder)
            MethodSpec.Builder mapHandlersBuilder = MethodSpec.methodBuilder("mapHandlers")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            int fieldNameCounter = 0;

            //--------------------------------------------------------------------------------------
            // PROCESS COMMAND HANDLERS
            //--------------------------------------------------------------------------------------

            for (HandleCommandResult result : processorResults.getHandleCommandResults()) {
                String fieldName = FIELD_PREFIX + (++fieldNameCounter);

                // collect handler specific parameters
                Name methodName = result.getMethodName();
                TypeName stateName = ClassName.get(result.getStateType());
                TypeName commandName = ClassName.get(result.getCommandType());
                TypeName eventTypeName = result.getEventTypeName();
                HandleCommandMethodType methodType = result.getMethodType();
                HandleCommandReturnType returnType = result.getReturnType();
                TypeName returnTypeName = ClassName.get(result.getReturnTypeName());

                ParameterizedTypeName handlerType;

                // build handler method
                MethodSpec.Builder handlerMethodBuilder = MethodSpec.methodBuilder(METHOD_HANDLE)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(stateName, VAR_STATE)
                        .addParameter(commandName, VAR_COMMAND)
                        .returns(returnTypeName);

                String callerStatement = "";

                if (returnType == HandleCommandReturnType.RETURNS_VOID) {
                    handlerType = ParameterizedTypeName.get(ClassName.get(VoidCommandHandler.class),
                            stateName, commandName);
                } else {
                    callerStatement = "return ";

                    // default: returns event
                    Class<? extends CommandHandler> commandHandlerClass = SyncCommandHandler.class;

                    switch (returnType) {
                        case RETURNS_EVENT_COLLECTION:
                            commandHandlerClass = SyncMultiCommandHandler.class;
                            break;

                        case RETURNS_EVENT_CALLABLE:
                            commandHandlerClass = AsyncCommandHandler.class;
                            break;

                        case RETURNS_EVENT_COLLECTION_CALLABLE:
                            commandHandlerClass = AsyncMultiCommandHandler.class;
                            break;

                        case RETURNS_EVENT_OBSERVABLE:
                            commandHandlerClass = RxCommandHandler.class;
                            break;

                        case RETURNS_EVENT_COLLECTION_OBSERVABLE:
                            commandHandlerClass = RxMultiCommandHandler.class;
                            break;
                    }

                    handlerType = ParameterizedTypeName.get(ClassName.get(commandHandlerClass),
                            stateName, commandName, eventTypeName);
                }

                switch (methodType) {
                    case METHOD_NO_PARAMS:
                        callerStatement += "$N.$N()";
                        handlerMethodBuilder.addStatement(callerStatement,
                                VAR_ORIGIN, methodName);
                        break;

                    case METHOD_ONE_PARAM_COMMAND:
                        callerStatement += "$N.$N($N)";
                        handlerMethodBuilder.addStatement(callerStatement,
                                VAR_ORIGIN, methodName, VAR_COMMAND);
                        break;

                    case METHOD_TWO_PARAMS_COMMAND_STATE:
                        callerStatement += "$N.$N($N, $N)";
                        handlerMethodBuilder.addStatement(callerStatement,
                                VAR_ORIGIN, methodName, VAR_COMMAND, VAR_STATE);
                        break;

                    case METHOD_TWO_PARAMS_STATE_COMMAND:
                        callerStatement += "$N.$N($N, $N)";
                        handlerMethodBuilder.addStatement(callerStatement,
                                VAR_ORIGIN, methodName, VAR_STATE, VAR_COMMAND);
                        break;
                }

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

                // add handler mapping to HandlerMapper
                mapHandlersBuilder.addStatement(
                        "$N.mapCommandHandler($T.class, $N)",
                        VAR_FACADE,
                        commandName,
                        fieldName
                );
            }

            //--------------------------------------------------------------------------------------
            // PROCESS EVENT HANDLERS
            //--------------------------------------------------------------------------------------

            for (HandleEventResult result : processorResults.getHandleEventResults()) {
                String fieldName = FIELD_PREFIX + (++fieldNameCounter);

                // collect handler specific parameters
                Name methodName = result.getMethodName();
                TypeName stateName = ClassName.get(result.getStateType());
                TypeName eventName = ClassName.get(result.getEventType());

                // build handler method
                MethodSpec.Builder handlerMethodBuilder = MethodSpec.methodBuilder(METHOD_HANDLE)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(stateName, VAR_STATE)
                        .addParameter(eventName, VAR_EVENT);

                switch (result.getMethodType()) {
                    case METHOD_NO_PARAMS:
                        handlerMethodBuilder.addStatement("return $N.$N()",
                                VAR_ORIGIN, methodName);
                        break;

                    case METHOD_ONE_PARAM_EVENT:
                        handlerMethodBuilder.addStatement("return $N.$N($N)",
                                VAR_ORIGIN, methodName, VAR_EVENT);
                        break;

                    case METHOD_TWO_PARAMS_EVENT_STATE:
                        handlerMethodBuilder.addStatement("return $N.$N($N, $N)",
                                VAR_ORIGIN, methodName, VAR_EVENT, VAR_STATE);
                        break;

                    case METHOD_TWO_PARAMS_STATE_EVENT:
                        handlerMethodBuilder.addStatement("return $N.$N($N, $N)",
                                VAR_ORIGIN, methodName, VAR_STATE, VAR_EVENT);
                        break;
                }

                MethodSpec handlerMethod = handlerMethodBuilder.returns(stateName)
                        .build();

                // create parameterized name for handler
                ParameterizedTypeName handlerType = ParameterizedTypeName.get(
                        ClassName.get(EventHandler.class), stateName, eventName);

                // create anonymous handler implementation
                TypeSpec handlerImpl = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(handlerType)
                        .addMethod(handlerMethod)
                        .build();

                // create field for handler
                FieldSpec handlerField = FieldSpec.builder(handlerType, fieldName)
                        .addModifiers(Modifier.FINAL)
                        .initializer("$L", handlerImpl)
                        .build();

                // add field to class
                typeSpecBuilder.addField(handlerField);

                // add handler mapping to HandlerMapper
                mapHandlersBuilder.addStatement(
                        "$N.mapEventHandler($T.class, $N)",
                        VAR_FACADE,
                        eventName,
                        fieldName
                );
            }

            //--------------------------------------------------------------------------------------
            // BUILD JAVA FILE
            //--------------------------------------------------------------------------------------

            // add populated HandlerMapper method to class
            typeSpecBuilder.addMethod(mapHandlersBuilder.build());

            try {
                // create java file from type spec
                JavaFile javaFile = JavaFile.builder(packageName, typeSpecBuilder.build())
                        .addFileComment("Generated code from LaPasse - do not modify!")
                        .build();

                // write file
                javaFile.writeTo(filer);
            } catch (IOException e) {
                error(origin, "Unable to generate handlers for %s: %s", origin, e.getMessage());
            }
        }

        return false;
    }

    private ProcessorResults getProcessorResults(Map<TypeElement, ProcessorResults> map, TypeElement element) {
        ProcessorResults processorResults = map.get(requireNonNull(element));

        if (processorResults == null) {
            processorResults = new ProcessorResults();
            map.put(element, processorResults);
        }

        return processorResults;
    }

    //----------------------------------------------------------------------------------------------
    // PRIVATE METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Returns the registry for the provided type and creates a new one if it doesn't exist yet.
     *
     * @param map              The map containing the registries.
     * @param enclosingElement The element to return the registry for.
     * @return The registry.
     */
    private Registry getRegistry(Map<TypeElement, Registry> map, TypeElement enclosingElement) {
        Registry registry = map.get(enclosingElement);

        if (registry == null) {
            registry = new Registry();
            map.put(enclosingElement, registry);
        }

        return registry;
    }

    /**
     * Generates a class name using the provided type and package name.
     *
     * @param type        The type element to extract the class name from.
     * @param packageName The name of the type's package.
     * @return The class name.
     */
    private String getClassName(TypeElement type, String packageName) {
        return type.getQualifiedName()
                .toString()
                .substring(packageName.length() + 1)
                .replace('.', '$');
    }

    /**
     * Print an error message.
     *
     * @param element The element that could not be processed.
     * @param msg     The error message.
     * @param args    Arguments to parse in the message.
     * @return False, so it is easier to exit the processing process.
     */
    private boolean error(Element element, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);

        return false;
    }

}
