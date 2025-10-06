# [ADR-0005] Keep Instantaneous Speed on GPS Position

## Context and Problem Statement

We need to represent instantaneous speed samples that are captured along with GPS points when importing GPX data.
There are multiple ways to model this in the domain and persistence layers. The question is: should we introduce a new
time series (e.g., SpeedAtTime) or simply enrich the existing GPSPosition with a speed field?

## Considered Options

* Store current speed on the existing GPSPosition (one structure per sample)
* Introduce a separate SpeedAtTime/SpeedSample data structure and collection on Activity
* Do not persist speed at all and compute it on-the-fly from adjacent GPS points

## Decision Outcome

Chosen option: "Store current speed on the existing GPSPosition (one structure per sample)", because it follows the KISS
principle and keeps the model cohesive: in our data sources (GPX), speed belongs to the same timestamp as the position.
It avoids an additional collection and mapping layer, reduces persistence complexity, and keeps read/write paths
straightforward.

### Consequences

* Good, because the domain model remains simple (one list of GPSPosition with all per-sample attributes, including
  speed)
* Good, because persistence is simpler: no extra table/relationship and fewer mapper conversions
* Good, because reads are efficient for common use cases (rendering pace/speed alongside positions or generating
  previews)
* Good, because transactional consistency is trivial; speed and position of the same sample are saved together
* Bad, because speed samples cannot exist independently of GPS positions (less flexible if we ever ingest speed-only
  data)
* Bad, because changing sampling/correction logic couples position and speed together; refactors might touch a single
  structure
* Bad, because recomputing speed with new algorithms requires updating positions or a migration rather than swapping a
  series

Decision date: 2025-10-05
