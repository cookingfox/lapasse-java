Fixes - all modules:
- FIXME: provide hooks for clean up, e.g. unsubscribing / clearing listeners.

Wishlist - general:
- TODO: support event sourcing using message store implementation (separate module)

Wishlist for `lapasse`:
- TODO: support command & event base type (like state), so messages can be correctly filtered

Wishlist for `lapasse-compiler`:
- TODO: support omitting the `state` parameter in command & event handlers
- TODO: support non-strict parameter order (state & command / event) in command & event handlers
- TODO: support protected handler methods
- TODO: validate concrete state of handlers based on provided facade state
