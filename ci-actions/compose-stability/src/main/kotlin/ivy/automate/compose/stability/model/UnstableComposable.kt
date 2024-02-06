package ivy.automate.compose.stability.model

data class UnstableComposable(
    val fullyQualifiedName: String,
    val name: String,
    val skippable: Boolean,
    val restartable: Boolean,
    val unstableArguments: List<Argument>
)

data class Argument(
    val name: String,
    val type: String
)