# A Cross Platform UUID for K/N

[ ![Download](https://api.bintray.com/packages/bschramke/maven/uuid/images/download.svg?version=0.0.1) ](https://bintray.com/bschramke/maven/uuid/0.0.1/link)

K/N doesn't have a UUID yet. This brings a random (v4) UUID that matches UUIDs on various platforms:

- iOS/Mac: `NSUUID`
- Java: `java.util.UUID` (specifically `randomUUID`)

### This `UUID` is:

- Frozen
- Thread-safe (thread-safe randomness in native)
- Adheres to RFC4122 (version 4 UUID)
- Tested
- Tested against macOS/iOS UUID to verify correctness
