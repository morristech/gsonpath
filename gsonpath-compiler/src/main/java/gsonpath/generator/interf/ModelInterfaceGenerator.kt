package gsonpath.generator.interf

import com.squareup.javapoet.*
import gsonpath.ProcessingException
import gsonpath.generator.Generator
import gsonpath.model.InterfaceFieldInfo
import gsonpath.model.InterfaceInfo
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.ExecutableType
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.ParameterizedTypeName
import com.squareup.javapoet.MethodSpec
import gsonpath.internal.GsonPathElementList
import com.squareup.javapoet.TypeSpec
import gsonpath.compiler.generateClassName
import gsonpath.compiler.addNewLine
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeMirror

internal class ModelInterfaceGenerator(processingEnv: ProcessingEnvironment) : Generator(processingEnv) {

    @Throws(ProcessingException::class)
    fun handle(element: TypeElement): InterfaceInfo {
        val interfaces = element.interfaces
        if (interfaces != null && interfaces.size == 1) {
            val listTypeMirror = interfaces[0]
            val typeName = TypeName.get(listTypeMirror)

            if (typeName is ParameterizedTypeName) {
                if (typeName.rawType == TypeName.get(List::class.java)) {
                    return handleList(ClassName.get(element), typeName, listTypeMirror)
                }
            }
        }
        return handleStandard(element)
    }

    private fun createOutputClassName(modelClassName: ClassName): ClassName {
        return ClassName.get(modelClassName.packageName(), generateClassName(modelClassName, "GsonPathModel"))
    }

    @Throws(ProcessingException::class)
    private fun handleList(modelClassName: ClassName, listTypeName: ParameterizedTypeName, listTypeMirror: TypeMirror): InterfaceInfo {
        val outputClassName = createOutputClassName(modelClassName)

        val typeBuilder = TypeSpec.classBuilder(outputClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .superclass(ParameterizedTypeName.get(ClassName.get(GsonPathElementList::class.java), listTypeName.typeArguments[0]))
                .addSuperinterface(modelClassName)

                .addField(listTypeName, "internalList", Modifier.PRIVATE, Modifier.FINAL)

                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(listTypeName, "internalList")

                        .addStatement("this.internalList = internalList")
                        .build())

                .addMethod(MethodSpec.methodBuilder("getList")
                        .addAnnotation(Override::class.java)
                        .addModifiers(Modifier.PROTECTED)
                        .returns(listTypeName)

                        .addStatement("return internalList")
                        .build())

        if (!writeFile(outputClassName.packageName(), typeBuilder)) {
            throw ProcessingException("Failed to write generated file: " + outputClassName.simpleName())
        }

        val fieldInfo = listOf(InterfaceFieldInfo(object : InterfaceFieldInfo.ElementInfo {
            override val underlyingElement: Element?
                get() = null

            override fun <T : Annotation> getAnnotation(annotationClass: Class<T>): T? {
                return null
            }

            override val annotationNames: List<String>
                get() = listOf("AutoGsonAdapter", "NonNull")

        }, listTypeName, listTypeMirror, "internalList", true))

        return InterfaceInfo(outputClassName, fieldInfo)
    }

    @Throws(ProcessingException::class)
    private fun handleStandard(element: TypeElement): InterfaceInfo {
        val modelClassName = ClassName.get(element)
        val outputClassName: ClassName = createOutputClassName(modelClassName)

        val typeBuilder = TypeSpec.classBuilder(outputClassName)
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(modelClassName)

        val constructorBuilder = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PUBLIC)

        val methodElements = getMethodElements(element)

        // Equals method
        val equalsCodeBlock = CodeBlock.builder()
                .addStatement("if (this == o) return true")
                .addStatement("if (o == null || getClass() != o.getClass()) return false")
                .addNewLine()
                .addStatement("\$T equalsOtherType = (\$T) o", outputClassName, outputClassName)
                .addNewLine()

        // Hash code method
        val hasCodeCodeBlock = CodeBlock.builder()

        // ToString method
        val toStringCodeBlock = CodeBlock.builder()
                .add("""return "${modelClassName.simpleName()}{" +""")
                .addNewLine()

        // An optimisation for hash codes which prevents us creating too many temp long variables.
        val hasDoubleField = methodElements
                .map { it.asType() as ExecutableType }
                .map { it.returnType }
                .any { TypeName.get(it) == TypeName.DOUBLE }

        if (hasDoubleField) {
            hasCodeCodeBlock.addStatement("long temp")
        }

        val interfaceInfoList = ArrayList<InterfaceFieldInfo>()
        for (elementIndex in methodElements.indices) {
            val enclosedElement = methodElements[elementIndex]

            val methodType = enclosedElement.asType() as ExecutableType

            // Ensure that any generics have been converted into their actual return types.
            val returnTypeMirror: TypeMirror = (processingEnv.typeUtils.asMemberOf(element.asType() as DeclaredType, enclosedElement)
                    as ExecutableType).returnType
            val typeName = TypeName.get(returnTypeMirror)

            if (typeName == null || typeName == TypeName.VOID) {
                throw ProcessingException("Gson Path interface methods must have a return type", enclosedElement)
            }

            if (methodType.parameterTypes.isNotEmpty()) {
                throw ProcessingException("Gson Path interface methods must not have parameters", enclosedElement)
            }

            val methodName = enclosedElement.simpleName.toString()

            //
            // Transform the method name into the field name by removing the first camel-cased portion.
            // e.g. 'getName' becomes 'name'
            //
            val fieldName: String
            val indexOfFirst = methodName.indexOfFirst(Char::isUpperCase)
            if (indexOfFirst != -1) {
                fieldName = methodName[indexOfFirst].toLowerCase() + methodName.substring(indexOfFirst + 1)
            } else {
                fieldName = methodName
            }

            typeBuilder.addField(typeName, fieldName, Modifier.PRIVATE, Modifier.FINAL)

            val accessorMethod = MethodSpec.methodBuilder(methodName)
                    .addAnnotation(Override::class.java)
                    .addModifiers(Modifier.PUBLIC)
                    .returns(typeName)
                    .addStatement("return $fieldName")

            // Copy all annotations from the interface accessor method to the implementing classes accessor.
            val annotationMirrors = enclosedElement.annotationMirrors
            for (annotationMirror in annotationMirrors) {
                accessorMethod.addAnnotation(AnnotationSpec.get(annotationMirror))
            }

            typeBuilder.addMethod(accessorMethod.build())

            // Add the parameter to the constructor
            constructorBuilder.addParameter(typeName, fieldName)
                    .addStatement("this.$fieldName = $fieldName")

            interfaceInfoList.add(InterfaceFieldInfo(StandardElementInfo(enclosedElement), typeName, returnTypeMirror, fieldName, false))

            // Add to the equals method
            if (typeName.isPrimitive) {
                equalsCodeBlock.addStatement("if ($fieldName != equalsOtherType.$fieldName) return false")
            } else {
                if (typeName is ArrayTypeName) {
                    equalsCodeBlock.addStatement("if (!java.util.Arrays.equals($fieldName, equalsOtherType.$fieldName)) return false")

                } else {
                    equalsCodeBlock.addStatement("if ($fieldName != null ? !$fieldName.equals(equalsOtherType.$fieldName) : equalsOtherType.$fieldName != null) return false")
                }
            }

            // Add to the hash code method
            val hashCodeLine: String

            if (typeName.isPrimitive) {
                // The allowed primitive types are: int, long, double, boolean
                if (typeName == TypeName.INT) {
                    hashCodeLine = fieldName

                } else if (typeName == TypeName.LONG) {
                    hashCodeLine = "(int) ($fieldName ^ ($fieldName >>> 32))"

                } else if (typeName == TypeName.DOUBLE) {
                    hasCodeCodeBlock.addStatement("temp = java.lang.Double.doubleToLongBits($fieldName)")
                    hashCodeLine = "(int) (temp ^ (temp >>> 32))"

                } else {
                    // Last possible outcome in a boolean.
                    hashCodeLine = "($fieldName ? 1 : 0)"
                }
            } else {
                if (typeName is ArrayTypeName) {
                    hashCodeLine = "java.util.Arrays.hashCode($fieldName)"

                } else {
                    hashCodeLine = "$fieldName != null ? $fieldName.hashCode() : 0"
                }
            }

            if (elementIndex == 0) {
                hasCodeCodeBlock.addStatement("int hashCodeReturnValue = $hashCodeLine")
            } else {
                hasCodeCodeBlock.addStatement("hashCodeReturnValue = 31 * hashCodeReturnValue + ($hashCodeLine)")
            }

            // Add to the toString method.
            toStringCodeBlock.add("\t\t\"")
            if (elementIndex > 0) {
                toStringCodeBlock.add(", ")
            }
            if (typeName is ArrayTypeName) {
                toStringCodeBlock.add("""$fieldName=" + java.util.Arrays.toString($fieldName) +""")

            } else {
                toStringCodeBlock.add("""$fieldName=" + $fieldName +""")
            }
            toStringCodeBlock.addNewLine()
        }

        typeBuilder.addMethod(constructorBuilder.build())

        // Add the equals method
        typeBuilder.addMethod(
                MethodSpec.methodBuilder("equals")
                        .addAnnotation(Override::class.java)
                        .addModifiers(Modifier.PUBLIC)
                        .returns(TypeName.BOOLEAN)
                        .addParameter(TypeName.OBJECT, "o")
                        .addCode(equalsCodeBlock.addNewLine()
                                .addStatement("return true")
                                .build())
                        .build())

        // If we have no elements, 'hashCodeReturnValue' won't be initialised!
        if (methodElements.isNotEmpty()) {
            hasCodeCodeBlock.addStatement("return hashCodeReturnValue")
        } else {
            hasCodeCodeBlock.addStatement("return 0")
        }

        // Add the hashCode method
        typeBuilder.addMethod(MethodSpec.methodBuilder("hashCode")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.INT)

                .addCode(hasCodeCodeBlock.build())
                .build())

        // Add the toString method
        typeBuilder.addMethod(MethodSpec.methodBuilder("toString")
                .addAnnotation(Override::class.java)
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(String::class.java))

                .addCode(toStringCodeBlock.build())
                .addStatement("\t\t'}'", modelClassName.simpleName())
                .build())

        if (!writeFile(outputClassName.packageName(), typeBuilder)) {
            throw ProcessingException("Failed to write generated file: " + outputClassName.simpleName())
        }

        return InterfaceInfo(outputClassName, interfaceInfoList)
    }

    private fun getMethodElements(element: TypeElement): List<Element> {
        val methodElements = processingEnv.elementUtils.getAllMembers(element).filter {
            // Ignore methods from the base Object class
            it.kind == ElementKind.METHOD && TypeName.get(it.enclosingElement.asType()) != TypeName.OBJECT
        }
        return methodElements
    }

    private class StandardElementInfo constructor(override val underlyingElement: Element) : InterfaceFieldInfo.ElementInfo {

        override fun <T : Annotation> getAnnotation(annotationClass: Class<T>): T? {
            return underlyingElement.getAnnotation(annotationClass)
        }

        override val annotationNames: List<String>
            get() {
                return underlyingElement.annotationMirrors.map { it ->
                    it.annotationType.asElement().simpleName.toString()
                }
            }
    }

}
