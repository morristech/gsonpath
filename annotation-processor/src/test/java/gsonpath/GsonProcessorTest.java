package gsonpath;

import com.google.common.base.Joiner;
import com.google.testing.compile.JavaFileObjects;

import org.junit.Test;

import java.util.ArrayList;

import javax.tools.JavaFileObject;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;
import static com.google.testing.compile.JavaSourcesSubjectFactory.javaSources;

/**
 * Created by Lachlan on 2/03/2016.
 */
public class GsonProcessorTest {

    private static final String STANDARD_PACKAGE_NAME = "package com.test;";

    private static final String IMPORT_GSON_PATH_CLASS = "import gsonpath.GsonPathClass;";
    private static final String IMPORT_GSON_PATH_ELEMENT = "import gsonpath.GsonPathElement;";

    private static final String STANDARD_RESULT_PACKAGE_AND_IMPORTS = Joiner.on('\n').join(
            STANDARD_PACKAGE_NAME,
            "",
            "import static gsonpath.GsonPathUtil.getBooleanSafely;",
            "import static gsonpath.GsonPathUtil.getDoubleSafely;",
            "import static gsonpath.GsonPathUtil.getIntegerSafely;",
            "import static gsonpath.GsonPathUtil.getLongSafely;",
            "import static gsonpath.GsonPathUtil.getStringSafely;",
            "",
            "import com.google.gson.Gson;",
            "import com.google.gson.TypeAdapter;",
            "import com.google.gson.stream.JsonReader;",
            "import com.google.gson.stream.JsonWriter;",
            "import java.io.IOException;",
            "import java.lang.Override;",
            ""
    );

    private static final String STANDARD_RESULT_HEADER = Joiner.on('\n').join(
            "public final class Test_GsonTypeAdapter extends TypeAdapter<Test> {",
            "  private final Gson mGson;",
            "",
            "  public Test_GsonTypeAdapter(Gson gson) {",
            "    this.mGson = gson;",
            "  }",
            "",
            "  @Override",
            "  public Test read(JsonReader in) throws IOException {",
            "    Test result = new Test();"
    );

    private static final String STANDARD_RESULT_FOOTER = Joiner.on('\n').join(
            "    return result;",
            "  }",
            "",
            "  @Override",
            "  public void write(JsonWriter out, Test value) throws IOException {",
            "    // GsonPath does not support writing at this stage.",
            "  }",
            "}"
    );

    private static final String EMPTY_RESULT = Joiner.on('\n').join(
            STANDARD_RESULT_PACKAGE_AND_IMPORTS,
            STANDARD_RESULT_HEADER,
            STANDARD_RESULT_FOOTER
    );

    private void assertEmptyFile(JavaFileObject source) {
        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter", EMPTY_RESULT));
    }

    /**
     * Tests the output generated when only a {@link GsonPathClass} annotation is used.
     */
    @Test
    public void testGsonPathClassOnly() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                "@GsonPathClass",
                "public class Test {",
                "}"
        ));

        assertEmptyFile(source);
    }

    @Test
    public void testGsonPathWithPrimitives() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass",
                "public class Test {",
                "  @GsonPathElement(\"Json1\")",
                "  public String value1;",
                "  public boolean value2;",
                "  public int value3;",
                "  public double value4;",
                "  public long value5;",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter",
                Joiner.on('\n').join(
                        STANDARD_RESULT_PACKAGE_AND_IMPORTS,
                        STANDARD_RESULT_HEADER,
                        "    in.beginObject();",
                        "    while (in.hasNext()) {",
                        "      switch(in.nextName()) {",
                        "        case \"Json1\":",
                        "          result.value1 = getStringSafely(in);",
                        "          break;",
                        "        case \"value2\":",
                        "          result.value2 = in.nextBoolean();",
                        "          break;",
                        "        case \"value3\":",
                        "          result.value3 = in.nextInt();",
                        "          break;",
                        "        case \"value4\":",
                        "          result.value4 = in.nextDouble();",
                        "          break;",
                        "        case \"value5\":",
                        "          result.value5 = in.nextLong();",
                        "          break;",
                        "        default:",
                        "          in.skipValue();",
                        "          break;",
                        "      }",
                        "    }",
                        "    in.endObject();",
                        STANDARD_RESULT_FOOTER
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void testGsonPathWithBoxedPrimitives() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass",
                "public class Test {",
                "  @GsonPathElement(\"Json1\")",
                "  public String value1;",
                "  public Boolean value2;",
                "  public Integer value3;",
                "  public Double value4;",
                "  public Long value5;",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter",
                Joiner.on('\n').join(
                        STANDARD_RESULT_PACKAGE_AND_IMPORTS,
                        STANDARD_RESULT_HEADER,
                        "    in.beginObject();",
                        "    while (in.hasNext()) {",
                        "      switch(in.nextName()) {",
                        "        case \"Json1\":",
                        "          result.value1 = getStringSafely(in);",
                        "          break;",
                        "        case \"value2\":",
                        "          result.value2 = getBooleanSafely(in);",
                        "          break;",
                        "        case \"value3\":",
                        "          result.value3 = getIntegerSafely(in);",
                        "          break;",
                        "        case \"value4\":",
                        "          result.value4 = getDoubleSafely(in);",
                        "          break;",
                        "        case \"value5\":",
                        "          result.value5 = getLongSafely(in);",
                        "          break;",
                        "        default:",
                        "          in.skipValue();",
                        "          break;",
                        "      }",
                        "    }",
                        "    in.endObject();",
                        STANDARD_RESULT_FOOTER
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void testGsonPathWithNestedPrimitives() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass",
                "public class Test {",
                "  @GsonPathElement(\"Json1\")",
                "  public String value1;",
                "  @GsonPathElement(\"Json2.Nest1\")",
                "  public String value2;",
                "  @GsonPathElement(\"Json2.Nest2\")",
                "  public String value3;",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter",
                Joiner.on('\n').join(
                        STANDARD_RESULT_PACKAGE_AND_IMPORTS,
                        STANDARD_RESULT_HEADER,
                        "    in.beginObject();",
                        "    while (in.hasNext()) {",
                        "      switch(in.nextName()) {",
                        "        case \"Json1\":",
                        "          result.value1 = getStringSafely(in);",
                        "          break;",
                        "        case \"Json2\":",
                        "          in.beginObject();",
                        "          while (in.hasNext()) {",
                        "            switch(in.nextName()) {",
                        "              case \"Nest1\":",
                        "                result.value2 = getStringSafely(in);",
                        "                break;",
                        "              case \"Nest2\":",
                        "                result.value3 = getStringSafely(in);",
                        "                break;",
                        "              default:",
                        "                in.skipValue();",
                        "                break;",
                        "            }",
                        "          }",
                        "          in.endObject();",
                        "          break;",
                        "        default:",
                        "          in.skipValue();",
                        "          break;",
                        "      }",
                        "    }",
                        "    in.endObject();",
                        STANDARD_RESULT_FOOTER
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void testGsonPathInheritance() {

        JavaFileObject source1 = JavaFileObjects.forSourceString("test.BaseTest", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_ELEMENT,
                "public class BaseTest {",
                "  @GsonPathElement(\"Json1\")",
                "  public String value1;",
                "}"
        ));

        JavaFileObject source2 = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                "@GsonPathClass",
                "public class Test extends BaseTest {",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter",
                Joiner.on('\n').join(
                        STANDARD_RESULT_PACKAGE_AND_IMPORTS,
                        STANDARD_RESULT_HEADER,
                        "    in.beginObject();",
                        "    while (in.hasNext()) {",
                        "      switch(in.nextName()) {",
                        "        case \"Json1\":",
                        "          result.value1 = getStringSafely(in);",
                        "          break;",
                        "        default:",
                        "          in.skipValue();",
                        "          break;",
                        "      }",
                        "    }",
                        "    in.endObject();",
                        STANDARD_RESULT_FOOTER
                ));

        ArrayList<JavaFileObject> sources = new ArrayList<>();
        sources.add(source1);
        sources.add(source2);

        assertAbout(javaSources()).that(sources)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void testGsonPathCollapseJson() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass",
                "public class Test {",
                "  @GsonPathElement(value = \"Json1\", collapseJson = true)",
                "  public String value1;",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter",
                Joiner.on('\n').join(
                        STANDARD_RESULT_PACKAGE_AND_IMPORTS,
                        STANDARD_RESULT_HEADER,
                        "    in.beginObject();",
                        "    while (in.hasNext()) {",
                        "      switch(in.nextName()) {",
                        "        case \"Json1\":",
                        "          result.value1 = mGson.getAdapter(com.google.gson.JsonElement.class).read(in).toString();",
                        "          break;",
                        "        default:",
                        "          in.skipValue();",
                        "          break;",
                        "      }",
                        "    }",
                        "    in.endObject();",
                        STANDARD_RESULT_FOOTER
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void testGsonPathCustomType() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass",
                "public class Test {",
                "  @GsonPathElement(value = \"Json1\")",
                "  public java.util.Currency value1;",
                "}"
        ));

        JavaFileObject expectedSource = JavaFileObjects.forSourceString("test.Test_GsonTypeAdapter",
                Joiner.on('\n').join(
                        STANDARD_RESULT_PACKAGE_AND_IMPORTS,
                        STANDARD_RESULT_HEADER,
                        "    in.beginObject();",
                        "    while (in.hasNext()) {",
                        "      switch(in.nextName()) {",
                        "        case \"Json1\":",
                        "          result.value1 = mGson.getAdapter(java.util.Currency.class).read(in);",
                        "          break;",
                        "        default:",
                        "          in.skipValue();",
                        "          break;",
                        "      }",
                        "    }",
                        "    in.endObject();",
                        STANDARD_RESULT_FOOTER
                ));

        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .compilesWithoutError()
                .and()
                .generatesSources(expectedSource);
    }

    @Test
    public void testGsonPathRequiresAnnotation() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass(ignoreNonAnnotatedFields = true)",
                "public class Test {",
                "  public java.lang.Object element1;",
                "}"
        ));

        assertEmptyFile(source);
    }

    @Test
    public void testGsonPathInvalidType() {

        JavaFileObject source = JavaFileObjects.forSourceString("test.Test", Joiner.on('\n').join(
                STANDARD_PACKAGE_NAME,
                IMPORT_GSON_PATH_CLASS,
                IMPORT_GSON_PATH_ELEMENT,
                "@GsonPathClass",
                "public class Test {",
                "  @GsonPathElement(\"element1\")",
                "  public java.lang.Object element1;",
                "}"
        ));

        assertAbout(javaSource()).that(source)
                .processedWith(new GsonProcessor())
                .failsToCompile()
                .withErrorContaining("Invalid field type: java.lang.Object")
                .in(source).onLine(7);
    }
}
