# 🌦️ Weather-Aware Delivery — Feature Documentation

> **Branch:** `feature/weather-aware-delivery`  
> **Status:** Research & Proof of Concept  
> **API Used:** [Open-Meteo](https://open-meteo.com/) (free, no API key required)  
> **Approach:** Per-segment forecast at estimated arrival time (Option 4)

---

## Overview

This document explains how real-time weather forecasting will be integrated into the Parcel/Logistics Tracking System to produce more accurate delivery cost estimates and ETAs. Rather than taking a single weather snapshot at the origin, the system fetches **forecasted weather for each route segment at the time the parcel is estimated to arrive there** — meaning a storm predicted to hit a highway in 4 hours is accounted for if the parcel is expected to pass through in 4 hours.

A live demo (`index.html`) accompanies this document.

---

## Why Weather Matters for Delivery

Weather directly affects core aspects of the system already defined in the project:

| System Aspect | How Weather Affects It |
|---|---|
| **Delivery speed** | Rain, wind, and storms slow vehicles down per segment |
| **Parcel cost** | Adverse conditions increase handling risk, proportional to affected distance |
| **ETA calculation** | Each segment gets its own adjusted speed → sum gives total ETA |
| **Fragile parcel handling** | Bad weather + high fragility = extra care = extra cost |

---

## The API — Open-Meteo

Open-Meteo is a free, open-source weather API requiring no authentication. It operates under a **fair use policy** — no hard request limits, but the API should not be hammered with hundreds of calls per second. For a logistics system fetching weather per route segment at booking time, usage is well within acceptable bounds.

### Endpoints Used

**1. Geocoding** — converts city names to coordinates
```
GET https://geocoding-api.open-meteo.com/v1/search?name={city}&count=1
```

**2. Hourly Forecast at a coordinate** — fetches predicted conditions at a future time
```
GET https://api.open-meteo.com/v1/forecast
  ?latitude={lat}
  &longitude={lon}
  &hourly=temperature_2m,precipitation,wind_speed_10m,weather_code
  &wind_speed_unit=kmh
  &forecast_days=3
```

The hourly forecast returns weather for every hour over the next 3 days. Given a segment's estimated arrival time, the system looks up the closest hourly entry to get the predicted conditions at that point in time and space.

### Key Fields Used

| Field | Used For |
|---|---|
| `temperature_2m` | Detect freezing / extreme heat conditions |
| `wind_speed_10m` | Wind penalty on speed and cost |
| `precipitation` | Rain surcharge |
| `weather_code` | Detect storms, fog, snow (WMO standard codes) |

---

## Routes, Coordinates, and Segments — Explained

The Map API (Google Directions / OpenRouteService) returns a route as a **polyline** — a list of coordinate points tracing the actual road. A segment is the stretch of road between two consecutive points.

### Example: Yaoundé → Bafia

```
Point 0:  { lat: 3.8480,  lng: 11.5021 }  ← Yaoundé (departure)
Point 1:  { lat: 3.9200,  lng: 11.3800 }
Point 2:  { lat: 4.1000,  lng: 11.2500 }
Point 3:  { lat: 4.2700,  lng: 11.1600 }
Point 4:  { lat: 4.4600,  lng: 10.9700 }
Point 5:  { lat: 4.6500,  lng: 10.7300 }  ← Bafia (arrival)
```

Each pair of consecutive points forms a segment with its own road type and distance:

```
Segment 1: Point 0 → Point 1  | road: town     | distance: 12km  | base speed:  60 km/h
Segment 2: Point 1 → Point 2  | road: highway  | distance: 22km  | base speed:  90 km/h
Segment 3: Point 2 → Point 3  | road: highway  | distance: 19km  | base speed:  90 km/h
Segment 4: Point 3 → Point 4  | road: motorway | distance: 28km  | base speed: 100 km/h
Segment 5: Point 4 → Point 5  | road: town     | distance:  8km  | base speed:  60 km/h
```

This structure is already stored in the `route_data` JSON field of the `RouteCache` table. Weather integration attaches a forecast-based penalty to each segment individually.

---

## Formulas — Full Explanation

### 1. Distance Factor

```
distance_factor = 1 + (distance_km / 1000)
```

A scaling multiplier that grows the base price with distance. Dividing by 1000 keeps the multiplier proportional — a 250km route gives `1.25` (25% increase), a 1000km route gives `2.0` (doubles the price). It captures fuel, driver time, and vehicle wear proportionally to how far the parcel travels.

---

### 2. Base Cost (no weather)

```
base_calc = base_price × distance_factor + (fragility_level × 500)
```

Two independent components. `base_price × distance_factor` is a variable cost that scales with distance. `fragility_level × 500` is a flat penalty per fragility point — a fixed handling cost that is distance-independent, because careful handling costs the same whether the trip is 10km or 500km.

---

### 3. Base Speed per Road Type

```
Town     → 60 km/h
Highway  → 90 km/h
Motorway → 100 km/h
```

Realistic average driving speeds per road type. Already part of the project specification — not derived mathematically.

---

### 4. Fragility Speed Penalty

```
fragility_factor = 1 − fragility_level / 15
adjusted_speed   = base_speed × fragility_factor
```

Fragility reduces speed as a proportion of base speed. The divisor is **15, not 10** — a deliberate design choice. If it were 10, a fragility-10 parcel would reduce speed to exactly zero, which makes no physical sense. With 15:

```
fragility 1  → factor = 0.933 → speed reduced by  6.7%
fragility 5  → factor = 0.667 → speed reduced by 33.3%
fragility 10 → factor = 0.333 → speed reduced by 66.7%  (still moving)
```

A fragility-10 parcel on a highway still travels at `90 × 0.333 ≈ 30 km/h` — slow but physically reasonable.

---

### 5. Weather Speed Penalty

```
adjusted_speed = base_speed × (1 − fragility_level / 15) × (1 − weather_penalty)
```

The weather penalty is **multiplied** onto the fragility factor, not added to it. The two effects are independent — fragility slows the vehicle due to handling care, weather slows it due to road conditions. Multiplying keeps both proportional:

```
base_speed       = 90 km/h
fragility = 6    → fragility_factor = 1 − 6/15 = 0.60
heavy rain       → weather_factor   = 1 − 0.25 = 0.75

adjusted_speed   = 90 × 0.60 × 0.75 = 40.5 km/h
```

If the penalties were added instead, severe combined conditions could push speed below zero. Multiplying prevents that — each factor pushes speed toward zero proportionally but can never cross it.

---

### 6. Segment ETA

```
segment_eta_hours = segment_distance_km / adjusted_speed_kmh
```

The fundamental physics formula `time = distance / speed`, applied per segment. The ETA of a segment is the time taken to travel through it — from its start point to its end point.

```
Segment 2: 22km / 40.5 km/h = 0.543h ≈ 32 minutes
```

---

### 7. Cumulative Arrival Time at a Segment

```
arrival_time_at_segment_N = departure_time + Σ eta(segment_0 .. segment_N-1)
```

Sum all segment ETAs **before** segment N to know when the parcel is expected to reach the start of that segment. This timestamp is what gets passed to the Open-Meteo hourly forecast to retrieve predicted weather at that future moment.

```
departure    = 08:00
seg1 eta     = 12 min  → parcel at Point 1 at 08:12
seg2 eta     = 32 min  → parcel at Point 2 at 08:44
seg3 eta     = 28 min  → parcel at Point 3 at 09:12
seg4 eta     = 41 min  → parcel at Point 4 at 09:53
seg5 eta     = 11 min  → parcel arrives Bafia at 10:04
```

---

### 8. Weather Surcharge — Weighted by Segment Distance

```
segment_weight       = segment_distance / total_route_distance
segment_contribution = weather_surcharge_for_segment × segment_weight
total_surcharge      = Σ segment_contribution  (all segments)

weather_add  = base_calc × total_surcharge
final_price  = base_calc + weather_add
```

The surcharge is **weighted by each segment's share of total distance**. A 5km town segment in heavy rain should not penalize the price as much as a 100km highway segment under the same rain. Weighting makes the surcharge proportional to how much of the journey was actually affected.

```
Total route = 89km

Segment 1: 12km, no rain    → (12/89) × 0.00 = 0.000
Segment 2: 22km, rain  20%  → (22/89) × 0.20 = 0.049
Segment 3: 19km, storm 40%  → (19/89) × 0.40 = 0.085
Segment 4: 28km, no rain    → (28/89) × 0.00 = 0.000
Segment 5:  8km, rain  20%  → ( 8/89) × 0.20 = 0.018

total_surcharge = 0.152  →  15.2% added to base cost
```

---

## Weather Condition Penalties

Penalties within a segment are additive, then capped at the segment level.

| Condition | Threshold | Speed Penalty | Price Surcharge |
|---|---|---|---|
| Strong wind | > 80 km/h | −30% | +25% |
| Moderate wind | > 50 km/h | −15% | +12% |
| Heavy rain | > 10 mm | −25% | +20% |
| Light rain | > 2 mm | −10% | +8% |
| Freezing temp | < 0°C | −20% | +15% |
| Extreme heat | > 40°C | −10% | +10% |
| Severe storm (code ≥ 80) | — | −20% | +20% |
| Fragile + bad weather | fragility ≥ 7 and penalty > 20% | — | +3% × fragility |

Caps per segment: **70% max speed reduction**, **80% max surcharge**.

---

## Full Per-Segment Pipeline (Option 4)

```
For each segment i in route:

  1. segment_distance    = distance between point_i and point_i+1 (haversine)
  2. base_speed          = speed based on road_type (town/highway/motorway)
  3. fragility_factor    = 1 − fragility_level / 15
  4. arrival_time        = departure_time + Σ eta of all previous segments
  5. forecast            = open_meteo_hourly(segment_midpoint_coords, arrival_time)
  6. weather_penalty,
     weather_surcharge   = evaluate(forecast.wind, forecast.rain, forecast.temp, forecast.code)
  7. adjusted_speed      = base_speed × fragility_factor × (1 − weather_penalty)
  8. segment_eta         = segment_distance / adjusted_speed
  9. segment_weight      = segment_distance / total_route_distance
  10. weighted_surcharge += weather_surcharge × segment_weight

After all segments:
  total_eta    = Σ all segment_eta
  base_calc    = base_price × distance_factor + (fragility_level × 500)
  weather_add  = base_calc × weighted_surcharge
  final_price  = base_calc + weather_add
```

Note that step 4 depends on previous segment ETAs, which themselves depend on weather fetched in step 5. The pipeline therefore runs **sequentially segment by segment**, not in parallel — each segment's arrival time is only known after all previous segments are processed.

---

## Integration into the Main System

### Where It Plugs In — Delivery Service

```
User submits parcel
       ↓
Geocode origin + destination              ← already exists
       ↓
Fetch route segments from Map API         ← already exists
       ↓
For each segment (sequential):
  → compute cumulative arrival time       ← NEW
  → fetch hourly forecast at coords       ← NEW (Open-Meteo)
  → calculate penalty and adjusted speed  ← extends existing formula
  → compute segment ETA                   ← extends existing formula
       ↓
Sum all segment ETAs → total ETA          ← extended
Compute weighted surcharge → final price  ← extended
       ↓
Store per-segment weather snapshots       ← NEW field in Parcel table
```

### Database Change — Parcel Table

```sql
ALTER TABLE parcel ADD COLUMN weather_snapshot JSON;
```

Stores the per-segment forecast conditions captured at booking time:

```json
{
  "segments": [
    {
      "segment_index": 0,
      "coords": { "lat": 3.848, "lng": 11.502 },
      "estimated_arrival": "2026-04-04T08:12:00",
      "temperature": 24.5,
      "wind_speed": 18.0,
      "precipitation": 0.0,
      "weather_code": 1,
      "speed_penalty": 0.00,
      "surcharge": 0.00
    },
    {
      "segment_index": 2,
      "coords": { "lat": 4.100, "lng": 11.250 },
      "estimated_arrival": "2026-04-04T09:12:00",
      "temperature": 21.0,
      "wind_speed": 62.0,
      "precipitation": 14.5,
      "weather_code": 65,
      "speed_penalty": 0.40,
      "surcharge": 0.32
    }
  ],
  "total_weighted_surcharge": 0.152,
  "total_eta_minutes": 124
}
```

This preserves the exact forecast conditions used to compute the final price — important for transparency, auditing, and customer dispute resolution.

### Kafka Event Update

```json
// parcel.created event payload (extended)
{
  "parcelId": "uuid",
  "departureTime": "2026-04-04T08:00:00",
  "estimatedArrival": "2026-04-04T10:04:00",
  "totalEtaMinutes": 124,
  "worstWeatherSegment": "Segment 3 — Heavy rain + strong wind",
  "weightedWeatherSurcharge": 0.152,
  "finalPrice": 11420
}
```

---

## Caching Strategy

| Data | Cache Duration | Reason |
|---|---|---|
| City coordinates (geocoding) | Permanent | Coordinates never change |
| Route segments | Permanent (per agency pair) | Road geometry doesn't change |
| Hourly forecast per coordinate | 30 minutes | Forecast updates hourly at source |

Forecast data is **not cached per parcel** — each booking fetches fresh predictions since two parcels departing hours apart face different conditions. Route geometry is still fully cached as per the existing strategy.

---

## Demo

The accompanying `index.html` demonstrates the weather impact calculation with live Open-Meteo data. It currently uses a single-point model (origin city) for simplicity of demonstration. The per-segment pipeline described in this document is the production implementation target.

### Running the Demo

Open `index.html` in any browser — no server or API key needed.

---
```
Note that this demo just shows how the weather service is called and how it affects the price and adjusted_speed.
```
## References

- [Open-Meteo Forecast API Docs](https://open-meteo.com/en/docs)
- [WMO Weather Interpretation Codes](https://open-meteo.com/en/docs#weathervariables)
- [Open-Meteo Geocoding API](https://open-meteo.com/en/docs/geocoding-api)
- Project base documentation: `logistic.pdf`
