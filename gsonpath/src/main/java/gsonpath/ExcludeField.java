package gsonpath;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that the annotated field will not be added to the auto generated
 * {@link com.google.gson.TypeAdapter} when using the {@link gsonpath.AutoGsonAdapter}
 * annotation.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
@Inherited
public @interface ExcludeField {
}
