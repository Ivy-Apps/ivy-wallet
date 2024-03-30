# Screen Architecture

Ivy Wallet uses a [Unidirectional Data Flow (UDF)](https://developer.android.com/topic/architecture#unidirectional-data-flow),
MVI architecture with the Compose runtime in the view-model. 
It key characteristics are:

- The VM produces a single view-state model with all information for the screen.
- The UI composables directly display the view-state provided by the VM.
- The user interacts with the Compose UI and the UI transforms user's interactions to events and sends them to the VM.
- The VM handles the events coming from the UI and produces a new view-state.

## References

- [Modern Compose Architecture with Circuit by Slack](https://youtu.be/ZIr_uuN8FEw?si=sulxyqta5dZn-L11)
- [Reactive UI state on Android, starring Compose by Reddit](https://www.reddit.com/r/RedditEng/s/WhIYLJUzNR)
- [The Circuit - Compose-driven Architecture for Kotlin by Slack](https://youtu.be/bMJocp969Bo?si=ab9UrAW1HSwm5sGV)
