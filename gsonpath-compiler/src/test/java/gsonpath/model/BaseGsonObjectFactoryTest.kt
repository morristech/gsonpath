package gsonpath.model

import com.google.gson.FieldNamingPolicy
import com.google.gson.annotations.SerializedName
import com.squareup.javapoet.TypeName
import gsonpath.GsonFieldValidationType
import gsonpath.PathSubstitution
import gsonpath.ProcessingException
import org.junit.Rule
import org.junit.rules.ExpectedException

import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

open class BaseGsonObjectFactoryTest {

    @JvmField
    @Rule
    val exception: ExpectedException = ExpectedException.none()

    @JvmOverloads internal fun mockFieldInfo(fieldName: String, jsonPath: String? = null): FieldInfo {
        val fieldInfo = mock(FieldInfo::class.java)
        `when`(fieldInfo.typeName).thenReturn(TypeName.INT)
        `when`(fieldInfo.annotationNames).thenReturn(emptyList())
        `when`(fieldInfo.fieldName).thenReturn(fieldName)

        if (jsonPath != null) {
            val serializedName = mock(SerializedName::class.java)
            `when`<String>(serializedName.value).thenReturn(jsonPath)
            `when`<SerializedName>(fieldInfo.getAnnotation(SerializedName::class.java)).thenReturn(serializedName)
        }

        return fieldInfo
    }

    @Throws(ProcessingException::class)
    @JvmOverloads internal fun executeAddGsonType(arguments: GsonTypeArguments, outputGsonObject: GsonObject = GsonObject()): GsonObject {
        GsonObjectFactory().addGsonType(
                outputGsonObject,
                arguments.fieldInfo,
                arguments.fieldInfoIndex,
                arguments.flattenDelimiter,
                arguments.gsonFieldNamingPolicy,
                arguments.gsonFieldValidationType,
                arguments.pathSubstitutions
        )

        return outputGsonObject
    }

    internal class GsonTypeArguments(val fieldInfo: FieldInfo,
                                     val gsonFieldValidationType: GsonFieldValidationType = GsonFieldValidationType.NO_VALIDATION,
                                     val pathSubstitutions: Array<PathSubstitution> = emptyArray(),
                                     val fieldInfoIndex: Int = 0,
                                     val flattenDelimiter: Char = '.',
                                     val gsonFieldNamingPolicy: FieldNamingPolicy = FieldNamingPolicy.IDENTITY)

    companion object {
        internal val DEFAULT_VARIABLE_NAME = "variableName"
    }
}
