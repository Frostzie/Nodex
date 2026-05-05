package io.github.frostzie.nodex.settings.validation

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.ObjectNode
import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.settings.schema.SettingSpec

/**
 * For validating settings JSON.
 */
object SettingsValidator {
    private val mapper = ObjectMapper()

    fun sanitize(
        rootNode: JsonNode,
        defaults: AppSettings,
        specs: List<SettingSpec>
    ): ValidationResult {
        val sanitizedRoot = if (rootNode is ObjectNode) {
            rootNode.deepCopy<ObjectNode>()
        } else {
            JsonNodeFactory.instance.objectNode()
        }

        val issues = mutableListOf<ValidationIssue>()
        specs.forEach { spec ->
            val path = spec.id.split('.')
            val parent = ensureParentObject(sanitizedRoot, path.dropLast(1))
            val fieldName = path.last()
            val node = parent.get(fieldName)

            val validation = SettingsValidationRules.validateNode(node, spec)
            if (!validation.isValid) {
                val defaultValue = spec.defaultGetter(defaults)
                val defaultNode = mapper.valueToTree<JsonNode>(defaultValue)
                parent.set<JsonNode>(fieldName, defaultNode)
                issues.add(
                    ValidationIssue(
                        path = spec.id,
                        reason = validation.reason ?: "invalid",
                        oldValue = node,
                        newValue = defaultNode
                    )
                )
            }
        }

        return ValidationResult(sanitizedRoot, issues)
    }

    private fun ensureParentObject(root: ObjectNode, path: List<String>): ObjectNode {
        var current = root
        for (segment in path) {
            val existing = current.get(segment)
            if (existing is ObjectNode) {
                current = existing
            } else {
                val created = JsonNodeFactory.instance.objectNode()
                current.set<ObjectNode>(segment, created)
                current = created
            }
        }
        return current
    }

}
