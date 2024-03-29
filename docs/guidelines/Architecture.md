# Architecture

Ivy Wallet follows a less constrained version of [the official Android Developer's guide to app architecture by Google](https://developer.android.com/topic/architecture).

**Data Mapping:** _Raw model → Domain model → ViewState model_

![data-mapping](../assets/data-mapping.svg)

> "Programming is a game of information. We receive/send data on which we perform arbitrary transformations and logic." — Iliyan Germanov

**Architecture:** _Data Layer → Domain Layer (optional) → UI layer_

![architecture](../assets/architecture.svg)

## Data Layer

The Data Layer is responsible for dealing with the outside world and mapping it to your domain. It does IO operations (network calls, fyle system, device hardware and sensors) and maps them into valid domain model by executing the required business logic and rules.

### Data source (optional)

Wraps an IO operation (e.g. a Ktor http call) and ensures that it won't throw exceptions. 

> A data source isn't always needed if it'll do nothing useful. For example, there's no point wrapping Room DB DAOs.

### Domain Mapper classes (optional)

A classes responsible for transforming and validating raw models (e.g. DTOs, entities) to domain ones. The validations can fail so mappers usually return `Either<Error, DomainModel>`.

### Repository

Combines one or many data sources to implement [CRUD operations](https://en.wikipedia.org/wiki/Create,_read,_update_and_delete) and provide validated domain data.

## Domain Layer

### UseCases

## UI Layer

### ViewModel

### ViewState Mapper classes (optional)

### Composables
