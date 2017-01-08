package gsonpath.generator.adapter.auto

import gsonpath.generator.BaseGeneratorTest
import org.junit.Test

class SerializeNullsTest : BaseGeneratorTest() {

    @Test
    fun testSerializeNulls() {
        assertGeneratedContent(BaseGeneratorTest.TestCriteria("adapter/auto/serialize_nulls")
                .addRelativeSource("TestSerializeNulls.java")
                .addRelativeGenerated("TestSerializeNulls_GsonTypeAdapter.java"))
    }

}