package io.github.frostzie.nodex.services.config

import com.fasterxml.jackson.databind.JsonNode
import io.github.frostzie.nodex.api.config.Migration
import io.github.frostzie.nodex.utils.ModVersionUtils

/**
 * Service responsible for transforming [JsonNode] data across version boundaries.
 */
class MigrationService : Migration {
    private val migrations = mutableListOf<MigrationEntry>()

    private data class MigrationEntry(
        val fromVersion: String,
        val action: (JsonNode) -> JsonNode
    )

    /**
     * Registers a migration step targeting a specific version.
     *
     * @param fromVersion The version that has this migration rule.
     * @param action The transformation logic.
     */
    override fun register(fromVersion: String, action: (JsonNode) -> JsonNode) {
        migrations.add(MigrationEntry(fromVersion, action))
    }

    /**
     * Migrates the given [node] from [storedVersion] to the latest registered version.
     *
     * Applies all migrations set after [storedVersion], sorted in ascending order.
     */
    override fun migrate(node: JsonNode, storedVersion: String): JsonNode {
        var currentNode = node

        // Only care about migrations newer than what's stored in the config (idc about backwards compatibility rn)
        val applicable = migrations
            .filter { ModVersionUtils.isNewerThan(it.fromVersion, storedVersion) }
            .sortedWith { m1, m2 -> ModVersionUtils.compare(m1.fromVersion, m2.fromVersion) }

        applicable.forEach { migration ->
            currentNode = migration.action(currentNode)
        }

        return currentNode
    }
}
