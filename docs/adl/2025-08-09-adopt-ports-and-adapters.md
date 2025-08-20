# Adopt Ports and Adapters (Hexagonal) Architecture instead of Layered Architecture

## Context and Problem Statement

We currently follow a classic layered architecture which leads to increased coupling between business logic and 
technical concerns. This slows down change, complicates testing, and makes it hard to evolve or swap infrastructure 
without touching domain code.

## Considered Options

* Keep classic layered architecture
* Adopt Ports and Adapters (a.k.a. Hexagonal Architecture)

## Decision Outcome

Chosen option: "Adopt Ports and Adapters", because it prioritizes domain independence and testability, allows 
infrastructure to be swapped via adapters, and provides a clear dependency rule (only inward) which can be enforced with 
ArchUnit rules.

### Consequences

* Good, because the domain is isolated behind ports, enabling:
  * Easier unit testing of application and domain services
  * Replaceable infrastructure by implementing new adapters
  * Clearer boundaries and reduced accidental coupling between layers
  * More stable domain model with fewer ripple effects when technical details change
* Good, because dependency direction is explicit
* Bad, because there is upfront refactoring cost
* Bad, because there is added indirection, which may feel verbose for simple features

Decision date: 2025-08-09
