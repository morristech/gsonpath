package gsonpath.generator.adapter.auto

import gsonpath.generator.BaseGeneratorTest
import org.junit.Test

class NestedClassTest : BaseGeneratorTest() {
    @Test
    fun testNestedClasses() {
        assertGeneratedContent(BaseGeneratorTest.TestCriteria("adapter/auto/nested_class")
                .addRelativeSource("TestNestedClass.java")
                .addRelativeGenerated("TestNestedClass_Nested_GsonTypeAdapter.java"))
    }
}