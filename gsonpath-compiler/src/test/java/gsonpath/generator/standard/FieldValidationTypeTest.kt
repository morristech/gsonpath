package gsonpath.generator.standard

import gsonpath.generator.BaseGeneratorTest
import org.junit.Test

class FieldValidationTypeTest : BaseGeneratorTest() {
    @Test
    fun testValidateExplicitNonNull() {
        assertGeneratedContent(TestCriteria("generator/standard/field_policy/validate_explicit_non_null",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java"),

                relativeSourceNames = listOf(
                        "TestValidateExplicitNonNull.java"),

                relativeGeneratedNames = listOf(
                        "TestValidateExplicitNonNull_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun testValidateAllExceptNullable() {
        assertGeneratedContent(TestCriteria("generator/standard/field_policy/validate_all_except_nullable",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java"),

                relativeSourceNames = listOf(
                        "TestValidateAllExceptNullable.java"),

                relativeGeneratedNames = listOf(
                        "TestValidateAllExceptNullable_GsonTypeAdapter.java")
        ))
    }

    @Test
    fun testNoValidation() {
        assertGeneratedContent(TestCriteria("generator/standard/field_policy/no_validation",

                absoluteSourceNames = listOf(
                        "generator/standard/TestGsonTypeFactory.java"),

                relativeSourceNames = listOf(
                        "TestNoValidation.java"),

                relativeGeneratedNames = listOf(
                        "TestNoValidation_GsonTypeAdapter.java")
        ))
    }
}