# Ivy Developer Guidelines

A short guide that'll evolve our time with one and only goal - to make you a better developer.

[![PRs welcome!](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/ILIYANGERMANOV/ivy-wallet/blob/main/CONTRIBUTING.md)

> Feedback: Welcome! 

> Proposals: Highly appreciated.

## Ivy Architecture

```mermaid
graph TD;

android(Android System)
user(User)
view(UI)
event(Event)
viewmodel(ViewModel)
action(Action)
io(IO side-effects)
pure(Pure)

event -- Propagated --> viewmodel
viewmodel -- Triggers --> action
viewmodel -- "New State (Flow)" --> view
action -- Abstracts --> io
action -- "Composition" --> action
io -- "Side-Effect abstraction" --> pure
pure -- "New State (Data)" --> viewmodel
pure -- "Composition" --> pure

user -- Interracts --> view
view -- Produces --> event
android -- Produces --> event

```

## I. Domain (Business Logic)

We classify business logic as any domain-specific logic that: is neither UI nor Android stuff nor IO (persistence or network calls).

Now knowing what the `domain` isn't, lets define what it is

# _WIP...._

### 1. Functional Programming (pure)

### 2. Actions (use-cases)

### 3. ViewModel

## II. UI

### `:ivy-design`

## III. Data model

## IV. IO (network + persistence)