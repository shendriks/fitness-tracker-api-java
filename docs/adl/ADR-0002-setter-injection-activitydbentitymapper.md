# \[ADR-0002\] Use Setter Injection in `ActivityDbEntityMapper` to enable simple Unit Testing

## Context and Problem Statement

`ActivityDbEntityMapper` is a MapStruct-generated component that maps `ActivityDbEntity` to domain objects and vice 
versa. Some mapping logic depends on `java.time.Clock` (e.g. choosing `Instant.now(Clock)` when GPX time is absent). We 
want to write plain unit tests for the mapper without spinning up a Spring Boot context. In those tests, we need an easy 
way to supply a controllable clock.

Constructor injection did not work as expected for this MapStruct mapper. The concrete implementation class is generated 
by MapStruct and didn't contain an appropiate constructor despite all needed annotations were set. Also, in plain unit 
tests we don't rely on Spring to construct and inject dependencies.

## Considered Options

* Setter injection for `Clock` on the mapper
* Constructor injection
* Field injection
* Provide `Clock` as a method parameter to mapping methods

## Decision Outcome

Chosen option: "Setter injection", because:
* In unit tests we can easily obtain the MapStruct-generated implementation via the MapStruct factory and set the clock through a public setter
* Spring can still inject the clock via the annotated setter
 
### Alternative approaches considered

* Constructor injection keeps dependencies immutable but didn't work for our simple unit test scenario
* Field injection is harder to manage and mock in plain unit tests
* Passing the clock as a method parameter pollutes mapping signatures

### Consequences

* Good, because unit tests can set a deterministic clock without bootstrapping Spring
* Good, because code continues to receive the clock from Spring via `@Autowired` on the setter
* Good, because it aligns with MapStruct's support for setter-based injection
* Bad, because setter injection introduces mutability, the dependency can be changed after construction if misused
* Bad, because if someone forgets to set the clock in a plain unit test, it could be null and cause null pointer exceptions

Decision date: 2025-08-23
