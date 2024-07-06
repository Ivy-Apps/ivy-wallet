# Architecture

Ivy Wallet follows a less constrained version of [the official Android Developer's guide to app architecture by Google](https://developer.android.com/topic/architecture).

**Data Mapping:** _Raw model → Domain model → ViewState model_

![data-mapping](../assets/data-mapping.svg)

> "Programming is a game of information. We receive and send data, on which we perform arbitrary transformations and logic." — Iliyan Germanov

**Architecture:** _Data layer → Domain layer (optional) → UI layer_

![architecture](../assets/architecture.svg)

## Data Layer

The Data Layer is responsible for dealing with the outside world and mapping it to your domain. It does IO operations (network calls, file system, device hardware and sensors) and maps them into valid domain model by executing the required business logic and rules.

### Data source (optional)

Wraps an IO operation (e.g., a Ktor http call) and ensures that it won't throw exceptions by making it a total function (i.e. wraps with `try-catch` and returns `Either<ErrorDto, DataDto>` of some raw data model).

> A data source isn't always needed if it'll do nothing useful. For example, there's no point wrapping Room DB DAOs.

### Domain Mapper classes (optional)

A class responsible for transforming and validating raw models (e.g., DTOs, entities) to domain ones. These validations can fail, so mappers usually return `Either<Error, DomainModel>`.

### Repository

Combines one or many data sources to implement [CRUD operations](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) and provide validated domain data. Repository functions must be **main-safe** (not blocking the main UI thread), or simply said, they must move work on a background thread (e.g., `withContext(Disparchers.IO)`)

## Domain Layer (optional)

Optional architecture layer for more complex domain logic that combines one or many repositories with business logic and rules (e.g., calculating the balance in Ivy Wallet).

### UseCases

Encapsulates the logic for a single operation from your domain. Nothing special just combines one or many repositories with some business logic.

## UI Layer

The user of the app sees and interacts only with the UI layer. The UI layer consists of screens, composable components, view-state mappers and presentation logic.

### ViewModel

The ViewModel combines the data from use cases and repositories and transforms it into view-state representation that's formatted and ready to display in your Compose UI. It also handles user interactions and translates them into data/domain layer calls.

> Simply said, the viewmodel is translator between the UI (user) and the domain. It's like an adapter - adapts domain models to view-state and adapts user interactions into domain calls.

### ViewState Mapper classes (optional)

In more complex cases, it becomes impractical to put all domain -> view-state mapping in the ViewModel. Also, it's common for multiple viewmodels to map the same domain model to the same view-state. In that case, it's good to extract the view-state mapping logic in a separate class that we call a `SomethingViewStateMapper`.

### Composables

Composables are the screens and UI components that the user sees and interacts with. They should be dumb as fck. Their responsibility and logic should be limited to:
- Displaying the already formatted view-state provided by the VM.
- Sending UI interactions to the VM in the form of events.
