package com.cookingfox.lapasse.compiler;

import com.cookingfox.lapasse.annotation.HandleCommand;
import com.cookingfox.lapasse.annotation.HandleEvent;
import com.cookingfox.lapasse.api.command.handler.*;
import com.cookingfox.lapasse.api.event.handler.EventHandler;
import com.cookingfox.lapasse.api.facade.Facade;
import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.exception.AnnotationProcessorException;
import com.cookingfox.lapasse.compiler.processor.ProcessorResults;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandMethodParams;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandProcessor;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandResult;
import com.cookingfox.lapasse.compiler.processor.command.HandleCommandReturnValue;
import com.cookingfox.lapasse.compiler.processor.event.HandleEventMethodParams;
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
import javax.lang.model.util.Types;
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

    protected static final String FIELD_PREFIX = "handler";
    protected static final String METHOD_HANDLE = "handle";
    protected static final String VAR_COMMAND = "command";
    protected static final String VAR_EVENT = "event";
    protected static final String VAR_FACADE = "facade";
    protected static final String VAR_ORIGIN = "origin";
    protected static final String VAR_STATE = "state";

    //----------------------------------------------------------------------------------------------
    // PROPERTIES
    //----------------------------------------------------------------------------------------------

    protected Elements elements;
    protected Filer filer;
    protected Messager messager;
    protected Types types;

    //----------------------------------------------------------------------------------------------
    // JAVAX ABSTRACT PROCESSOR IMPLEMENTATION
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
        types = processingEnv.getTypeUtils();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        try {
            doProcess(roundEnv);
        } catch (AnnotationProcessorException e) {
            printError(e.getOrigin(), e.getMessage());
        }

        return false;
    }

    //----------------------------------------------------------------------------------------------
    // PROTECTED METHODS
    //----------------------------------------------------------------------------------------------

    /**
     * Perform annotation processing.
     *
     * @param roundEnv The environment for the round of annotation processing.
     * @throws AnnotationProcessorException when an error occurs.
     */
    protected void doProcess(RoundEnvironment roundEnv) throws AnnotationProcessorException {
        // processor results by 'origin' (the enclosing element - the class containing the
        // annotated method)
        final Map<TypeElement, ProcessorResults> results = new LinkedHashMap<>();

        // events first, because they have a better chance of determining the concrete facade state
        processHandleEventAnnotations(results, roundEnv);
        processHandleCommandAnnotations(results, roundEnv);

        // generate code with processor results
        for (Map.Entry<TypeElement, ProcessorResults> entry : results.entrySet()) {
            TypeElement origin = entry.getKey();
            ProcessorResults processorResults = entry.getValue();

            GenerationModel model = new GenerationModel();
            model.fieldNameCounter = 0;
            model.origin = origin;
            model.processorResults = processorResults;
            model.targetStateName = processorResults.getTargetStateName();

            // generate handlers
            generateHandlersType(model);
            generateCommandHandlers(model);
            generateEventHandlers(model);

            // build type spec
            TypeSpec typeSpec = model.buildTypeSpec();

            // create java file from type spec
            JavaFile javaFile = JavaFile.builder(model.packageName, typeSpec)
                    .addFileComment("Generated code from LaPasse - do not modify!")
                    .build();

            writeJavaFile(javaFile, origin);
        }
    }

    /**
     * Generate the enclosing element ({@link HandlerMapper} implementation) for the handlers.
     *
     * @param model The processed data to use for code generation.
     */
    protected void generateHandlersType(GenerationModel model) {
        // get original package and class name
        String packageName = elements.getPackageOf(model.origin).getQualifiedName().toString();
        String originClassName = getClassName(model.origin, packageName);

        // create new class name
        String generatedClassName = originClassName + LaPasse.GENERATED_SUFFIX;
        ClassName className = ClassName.get(packageName, generatedClassName);

        // create generic type parameter for origin
        TypeVariableName originGeneric = TypeVariableName.get("T",
                ClassName.get(packageName, originClassName));

        // create facade type with concrete state generic
        ParameterizedTypeName facadeConcrete =
                ParameterizedTypeName.get(ClassName.get(Facade.class), model.targetStateName);

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

        model.packageName = packageName;
        model.typeSpecBuilder = typeSpecBuilder;
        model.mapHandlersBuilder = mapHandlersBuilder;
    }

    /**
     * Generate the command handlers.
     *
     * @param model The processed data to use for code generation.
     * @throws AnnotationProcessorException when an error occurs.
     */
    protected void generateCommandHandlers(GenerationModel model) throws AnnotationProcessorException {
        for (HandleCommandResult result : model.processorResults.getHandleCommandResults()) {
            String fieldName = FIELD_PREFIX + (++model.fieldNameCounter);

            // collect handler specific parameters
            Name methodName = result.getMethodName();
            TypeName commandName = result.getCommandTypeName();
            TypeName eventTypeName = result.getEventTypeName();
            HandleCommandReturnValue returnValue = result.getReturnValue();
            TypeName returnType = result.getReturnTypeName();
            HandleCommandMethodParams methodParams = result.getMethodParams();

            if (methodParams == null) {
                throw new AnnotationProcessorException("Method params is null", model.origin);
            }

            // build handler method
            MethodSpec.Builder handlerMethodBuilder = MethodSpec.methodBuilder(METHOD_HANDLE)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(model.targetStateName, VAR_STATE)
                    .addParameter(commandName, VAR_COMMAND)
                    .returns(returnType);

            ParameterizedTypeName handlerType;
            String callerStatement = "";

            if (returnValue == HandleCommandReturnValue.RETURNS_VOID) {
                handlerType = ParameterizedTypeName.get(ClassName.get(VoidCommandHandler.class),
                        model.targetStateName, commandName);
            } else {
                callerStatement = "return ";

                // default: returns event
                Class<? extends CommandHandler> commandHandlerClass = SyncCommandHandler.class;

                // set command handler class
                switch (returnValue) {
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
                        model.targetStateName, commandName, eventTypeName);
            }

            List<CharSequence> statementArgs = new LinkedList<>();
            statementArgs.add(VAR_ORIGIN);
            statementArgs.add(methodName);

            if (methodParams == HandleCommandMethodParams.METHOD_ONE_PARAM_COMMAND) {
                callerStatement += "$N.$N($N)";
                statementArgs.add(VAR_COMMAND);
            } else if (methodParams == HandleCommandMethodParams.METHOD_ONE_PARAM_STATE) {
                callerStatement += "$N.$N($N)";
                statementArgs.add(VAR_STATE);
            } else if (methodParams == HandleCommandMethodParams.METHOD_TWO_PARAMS_COMMAND_STATE) {
                callerStatement += "$N.$N($N, $N)";
                statementArgs.add(VAR_COMMAND);
                statementArgs.add(VAR_STATE);
            } else if (methodParams == HandleCommandMethodParams.METHOD_TWO_PARAMS_STATE_COMMAND) {
                callerStatement += "$N.$N($N, $N)";
                statementArgs.add(VAR_STATE);
                statementArgs.add(VAR_COMMAND);
            } else {
                callerStatement += "$N.$N()";
            }

            handlerMethodBuilder.addStatement(callerStatement, statementArgs.toArray());

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
            model.typeSpecBuilder.addField(handlerField);

            // add handler mapping to HandlerMapper
            model.mapHandlersBuilder.addStatement(
                    "$N.mapCommandHandler($T.class, $N)",
                    VAR_FACADE,
                    commandName,
                    fieldName
            );
        }
    }

    /**
     * Generate the event handlers.
     *
     * @param model The processed data to use for code generation.
     * @throws AnnotationProcessorException when an error occurs.
     */
    protected void generateEventHandlers(GenerationModel model) throws AnnotationProcessorException {
        for (HandleEventResult result : model.processorResults.getHandleEventResults()) {
            String fieldName = FIELD_PREFIX + (++model.fieldNameCounter);

            // collect handler specific parameters
            Name methodName = result.getMethodName();
            TypeName eventType = result.getEventTypeName();
            HandleEventMethodParams methodParams = result.getMethodParams();

            if (methodParams == null) {
                throw new AnnotationProcessorException("Method params is null", model.origin);
            }

            // build handler method
            MethodSpec.Builder handlerMethodBuilder = MethodSpec.methodBuilder(METHOD_HANDLE)
                    .addAnnotation(Override.class)
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(model.targetStateName, VAR_STATE)
                    .addParameter(eventType, VAR_EVENT);

            List<CharSequence> statementArgs = new LinkedList<>();
            statementArgs.add(VAR_ORIGIN);
            statementArgs.add(methodName);

            String callerStatement = "return ";

            if (methodParams == HandleEventMethodParams.METHOD_ONE_PARAM_EVENT) {
                callerStatement += "$N.$N($N)";
                statementArgs.add(VAR_EVENT);
            } else if (methodParams == HandleEventMethodParams.METHOD_ONE_PARAM_STATE) {
                callerStatement += "$N.$N($N)";
                statementArgs.add(VAR_STATE);
            } else if (methodParams == HandleEventMethodParams.METHOD_TWO_PARAMS_EVENT_STATE) {
                callerStatement += "$N.$N($N, $N)";
                statementArgs.add(VAR_EVENT);
                statementArgs.add(VAR_STATE);
            } else if (methodParams == HandleEventMethodParams.METHOD_TWO_PARAMS_STATE_EVENT) {
                callerStatement += "$N.$N($N, $N)";
                statementArgs.add(VAR_STATE);
                statementArgs.add(VAR_EVENT);
            } else {
                callerStatement += "$N.$N()";
            }

            handlerMethodBuilder.addStatement(callerStatement, statementArgs.toArray());

            MethodSpec handlerMethod = handlerMethodBuilder.returns(model.targetStateName)
                    .build();

            // create parameterized name for handler
            ParameterizedTypeName handlerType = ParameterizedTypeName.get(
                    ClassName.get(EventHandler.class), model.targetStateName, eventType);

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
            model.typeSpecBuilder.addField(handlerField);

            // add handler mapping to HandlerMapper
            model.mapHandlersBuilder.addStatement(
                    "$N.mapEventHandler($T.class, $N)",
                    VAR_FACADE,
                    eventType,
                    fieldName
            );
        }
    }

    /**
     * Generates a class name using the provided type and package name.
     *
     * @param type        The type element to extract the class name from.
     * @param packageName The name of the type's package.
     * @return The class name.
     */
    protected String getClassName(TypeElement type, String packageName) {
        return type.getQualifiedName()
                .toString()
                .substring(packageName.length() + 1)
                .replace('.', '$');
    }

    /**
     * Fetches and/or creates the {@link ProcessorResults} for the provided element and stores it in
     * the results map.
     *
     * @param results The map of processor results, ordered by enclosing element (class).
     * @param element The enclosing element of the annotated handler methods.
     * @return The existing or newly created processor results for this enclosing element (class).
     */
    protected ProcessorResults getProcessorResults(Map<TypeElement, ProcessorResults> results, TypeElement element) {
        ProcessorResults processorResults = results.get(requireNonNull(element));

        if (processorResults == null) {
            processorResults = new ProcessorResults();
            results.put(element, processorResults);
        }

        return processorResults;
    }

    /**
     * Print an error message.
     *
     * @param element The element that could not be processed.
     * @param msg     The error message.
     * @param args    Arguments to parse in the message.
     * @return False, so it is easier to exit the processing process.
     */
    protected boolean printError(Element element, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), element);

        return false;
    }

    /**
     * Process all {@link HandleCommand} annotations and collect the results.
     *
     * @param results  The destination for the processor results.
     * @param roundEnv The environment for the round of annotation processing.
     * @throws AnnotationProcessorException when an error occurs.
     */
    protected void processHandleCommandAnnotations(Map<TypeElement, ProcessorResults> results, RoundEnvironment roundEnv) throws AnnotationProcessorException {
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleCommand.class)) {
            TypeElement origin = (TypeElement) element.getEnclosingElement();
            HandleCommandProcessor processor = new HandleCommandProcessor(element, types);

            try {
                HandleCommandResult result = processor.process();

                ProcessorResults processorResults = getProcessorResults(results, origin);
                processorResults.addHandleCommandResult(result);
                processorResults.detectTargetStateNameConflict();
            } catch (Exception e) {
                throw new AnnotationProcessorException(e.getMessage(), element);
            }
        }
    }

    /**
     * Process all {@link HandleEvent} annotations and collect the results.
     *
     * @param results  The destination for the processor results.
     * @param roundEnv The environment for the round of annotation processing.
     * @throws AnnotationProcessorException when an error occurs.
     */
    protected void processHandleEventAnnotations(Map<TypeElement, ProcessorResults> results, RoundEnvironment roundEnv) throws AnnotationProcessorException {
        for (Element element : roundEnv.getElementsAnnotatedWith(HandleEvent.class)) {
            TypeElement origin = (TypeElement) element.getEnclosingElement();
            HandleEventProcessor processor = new HandleEventProcessor(element, types);

            try {
                HandleEventResult result = processor.process();

                ProcessorResults processorResults = getProcessorResults(results, origin);
                processorResults.addHandleEventResult(result);
                processorResults.detectTargetStateNameConflict();
            } catch (Exception e) {
                throw new AnnotationProcessorException(e.getMessage(), element);
            }
        }
    }

    /**
     * Writes the generated Java file to the filer.
     *
     * @param javaFile The generated Java file.
     * @param origin   The enclosing element (class) for the annotated methods.
     * @throws AnnotationProcessorException when an errors occurs.
     */
    protected void writeJavaFile(JavaFile javaFile, TypeElement origin) throws AnnotationProcessorException {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            throw new AnnotationProcessorException("Unable to write Java file", e, origin);
        }
    }

    //----------------------------------------------------------------------------------------------
    // MEMBER CLASSES
    //----------------------------------------------------------------------------------------------

    /**
     * Data model for annotation process and code generation.
     */
    protected static class GenerationModel {

        //------------------------------------------------------------------------------------------
        // PROPERTIES
        //------------------------------------------------------------------------------------------

        /**
         * Increasing counter, to make sure all fields have a unique name.
         */
        protected int fieldNameCounter;

        /**
         * Method spec builder for the {@link HandlerMapper#mapHandlers()} implementation.
         */
        protected MethodSpec.Builder mapHandlersBuilder;

        /**
         * The enclosing element (class) for the annotated methods.
         */
        protected TypeElement origin;

        /**
         * The package name of the {@link #origin}.
         */
        protected String packageName;

        /**
         * The processor results for the {@link #origin}.
         */
        protected ProcessorResults processorResults;

        /**
         * The type name of the concrete {@link State} implementation for the LaPasse facade.
         *
         * @see ProcessorResults#getTargetStateName()
         */
        protected TypeName targetStateName;

        /**
         * Type spec builder for the generated {@link HandlerMapper} implementation.
         */
        protected TypeSpec.Builder typeSpecBuilder;

        //------------------------------------------------------------------------------------------
        // METHODS
        //------------------------------------------------------------------------------------------

        /**
         * Builds and adds the type spec for the {@link #origin}.
         *
         * @return The {@link #typeSpecBuilder} build result.
         */
        protected TypeSpec buildTypeSpec() {
            // add populated HandlerMapper method to class
            typeSpecBuilder.addMethod(mapHandlersBuilder.build());

            return typeSpecBuilder.build();
        }

    }

}
