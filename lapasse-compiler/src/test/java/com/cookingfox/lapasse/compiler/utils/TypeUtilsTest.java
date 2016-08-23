package com.cookingfox.lapasse.compiler.utils;

import com.cookingfox.lapasse.api.state.State;
import com.cookingfox.lapasse.compiler.LaPasseAnnotationProcessor;
import com.google.testing.compile.CompilationRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import javax.annotation.processing.AbstractProcessor;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.cookingfox.lapasse.compiler.utils.TypeUtils.firstArgIsSubType;
import static com.cookingfox.lapasse.compiler.utils.TypeUtils.isSubtype;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static testing.TestingUtils.assertPrivateConstructorInstantiationUnsupported;

/**
 * Unit tests for {@link TypeUtils}.
 */
public class TypeUtilsTest {

    //----------------------------------------------------------------------------------------------
    // TEST SETUP
    //----------------------------------------------------------------------------------------------

    @Rule
    public CompilationRule rule = new CompilationRule();

    private Elements elements;
    private Types types;

    @Before
    public void setUp() throws Exception {
        elements = rule.getElements();
        types = rule.getTypes();
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: constructor
    //----------------------------------------------------------------------------------------------

    @Test
    public void constructor_should_throw() throws Exception {
        assertPrivateConstructorInstantiationUnsupported(TypeUtils.class);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: firstArgIsSubType
    //----------------------------------------------------------------------------------------------

    @Test
    public void firstArgIsSubType_should_return_false_for_non_declared() throws Exception {
        NoType noType = types.getNoType(TypeKind.VOID);

        boolean result = firstArgIsSubType(noType, State.class);

        assertFalse(result);
    }

    @Test
    public void firstArgIsSubType_should_return_false_for_non_generic() throws Exception {
        DeclaredType type = types.getDeclaredType(elements.getTypeElement(State.class.getName()));

        boolean result = firstArgIsSubType(type, State.class);

        assertFalse(result);
    }

    //----------------------------------------------------------------------------------------------
    // TESTS: isSubtype
    //----------------------------------------------------------------------------------------------

    @Test
    public void isSubtype_should_return_true_for_sub_class() throws Exception {
        // LaPasseAnnotationProcessor extends AbstractProcessor
        TypeElement element = elements.getTypeElement(LaPasseAnnotationProcessor.class.getName());

        boolean result = isSubtype(element, AbstractProcessor.class);

        assertTrue(result);
    }

    @Test
    public void isSubtype_should_return_false_for_non_element() throws Exception {
        DeclaredType mock = mock(DeclaredType.class);
        when(mock.getKind()).thenReturn(TypeKind.DECLARED);

        boolean result = isSubtype(mock, State.class);

        assertFalse(result);
    }

}
