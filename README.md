<div align="center">
    <img src="src/main/resources/META-INF/resources/static/images/kamifusen-logo.png">
</div>

---

![GitHub Release](https://img.shields.io/github/v/release/tohuwabohu-io/kamifusen) ![Coverage](https://raw.githubusercontent.com/tohuwabohu-io/kamifusen/badges/jacoco.svg)

# kamifusen

> A simple page hit counter written in kotlin.

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: <https://quarkus.io/>.

## First time setup
For development, a default user with username/password `admin` exists. You will be prompted to change your
password after your first login. For production, access the database and execute the given `insert.sql` in
`src/main/resources`.

## API Key creation
Issue a new API Key on: http://localhost:8080/users

The new Key will be visible only once. Copy and distribute it according to your needs. Do not use the same API Key across
multiple domains (or do, it's not like I can stop you).

## Registering pages
You need to add some JavaScript to your page if you want the hit counter to work. Use this snippet provided:

```html
<script language="JavaScript">
    document.addEventListener('DOMContentLoaded', function () {
    const url = new URL(window.location.href);

    fetch('http://localhost:8080/public/visits/hit', {
        method: 'POST',
        headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Basic <API-KEY>'
        },
        body: JSON.stringify({
            path: url.pathname,
            domain: url.hostname
        })
    });
})
</script>
```

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at <http://localhost:8080/q/dev/>.

## Packaging and running the application

The application can be packaged using:

```shell script
./gradlew build
```

It produces the `quarkus-run.jar` file in the `build/quarkus-app/` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `build/quarkus-app/lib/` directory.

The application is now runnable using `java -jar build/quarkus-app/quarkus-run.jar`.

If you want to build an _über-jar_, execute the following command:

```shell script
./gradlew build -Dquarkus.package.jar.type=uber-jar
```

The application, packaged as an _über-jar_, is now runnable using `java -jar build/*-runner.jar`.

## Creating a native executable

You can create a native executable using:

```shell script
./gradlew build -Dquarkus.native.enabled=true
```

Or, if you don't have GraalVM installed, you can run the native executable build in a container using:

```shell script
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

You can then execute your native executable with: `./build/kamifusen-0.0.1-runner`

If you want to learn more about building native executables, please consult <https://quarkus.io/guides/gradle-tooling>.

## Related Guides

- Kotlin ([guide](https://quarkus.io/guides/kotlin)): Write your services in Kotlin

## Provided Code

### REST

Easily start your REST Web Services

[Related guide section...](https://quarkus.io/guides/getting-started-reactive#reactive-jax-rs-resources)
