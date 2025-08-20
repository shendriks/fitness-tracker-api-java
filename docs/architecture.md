# Architecture
## Ports and Adapters
### Folder Structure

```plain
fitnesstrackerapi
├-- auth
|   ├-- adapter
|   |   ├-- in
|   |   |   └-- rest
|   |   └-- out
|   |       └-- jpa
|   └--- core
|        ├-- application
|        ├-- port
|        |   ├-- in
|        |   └-- out
|        └-- domain
|            ├-- entity
|            └-- value
├-- activity
|   ├-- adapter
|   |   ├-- in
|   |   |   └-- rest
|   |   └-- out
|   |       └-- jpa
|   └--- core
|        ├-- application
|        ├-- port
|        |   ├-- in
|        |   └-- out
|        └-- domain
|            ├-- entity
|            └-- value
├-- gameification
|   ├-- adapter
|   |   ├-- in
|   |   |   └-- rest
|   |   └-- out
|   |       └-- jpa
|   └--- core
|        ├-- application
|        ├-- port
|        |   ├-- in
|        |   └-- out
|        └-- domain
|            ├-- entity
|            └-- value
├-- notification
|   ├-- adapter
|   |   ├-- in
|   |   |   └-- rest
|   |   └-- out
|   |       └-- jpa
|   └--- core
|        ├-- application
|        ├-- port
|        |   ├-- in
|        |   └-- out
|        └-- domain
|            ├-- entity
|            └-- value
⋮
```

```plain
fitnesstrackerapi
├-- adapter
|   ├-- in
|   |   └-- rest
|   |       ├-- activity
|   |       ├-- challenge
|   |       ⋮
|   └-- out
|       └-- jpa
|           ├-- activity
|           ├-- challenge
|           ⋮
└--- core
     ├-- application
     |   ├-- activity
     |   ├-- challenge
     |   ⋮
     ├-- port
     |   ├-- in
     |   |   ├-- activity
     |   |   ├-- challenge
     |   |   ⋮
     |   └-- out
     |       ├-- activity
     |       ├-- challenge
     |       ⋮
     └-- domain
         ├-- activity
         |   ├-- entity
         |   └-- value
         ├-- challenge
         |   ├-- entity
         |   └-- value
         ⋮
```
### "Domains"

* activity
* challenge
* milestone
* notification
* trophy
* user
