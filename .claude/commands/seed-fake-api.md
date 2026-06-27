---
allowed-tools: WebFetch, Write(spring-ai-mcp/src/main/resources/*), Bash(mkdir -p spring-ai-mcp/src/main/resources/*), Bash(find spring-ai-mcp/src/main/resources/*)
---

# seed-fake-api

Fetch live data from the Open Meteo APIs for every city in the canonical list and write the responses as
resource files.

Use relative paths (e.g. `spring-ai-mcp/src/main/resources/...`) for all `mkdir` and `find` commands —
the working directory is always the project root.

## Cities

New York, San Francisco, Sydney, Warsaw.

## Steps

### 1 — Fetch geocoding fixtures

Read the `API_URL` constant from `OpenMeteoGeocodingClient`. For each city, call that URL with `name={city}&count=1`.
Extract **only** the first element of the `results` array (it is already a `Geocoding`-shaped object).
Write it to:

```
spring-ai-mcp/src/main/resources/io/github/kmikuta/mcp/adapters/geocoding/{slug}.json
```

These files are also the source of `latitude` and `longitude` used by all subsequent steps.

### 2 — Fetch weather fixtures

Read `latitude` and `longitude` from the geocoding fixtures written in Step 1.
Read the `API_URL` constant from `OpenMeteoWeatherClient`. For each city, call that URL substituting the coordinates.
Extract **only** the `current` object from the response (e.g. `{"temperature_2m": 22.5}`).
Write it to:

```
spring-ai-mcp/src/main/resources/io/github/kmikuta/mcp/adapters/weather/{slug}.json
```

### 3 — Fetch air quality fixtures

Use the same coordinates from Step 1.
Read the `API_URL` constant from `OpenMeteoAirQualityClient`. For each city, call that URL substituting the coordinates.
Extract **only** the `current` object (e.g. `{"european_aqi": 45}`).
Write it to:

```
spring-ai-mcp/src/main/resources/io/github/kmikuta/mcp/adapters/air_quality/{slug}.json
```

