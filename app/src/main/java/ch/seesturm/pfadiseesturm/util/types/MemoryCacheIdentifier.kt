package ch.seesturm.pfadiseesturm.util.types

import kotlinx.serialization.Serializable

@Serializable
enum class MemoryCacheIdentifier {
    ForceReload,
    TryGetFromListCache,
    TryGetFromHomeCache
}