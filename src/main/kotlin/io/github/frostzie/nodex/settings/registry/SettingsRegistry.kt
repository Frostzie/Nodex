package io.github.frostzie.nodex.settings.registry

import io.github.frostzie.nodex.settings.schema.SettingSpec

/**
 * Dynamic registry for settings specs and categories.
 *
 * Settings owners (core only for now) register specs under
 * an owner ID and can unregister them later.
 *
 * Thread-safe via synchronized access.
 */
class SettingsRegistry {
    private val specsByOwner = mutableMapOf<String, List<SettingSpec>>()
    private val categories = mutableMapOf<String, CategorySpec>()

    // Spec

    /**
     * Registers [specs] under the given [ownerId].
     * If the owner already has registered specs, they are replaced.
     */
    @Synchronized
    fun registerSpecs(ownerId: String, specs: List<SettingSpec>) {
        val internalDuplicates = specs.groupBy { it.id }.filterValues { it.size > 1 }.keys
        require(internalDuplicates.isEmpty()) {
            "Duplicate spec IDs within $ownerId: ${internalDuplicates.joinToString(", ")}"
        }

        val existingIds = specsByOwner
            .filterKeys { it != ownerId }
            .values
            .flatten()
            .map { it.id }
            .toSet()
        val duplicates = specs.map { it.id }.filter { it in existingIds }.toSet()
        require(duplicates.isEmpty()) {
            "Duplicate setting spec IDs registered by $ownerId: ${duplicates.joinToString(", ")}"
        }
        specsByOwner[ownerId] = specs
    }

    /**
     * Unregisters specs for the given [ownerId].
     */
    @Synchronized
    fun unregister(ownerId: String) {
        specsByOwner.remove(ownerId)
    }

    // Category

    /**
     * Registers a category definition. Replaces any existing category with the same ID.
     */
    @Synchronized
    fun registerCategory(category: CategorySpec) {
        categories[category.id] = category
    }

    /**
     * Unregisters a category by ID.
     */
    @Synchronized
    fun unregisterCategory(categoryId: String) {
        categories.remove(categoryId)
    }

    // Queries

    /**
     * Returns all registered specs across all owners.
     */
    @Synchronized
    fun allSpecs(): List<SettingSpec> = specsByOwner.values.flatten()

    /**
     * Returns the spec with the given [id], or `null` if not found.
     */
    @Synchronized
    fun specById(id: String): SettingSpec? = allSpecs().find { it.id == id }

    /**
     * Returns all specs belonging to the given [categoryId].
     */
    @Synchronized
    fun specsByCategory(categoryId: String): List<SettingSpec> =
        allSpecs().filter { it.categoryId == categoryId }

    /**
     * Returns all registered categories.
     */
    @Synchronized
    fun allCategories(): List<CategorySpec> = categories.values.toList()

    /**
     * Returns direct children of the category with the given [parentId].
     */
    @Synchronized
    fun childrenOf(parentId: String?): List<CategorySpec> =
        categories.values.filter { it.parentId == parentId }

    /**
     * Returns all specs that belong to the given [storeId].
     */
    @Synchronized
    fun specsByStore(storeId: String): List<SettingSpec> {
        val categoryById = categories
        return allSpecs().filter { spec ->
            categoryById[spec.categoryId]?.storeId == storeId
        }
    }

    /**
     * Returns all specs owned by the given [ownerId].
     */
    @Synchronized
    fun specsByOwner(ownerId: String): List<SettingSpec> = specsByOwner[ownerId].orEmpty()
}
