package io.github.frostzie.nodex.settings.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.domain.settings.SettingValueType

object SettingsValidationRules {
    private val mapper = ObjectMapper()

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
            SettingValueType.STRING -> node.isTextual
            SettingValueType.ENUM -> node.isTextual && spec.enumValues.contains(node.asText())
            SettingValueType.COLOR -> node.isObject
        }
    }

    private fun isConstraintValid(node: JsonNode, spec: SettingSpec): Boolean {
        val constraints = spec.constraints
        if (constraints.required && isEmptyRequired(node)) return false

        if (node.isTextual) {
            val text = node.asText()
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
        if (node.isTextual) return node.asText().isBlank()
        return false
    }
}
