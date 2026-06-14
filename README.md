# Stateful.co

[![EO principles respected here](https://www.elegantobjects.org/badge.svg)](https://www.elegantobjects.org)
[![DevOps By Rultor.com](https://www.rultor.com/b/sttc/stateful)](https://www.rultor.com/p/sttc/stateful)
[![We recommend IntelliJ IDEA](https://www.elegantobjects.org/intellij-idea.svg)](https://www.jetbrains.com/idea/)

[![Availability at SixNines](https://www.sixnines.io/b/0841)](https://www.sixnines.io/h/0841)
[![mvn](https://github.com/sttc/stateful/actions/workflows/mvn.yml/badge.svg)](https://github.com/sttc/stateful/actions/workflows/mvn.yml)

Stateful Web Primitives.

The service exposes two primitives over HTTP: atomic counters under `/c`
and concurrent locks under `/k`. The live deployment is at
[stateful.co](https://www.stateful.co); generated Javadoc lives at
[doc.stateful.co](https://doc.stateful.co); the in-app pages at
[stateful.co/counters](https://www.stateful.co/counters) and
[stateful.co/k](https://www.stateful.co/k) render the same documentation
shown below.

## Authentication

Sign in once on [stateful.co](https://www.stateful.co) through the
GitHub OAuth flow. Open [stateful.co/u](https://www.stateful.co/u) to
read your URN (in the form `urn:github:<id>`) and your security token,
and to rotate the token through the refresh link. Every REST call must
carry both as request headers:

```http
X-Sttc-URN: urn:github:526301
X-Sttc-Token: B28F-38E4-B305-C3A3
```

Requests without the two headers, or with a token that no longer
matches the one stored against the URN, receive HTTP 401.

## Counters

Counter names are 1 to 32 characters from `[0-9a-zA-Z-]`. An account is
capped at 64 counters. Create a counter with a form-encoded `POST` to
`/counters/add`:

```bash
curl -X POST https://www.stateful.co/counters/add \
  -H "X-Sttc-URN: urn:github:526301" \
  -H "X-Sttc-Token: B28F-38E4-B305-C3A3" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data "name=test"
```

Set the value to a signed integer of up to 32 digits with a `PUT` to
`/c/<name>/set?value=<int>`; the response is empty. Read the current
value or move it by an arbitrary delta with a `GET` to
`/c/<name>/inc?value=<int>`; the response body is the new value as
plain text, so a `value=0` increment reads the counter without mutating
it, and a negative value subtracts. Delete a counter with `/counters/delete?name=<name>`.

```bash
curl -X PUT "https://www.stateful.co/c/test/set?value=123" \
  -H "X-Sttc-URN: urn:github:526301" \
  -H "X-Sttc-Token: B28F-38E4-B305-C3A3"

curl "https://www.stateful.co/c/test/inc?value=0" \
  -H "X-Sttc-URN: urn:github:526301" \
  -H "X-Sttc-Token: B28F-38E4-B305-C3A3" \
  -H "Accept: text/plain"
```

## Locks

Lock names are 1 to 256 characters from `[0-9a-zA-Z._$-]`. An account
is capped at 4096 locks. Acquire a lock with a form-encoded `POST` to
`/k/lock`, passing the lock `name` and a `label` that identifies the
holder. The server replies HTTP 303 on success and HTTP 409 when the
lock is held by another label:

```bash
curl -X POST https://www.stateful.co/k/lock \
  -H "X-Sttc-URN: urn:github:526301" \
  -H "X-Sttc-Token: B28F-38E4-B305-C3A3" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  --data "name=deploy&label=worker-7"
```

Release the lock with `/k/unlock?name=<name>` and, optionally, a
matching `&label=<label>` that has to equal the holder label or the
call replies HTTP 409. Read the current holder label without releasing
with `/k/label?name=<name>`; the body is the label as plain text.

## Building from source

The build runs on Java 21 with Maven. Clone the repository and execute
the full Qulice profile that gates pull requests in CI:

```bash
mvn --batch-mode clean install -Pqulice
```
