# Tor Exit Nodes

Design and implement a service checking whether provided IPv4 address is a [Tor](https://www.torproject.org/) exit node.

API of the service should:
- respond with `HTTP 200 OK` and empty body on `HEAD /A.B.C.D` request when provided IP is a Tor exit node
- respond with `HTTP 404 Not Found` and empty body on `HEAD /A.B.C.D` request when provided IP *is not* a Tor exit node
- (nice to have) respond with `HTTP 200 OK` and JSON body (design the response) on `GET /A.B.C.D` request when provided IP is a Tor exit node
- (nice to have) respond with `HTTP 404 Not Found` and empty body on `GET /A.B.C.D` request when provided IP *is not* a Tor exit node

Points above represent minimal required specification of the service's API - for example you are free to use additional HTTP status codes if it makes sense.

You can find a list of current Tor exit nodes here: https://check.torproject.org/exit-addresses.

## Non-functional requirements

- the service should:
  - be dockerized
  - cache Tor exit nodes for a specified amount of time
  - include a health check
- readme should be replaced with brief notes covering:
  - service description with all made assumptions
  - tech stack used (runtime environment, frameworks, key libraries)
  - how to:
    - build the service
    - run automatic tests
    - run the service locally
  - what improvements would you make if you had more time

## Evaluation criteria

- alignment with the requirements
- usage of best practices when dealing with edge cases that are not covered here
- code quality and readability
- presence and quality of (or lack of) automatic tests
- commit history (thought process, commit messages)

Your work should be handed off in a form of a PR to the private repository that you were given. You are responsible for picking a deadline for its delivery, communicating it and sticking to it.
