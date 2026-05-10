package io.github.frostzie.nodex.services.settings

import io.github.frostzie.nodex.domain.settings.AppSettings
import io.github.frostzie.nodex.settings.registry.SettingsRegistry
import io.github.frostzie.nodex.settings.registry.SettingsStores
import io.github.frostzie.nodex.settings.schema.SettingSpec
import io.github.frostzie.nodex.settings.validation.SettingsValidationRules
import io.github.frostzie.nodex.settings.validation.SettingsValidator
import io.github.frostzie.nodex.settings.validation.ValidationIssue
import io.github.frostzie.nodex.settings.validation.ValidationResult
import tools.jackson.databind.JsonNode
import tools.jackson.module.kotlin.jsonMapper
import tools.jackson.module.kotlin.kotlinModule

/**
 * Stateful settings validation service.
 *
 * Takes [SettingsRegistry] for spec lookups.
 */
class SettingsValidationService(
    private val registry: SettingsRegistry,
    private val storeId: String = SettingsStores.CORE
) {
    private val mapper = jsonMapper { addModules(kotlinModule()) }
    private val defaults = AppSettings()

    fun validate(node: JsonNode, defaults: AppSettings, storeIdOverride: String? = null): ValidationResult {
        return SettingsValidator.sanitize(
            node, defaults, resolveSpecs(storeIdOverride)
        )
    }

    /**
     * Validates staged settings against all registered specs.
     * Used at Apply/Save time to block commits with invalid values.
     */
    fun validateStaged(staged: AppSettings, storeIdOverride: String? = null): ValidationResult {
        val issues = mutableListOf<ValidationIssue>()
        val specs = resolveSpecs(storeIdOverride)
        for (spec in specs) {
            val value = spec.defaultGetter(staged)
            val result = SettingsValidationRules.validateValue(value, spec)
            if (!result.isValid) {
                val defaultValue = spec.defaultGetter(defaults)
                issues.add(
                    ValidationIssue(
                        path = spec.id,
                        reason = result.reason ?: "invalid",
                        oldValue = mapper.valueToTree<JsonNode>(value),
                        newValue = mapper.valueToTree<JsonNode>(defaultValue)
                    )
                )
            }
        }
        return ValidationResult(
            mapper.valueToTree<JsonNode>(staged),
            issues
        )
    }

    private fun resolveSpecs(storeIdOverride: String?): List<SettingSpec> {
        val resolvedStore = storeIdOverride ?: storeId
        return registry.specsByStore(resolvedStore)
    }
}