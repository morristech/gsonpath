package gsonpath.generator.standard

import com.google.gson.FieldNamingPolicy
import gsonpath.GsonFieldValidationType
import gsonpath.PathSubstitution

internal class AutoGsonAdapterProperties(
        val fieldsRequireAnnotation: Boolean,
        val flattenDelimiter: Char,
        val serializeNulls: Boolean,
        val rootField: String,
        val gsonFieldValidationType: GsonFieldValidationType,
        val gsonFieldNamingPolicy: FieldNamingPolicy,
        val pathSubstitutions: Array<PathSubstitution>)
