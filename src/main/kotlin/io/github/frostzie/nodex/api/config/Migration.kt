package io.github.frostzie.nodex.api.config

import com.fasterxml.jackson.databind.JsonNode

/**
 * Transforms [JsonNode] data across version boundaries.
 */
interface Migration {
    /**
     * Registers a migration step targeting a specific version.
     *
     * @param fromVersion The version that has this migration rule.
     * @param action The transformation logic.
     */
    fun register(fromVersion: String, action: (JsonNode) -> JsonNode)

    /**
     * Migrates the given [node] from [storedVersion] to the latest registered version.
     *
     * Applies all migrations set after [storedVersion], sorted in ascending order.
     */
    fun migrate(node: JsonNode, storedVersion: String): JsonNode
}
