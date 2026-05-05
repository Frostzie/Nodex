package io.github.frostzie.nodex.settings.validation

import com.fasterxml.jackson.databind.JsonNode

data class ValidationIssue(
    val path: String,
    val reason: String,
    val oldValue: JsonNode?,
    val newValue: JsonNode?
)
