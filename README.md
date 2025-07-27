# AnyMapper

AnyMapper is a lightweight Java library for transforming deeply nested Map<String, Object> structures into different shapes based on a declarative mapping configuration. It supports both flat and nested remapping of keys and values, including intelligent handling of List<Map<...>> structures. Designed for flexibility and extensibility, AnyMapper simplifies data transformation pipelines without the need for boilerplate code or rigid DTO classes.

Use cases
  - Mapping external API payloads to internal formats
  - Normalizing deeply nested data for indexing (e.g., Elasticsearch)
  - Extracting or flattening complex JSON-like maps
  - Structuring data before serialization or persistence


- [AnyMapper](#anymapper)
    * [✨ Features](#-features)
    * [🔧 Installation](#-installation)
        + [Gradle](#gradle)
        + [Maven](#maven)
    * [📦 Usage](#-usage)
        + [Example Input](#example-input)
        + [Mapping Configuration](#mapping-configuration)
        + [Transformation](#transformation)
        + [Result](#result)
    * [Behavior](#-behavior)
        + [If `type=list` is not specified](#if-typelist-is-not-specified)
        + [List-to-Map and Map-to-List conversion is **not supported**](#list-to-map-and-map-to-list-conversion-is-not-supported)
    * [🔍 Advanced Usage: Nested Lists](#-advanced-usage-nested-lists)
    * [📜 License](#-license)
    * [👤 Maintainer](#-maintainer)
    * [🤝 Contributing](#-contributing)

---

## ✨ Features

- Declarative transformation mapping
- Supports nested maps and lists
- Minimal footprint and zero dependencies
- Preserves insertion order
- Easy to integrate into any JVM project

---

## 🔧 Installation

Maven Central: [LINK]()

### Gradle

```groovy
dependencies {
    implementation 'com.moftium:anymapper:1.0.0'
}
```

### Maven

```xml
<dependency>
  <groupId>com.moftium</groupId>
  <artifactId>anymapper</artifactId>
  <version>1.0.0</version>
</dependency>
```

---

## 📦 Usage

### Example Input

```java
Map<String, Object> input = Map.of(
    "user", Map.of(
        "name", "Max",
        "info", Map.of(
            "email", "max@example.com",
            "roles", List.of("admin", "editor")
        )
    )
);
```

### Mapping Configuration

```java
Map<String, Object> config = Map.of(
    "user.name", Map.of("destination", "profile.fullName"),
    "user.info.email", Map.of("destination", "profile.contact.email"),
    "user.info.roles", Map.of(
        "destination", "profile.access.roles",
        "type", "list"
    )
);
```

### Transformation

```java
AnyMapper anyMapper = new AnyMapper(config);
Map<String, Object> output = anyMapper.transform(input);
```

### Result

```java
{
  "profile": {
    "fullName": "Max",
    "contact": {
      "email": "max@example.com"
    },
    "access": {
      "roles": ["admin", "editor"]
    }
  }
}
```

---

## Behavior

### If `type=list` is not specified
The value is **treated as-is**, and will be **copied over** even if it's a list or map. This means:
```yaml
type: list
```
… is required to transform each element individually.

### List-to-Map and Map-to-List conversion is **not supported**
Type must be preserved between source and destination.

---

## 🔍 Advanced Usage: Nested Lists

```java
// input data
Map<String, Object> input = Map.of(
        "data", Map.of(
                "items", List.of(
                        Map.of("title", "Temperature", "value", 23),
                        Map.of("title", "Humidity", "value", 60)
                )
        )
);

// mapping configuration
Map<String, Object> config = Map.of(
    "data.items", Map.of(
        "destination", "payload.elements",
        "type", "list",
        "title", Map.of("destination", "header.title"),
        "value", Map.of("destination", "metrics.value")
    )
);

// transformation
AnyMapper anyMapper = new AnyMapper(config);
Map<String, Object> output = anyMapper.transform(input);
```

Result:
```json
{
  "payload": {
    "elements": [
      {
        "header": {
          "title": "Temperature"
        },
        "metrics": {
          "value": 23
        }
      },
      {
        "header": {
          "title": "Humidity"
        },
        "metrics": {
          "value": 60
        }
      }
    ]
  }
}
```
This will transform each element in the `data.items` list independently using the sub-mapping provided.

---

## 📜 License

This project is licensed under the [MIT License](https://opensource.org/licenses/MIT).

---

## 🤝 Contributing

PRs are welcome. Please open issues or suggestions to help improve this utility.