package io.github.frostzie.nodex.settings.validation

import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.domain.settings.SettingValueType
import tools.jackson.databind.JsonNode
import tools.jackson.module.kotlin.jsonMapper

object SettingsValidationRules {
    private val mapper = jsonMapper()

    data class Result(val isValid: Boolean, val reason: String? = null)

    fun validateValue(value: Any?, spec: SettingSpec): Result {
        val node = if (value == null) null else mapper.valueToTree<JsonNode>(value)
        return validateNode(node, spec)
    }

    fun validateNode(node: JsonNode?, spec: SettingSpec): Result {
        if (node == null || node.isNull) {
            return Result(false, "missing")
        }

        if (!isTypeValid(node, spec)) {
            return Result(false, "type")
        }

        if (!isConstraintValid(node, spec)) {
            return Result(false, "constraints")
        }

        return Result(true)
    }

    private fun isTypeValid(node: JsonNode, spec: SettingSpec): Boolean {
        return when (spec.valueType) {
            SettingValueType.BOOLEAN -> node.isBoolean
            SettingValueType.INT -> node.isInt
            SettingValueType.DOUBLE -> node.isFloatingPointNumber
            SettingValueType.STRING -> node.isString
            SettingValueType.ENUM -> node.isString && spec.enumValues.contains(node.asString())
            SettingValueType.COLOR -> node.isObject
        }
    }

    private fun isConstraintValid(node: JsonNode, spec: SettingSpec): Boolean {
        val constraints = spec.constraints
        if (constraints.required && isEmptyRequired(node)) return false

        if (node.isString) {
            val text = node.asString()
            constraints.minLength?.let { if (text.length < it) return false }
            constraints.maxLength?.let { if (text.length > it) return false }
            constraints.regex?.let { if (!Regex(it).matches(text)) return false }
        }

        if (node.isNumber) {
            val numeric = node.asDouble()
            constraints.minNumeric?.let { if (numeric < it) return false }
            constraints.maxNumeric?.let { if (numeric > it) return false }
        }

        return true
    }

    private fun isEmptyRequired(node: JsonNode): Boolean {
        if (node.isMissingNode || node.isNull) return true
        if (node.isString) return node.asString().isBlank()
        return false
    }
}
