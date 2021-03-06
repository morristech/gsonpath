package gsonpath.generator.standard

import com.google.common.truth.Truth
import com.google.testing.compile.JavaSourcesSubjectFactory
import gsonpath.GsonProcessorImpl
import gsonpath.generator.BaseGeneratorTest
import org.junit.Test

class PolymorphismTest : BaseGeneratorTest() {
    @Test
    fun givenStringKeys_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/string_keys",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenIntegerKeys_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/integer_keys",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenBooleanKeys_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/boolean_keys",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenStringKeysWithInterface_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/using_interface",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenStringKeysWithListField_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/using_list",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenDefaultValueAndDefaultFailureOutcome_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/default_value",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenRemoveElementFailureOutcome_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/failure_outcome_remove_element",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenFailFailureOutcome_whenProcessorExecuted_expectValidGsonTypeAdapter() {
        assertGeneratedContent(TestCriteria("generator/standard/polymorphism/failure_outcome_fail",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java",
                        "generator/standard/polymorphism/Type.java",
                        "generator/standard/polymorphism/Type1.java",
                        "generator/standard/polymorphism/Type2.java"),

                relativeSourceNames = listOf(
                        "TypesList.java"),

                relativeGeneratedNames = listOf(
                        "TypesList_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun givenNoKeys_whenProcessorExecuted_expectNoKeysError() {
        assertPolymorphismFailure("TypesList_NoKeys.java",
                "Gson Path: Keys must be specified for the GsonSubType")
    }

    @Test
    fun givenMultipleKeys_whenProcessorExecuted_expectMultipleKeysError() {
        assertPolymorphismFailure("TypesList_MultipleKeys.java",
                "Only one keys array (string, integer or boolean) may be specified for the GsonSubType")
    }

    @Test
    fun givenBlankFieldName_whenProcessorExecuted_expectBlankFieldNameError() {
        assertPolymorphismFailure("TypesList_BlankFieldName.java",
                "Gson Path: subTypeKey cannot be blank for GsonSubType")
    }

    @Test
    fun givenNoInheritanceLink_whenProcessorExecuted_expectBlankFieldNameError() {
        assertPolymorphismFailure("TypesList_TypeInvalidInheritance.java",
                "Gson Path: subtype java.lang.String does not inherit from generator.standard.polymorphism.Type")
    }

    private fun assertPolymorphismFailure(className: String, errorMessage: String) {
        val criteria = TestCriteria("generator/standard/polymorphism/failures",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java"),

                relativeSourceNames = listOf(
                        className))

        val sourceFilesSize = criteria.sourceFilesSize
        val sources = (0..sourceFilesSize - 1).map { criteria.getSourceFileObject(it) }

        Truth.assertAbout(JavaSourcesSubjectFactory.javaSources()).that(sources)
                .processedWith(GsonProcessorImpl())
                .failsToCompile()
                .withErrorContaining(errorMessage)
    }
}