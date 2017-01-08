package gsonpath.model

import java.util.LinkedHashMap

class MandatoryFieldInfoFactory {

    fun createMandatoryFieldsFromGsonObject(gsonObject: GsonObject): Map<String, MandatoryFieldInfo> {
        val mandatoryInfoMap = LinkedHashMap<String, MandatoryFieldInfo>()
        createMandatoryFieldsFromGsonObject(gsonObject, mandatoryInfoMap)
        return mandatoryInfoMap
    }

    /**
     * Add any mandatory field indexes as constants. This is done for code readability.
     * We will obtain the values using a depth-first recursion.
     */
    private fun createMandatoryFieldsFromGsonObject(gsonObject: GsonObject, mandatoryInfoMap: MutableMap<String, MandatoryFieldInfo>) {
        gsonObject.keySet()
                .map { gsonObject[it]!! }
                .forEach {
                    if (it is GsonField) {
                        //
                        // For all required fields we add an index field so we can easily check whether the
                        // value has been assigned after the json has been parsed.
                        //
                        if (it.isRequired) {
                            val fieldName = it.fieldInfo.fieldName
                            val mandatoryFieldIndexName = "MANDATORY_INDEX_" + fieldName.toUpperCase()

                            // Keep track of the information for later use. Since this is a linked list, we keep track of insert order.
                            mandatoryInfoMap[fieldName] = MandatoryFieldInfo(mandatoryFieldIndexName, it)
                        }

                    } else {
                        // Recursive call, navigating further down the tree.
                        createMandatoryFieldsFromGsonObject(it as GsonObject, mandatoryInfoMap)
                    }
                }
    }

}