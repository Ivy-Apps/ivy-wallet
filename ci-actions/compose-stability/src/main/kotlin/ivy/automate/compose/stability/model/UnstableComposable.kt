package ivy.automate.compose.stability.model

data class UnstableComposable(
    val fullyQualifiedName: String,
    val name: String,
    val skippable: Boolean,
    val restartable: Boolean,
    val unstableArguments: List<ComposableArgument>
)

data class ComposableArgument(
    val name: String,
    val type: String
)