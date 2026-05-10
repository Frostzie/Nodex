package io.github.frostzie.nodex.settings.validation

import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.settings.schema.SettingSpec
import tools.jackson.databind.JsonNode
import tools.jackson.databind.node.JsonNodeFactory
import tools.jackson.databind.node.ObjectNode
import tools.jackson.module.kotlin.jsonMapper

/**
 * For validating settings JSON.
 */
object SettingsValidator {
    private val mapper = jsonMapper()

    fun sanitize(
        rootNode: JsonNode,
        defaults: AppSettings,
        specs: List<SettingSpec>
    ): ValidationResult {
        val sanitizedRoot = if (rootNode is ObjectNode) {
            rootNode.deepCopy()
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
                parent.set(fieldName, defaultNode)
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
                current.set(segment, created)
                current = created
            }
        }
        return current
    }

}
