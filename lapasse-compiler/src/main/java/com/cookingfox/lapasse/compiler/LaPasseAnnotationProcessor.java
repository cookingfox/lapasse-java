package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.compiler.command.HandleCommandInfo;
import com.cookingfox.lapasse.compiler.command.HandleCommandReturnType;
import com.cookingfox.lapasse.compiler.event.HandleEventInfo;
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

/**
 * Processes the annotations from the LaPasse library and generates Java code.
 */
public class LaPasseAnnotationProcessor extends AbstractProcessor {

    //----------------------------------------------------------------------------------------------
    // CONSTANTS
    //----------------------------------------------------------------------------------------------

    private static final String FIELD_PREFIX = "_";
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
        // map of enclosing element (class info) to handler registry
        final Map<TypeElement, Registry> map = new LinkedHashMap<>();

        // process `@HandleCommand` annotated methods
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleCommand.class)) {
            HandleCommandInfo info = new HandleCommandInfo(element);
            info.process();

            if (info.isValid()) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                getRegistry(map, enclosingElement).addHandleCommandInfo(info);
            } else {
                error(element, info.getError());
            }
        }

        // process `@HandleEvent` annotated methods
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleEvent.class)) {
            HandleEventInfo info = new HandleEventInfo(element);
            info.process();

            if (info.isValid()) {
                TypeElement enclosingElement = (TypeElement) element.getEnclosingElement();
                getRegistry(map, enclosingElement).addHandleEventInfo(info);
            } else {
                error(element, info.getError());
            }
        }

        for (Map.Entry<TypeElement, Registry> entry : map.entrySet()) {
            TypeElement origin = entry.getKey();
            Registry registry = entry.getValue();

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

            // create constructor
            MethodSpec constructor = MethodSpec.constructorBuilder()
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(originGeneric, VAR_ORIGIN)
                    .addParameter(Facade.class, VAR_FACADE)
                    .addStatement("this.$N = $N", VAR_ORIGIN, VAR_ORIGIN)
                    .addStatement("this.$N = $N", VAR_FACADE, VAR_FACADE)
                    .build();

            // create class (builder)
            TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC)
                    .addTypeVariable(originGeneric)
                    .addSuperinterface(HandlerMapper.class)
                    .addField(originGeneric, VAR_ORIGIN, Modifier.FINAL)
                    .addField(Facade.class, VAR_FACADE, Modifier.FINAL)
                    .addMethod(constructor);

            // create HandlerMapper (builder)
            MethodSpec.Builder mapHandlersBuilder = MethodSpec.methodBuilder("mapHandlers")
                    .addModifiers(Modifier.PUBLIC)
                    .addAnnotation(Override.class);

            int fieldNameCounter = 0;

            //--------------------------------------------------------------------------------------
            // PROCESS COMMAND HANDLERS
            //--------------------------------------------------------------------------------------

            for (HandleCommandInfo info : registry.getHandleCommands()) {
                String fieldName = FIELD_PREFIX + (++fieldNameCounter);

                // collect handler specific parameters
                Name methodName = info.getMethodName();
                TypeName stateName = info.getStateName();
                TypeName commandName = info.getCommandName();
                TypeName returnTypeName = info.getMethodReturnTypeName();

                // parameterized name for handler
                ParameterizedTypeName handlerType;

                // build handler method implementation
                MethodSpec.Builder handlerImplBuilder = MethodSpec.methodBuilder(METHOD_HANDLE)
                        .addAnnotation(Override.class)
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(stateName, VAR_STATE)
                        .addParameter(commandName, VAR_COMMAND)
                        .returns(returnTypeName);

                String handleStatement = "$N.$N($N, $N)";
                HandleCommandReturnType returnType = info.getReturnType();

                if (returnType.returnsVoid()) {
                    handlerType = ParameterizedTypeName.get(ClassName.get(VoidCommandHandler.class),
                            stateName, commandName);
                } else {
                    TypeName eventName = returnType.getEventName();

                    Class<? extends CommandHandler> commandHandlerClass = SyncCommandHandler.class;

                    if (returnType.returnsEventCollection()) {
                        commandHandlerClass = SyncMultiCommandHandler.class;
                    } else if (returnType.returnsEventCallable()) {
                        commandHandlerClass = AsyncCommandHandler.class;
                    } else if (returnType.returnsEventCollectionCallable()) {
                        commandHandlerClass = AsyncMultiCommandHandler.class;
                    } else if (returnType.returnsEventObservable()) {
                        commandHandlerClass = RxCommandHandler.class;
                    } else if (returnType.returnsEventCollectionObservable()) {
                        commandHandlerClass = RxMultiCommandHandler.class;
                    }

                    handlerType = ParameterizedTypeName.get(ClassName.get(commandHandlerClass),
                            stateName, commandName, eventName);

                    handleStatement = "return " + handleStatement;
                }

                handlerImplBuilder.addStatement(
                        handleStatement,
                        VAR_ORIGIN,
                        methodName,
                        VAR_STATE,
                        VAR_COMMAND
                );

                // add handler implementation
                TypeSpec handlerImpl = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(handlerType)
                        .addMethod(handlerImplBuilder.build())
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

            for (HandleEventInfo info : registry.getHandleEvents()) {
                String fieldName = FIELD_PREFIX + (++fieldNameCounter);

                // collect handler specific parameters
                Name methodName = info.getMethodName();
                TypeName stateName = info.getStateName();
                TypeName eventName = info.getEventName();

                // create parameterized name for handler
                ParameterizedTypeName handlerType = ParameterizedTypeName.get(
                        ClassName.get(EventHandler.class), stateName, eventName);

                // create anonymous handler implementation
                TypeSpec handlerImpl = TypeSpec.anonymousClassBuilder("")
                        .addSuperinterface(handlerType)
                        .addMethod(MethodSpec.methodBuilder(METHOD_HANDLE)
                                .addAnnotation(Override.class)
                                .addModifiers(Modifier.PUBLIC)
                                .addParameter(stateName, VAR_STATE)
                                .addParameter(eventName, VAR_EVENT)
                                .returns(stateName)
                                .addStatement(
                                        "return $N.$N($N, $N)",
                                        VAR_ORIGIN,
                                        methodName,
                                        VAR_STATE,
                                        VAR_EVENT
                                )
                                .build())
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
     */
    private void error(Element element, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);
    }

}
