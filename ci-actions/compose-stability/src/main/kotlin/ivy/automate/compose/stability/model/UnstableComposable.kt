package ivy.automate.compose.stability.model

typealias FullyQualifiedName = String

data class UnstableComposable(
    val fullyQualifiedName: FullyQualifiedName,
    val name: String,
    val skippable: Boolean,
    val restartable: Boolean,
    val unstableArguments: Set<ComposableArgument>
)

data class ComposableArgument(
    val name: String,
    val type: String
)