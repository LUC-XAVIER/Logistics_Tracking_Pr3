# Full Stack Blueprint: Logistics Tracking System (Enhanced)

This document provides a comprehensive technical mapping of the proposed frontend architecture, data structures, and backend integrations.

---

## 1. Actor: Customer (Sender)

### 📦 Parcel Creation Wizard (Component Breakdown)
| Step | Component | Interaction | Backend / State |
| :--- | :--- | :--- | :--- |
| **1. Origin/Dest** | `AgencySelectorComponent` | Searchable dropdown with map icons. | `GET /agencies` |
| **2. Specs** | `ParcelSpecsFormComponent` | Inputs for weight, fragility, and receiver details. | `ParcelDraftSignal` |
| **3. Quote** | `QuoteSummaryComponent` | Display estimated cost & ETA. Visual breakdown of distance. | `POST /parcels/quote` |
| **4. Review** | `ParcelReviewComponent` | Summary of all previous steps. | Local State |
| **5. Payment** | `PaymentGatewayComponent` | Integration with payment providers. | `POST /payments/{id}/pay` |

**State Management**: 
*   **Service**: `ParcelWizardService`
*   **Storage**: `WritableSignal<ParcelDraft>`
*   **Reset**: State is cleared only upon successful payment or explicit cancelation.

---

## 2. Actor: Delivery Agent (Driver)

### 🛠️ Agent Workflows
*   **Verification Gate**: Agents with `status: PENDING` are redirected to a `VerificationPendingComponent`. They cannot access the **Trip Planner** until verified.
*   **Journey HUD**:
    *   **Map Integration**: Mapbox/Google Maps showing the polyline from `trip.fullPath`.
    *   **Real-time Progress**: A "Pulse" animation on the next upcoming segment.
    *   **Manual Control**: "Mark Segment Reached" button is always enabled for simulation (no GPS proximity lock).

---

## 3. Data Transfer Objects (DTOs)

### [NEW] Analytics & Stats
```typescript
interface AdminStats {
  totalParcels: number;
  activeParcels: number;
  deliveredParcels: number;
  totalRevenue: number; // in XAF
  activeTrips: number;
  agencyUsage: { name: string, count: number }[];
}

interface AgentStats {
  totalTrips: number;
  activeTrips: number;
  completedTrips: number;
  totalDistanceKm: number;
  totalParcelsDelivered: number;
}
```

### [NEW] Parcel Quote
```typescript
interface ParcelQuoteRequest {
  sourceAgencyId: string;
  destAgencyId: string;
  weight: number;
  fragility: number;
}

interface ParcelQuoteResponse {
  estimatedCost: number;
  estimatedDeliveryTime: string; // ISO Date
  distanceKm: number;
}
```

---

## 4. Navigation & Linkage Strategy

### 🔗 Deep Linking & Notifications
Notifications sent via Kafka will now include a `metadata` payload to enable one-click navigation:
*   **Type: PARCEL_UPDATE** -> `router.navigate(['/parcels', parcelId])`
*   **Type: NEW_VERIFICATION** (Admin) -> `router.navigate(['/admin/verify', userId])`
*   **Type: TRIP_START** -> `router.navigate(['/agent/hud', tripId])`

### 🛡️ Guard Logic
```typescript
canActivate(route: ActivatedRouteSnapshot) {
  const user = authService.currentUser();
  if (user.role === 'AGENT' && user.verificationStatus !== 'VERIFIED') {
    return router.parseUrl('/verification-pending');
  }
  return true;
}
```

---

## 5. Error Handling & Edge Cases

| Scenario | UX Feedback | Backend Action |
| :--- | :--- | :--- |
| **Quote Timeout** | Show "Retrying..." toast. | Handled by Feign/Resilience4j. |
| **Driver Rejected** | Display "Verification Denied" with appeal button. | `VerificationStatus` set to `REJECTED`. |
| **Payment Failure** | Return to Wizard Step 5 (Payment) with error message. | Payment event published with `FAILED` status. |
