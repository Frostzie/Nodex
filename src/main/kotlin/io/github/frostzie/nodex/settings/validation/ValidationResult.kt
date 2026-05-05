package io.github.frostzie.nodex.settings.validation

import com.fasterxml.jackson.databind.JsonNode

data class ValidationResult(
    val sanitizedNode: JsonNode,
    val issues: List<ValidationIssue>
)
