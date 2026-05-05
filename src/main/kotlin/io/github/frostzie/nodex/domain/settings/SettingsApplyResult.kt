package io.github.frostzie.nodex.domain.settings

import io.github.frostzie.nodex.settings.validation.ValidationIssue

/**
 * Result of an apply/save operation.
 */
sealed class ApplyResult {
    data object Success : ApplyResult()
    data class Failure(val issues: List<ValidationIssue>) : ApplyResult()
}
