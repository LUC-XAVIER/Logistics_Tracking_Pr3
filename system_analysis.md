# Logistics Tracking System - Overview and User Stories

## System Overview

The Logistics Tracking System is built using a microservices architecture to manage parcel delivery from end to end. The platform connects customers who want to send parcels with delivery agents who can transport them. It incorporates real-time routing, dynamic pricing, event-driven notifications, and a structured delivery workflow.

### Microservices Architecture
The backend is composed of the following core services communicating via REST APIs and Kafka events:
1. **UserManagementService**: Handles authentication (JWT), authorization, and user profiles for Admins, Customers, and Delivery Agents.
2. **ParcelService**: Manages the creation and lifecycle of parcels, as well as the geographical Agencies. It dynamically calculates delivery costs and Estimated Time of Arrival (ETA) based on distance (using coordinates), weight, and fragility.
3. **PaymentService**: Processes payments for parcels. A successful payment transitions a parcel's status so it can be picked up by a driver.
4. **DeliveryService**: Manages delivery "Trips". It calculates optimal paths using an OSRM routing service, breaking the route into physical checkpoints (Segments). It handles the assignment of parcels to trips and tracks the journey progress.
5. **NotificationService**: An event-driven service listening to Kafka events (e.g., `SegmentReachedEvent`, `TripCompletedEvent`, `ParcelCreatedEvent`) to dispatch real-time updates (Push, SMS, Email) to customers.

---

## Identified Actors
1. **Admin**: Manages the infrastructure (Agencies) and oversees platform users.
2. **Customer**: Senders of parcels who create delivery requests, pay for them, and track their progress.
3. **Delivery Agent (Driver)**: Independent transporters who travel between agencies, select parcels to carry, and report their progress along calculated routes.

---

## Detailed User Stories

### 1. Admin Stories
* **Agency Management**
  * As an Admin, I want to add new Agencies (specifying name, address, and coordinates) so that customers and drivers have valid pick-up and drop-off points.
  * As an Admin, I want to view all registered agencies in the system.
* **User Management**
  * As an Admin, I want to add or verify Delivery Agents so that trusted drivers can access the platform.
  * As an Admin, I want to view all users grouped by their roles (`ADMIN`, `CUSTOMER`, `DRIVER`) to monitor platform adoption.

### 2. Customer Stories
* **Authentication & Profile**
  * As a Customer, I want to register and log in to the platform securely.
  * As a Customer, I want to view and update my profile and contact information.
* **Parcel Creation & Payment**
  * As a Customer, I want to create a new parcel request by selecting a source agency, a destination agency, and specifying the parcel's weight and fragility.
  * As a Customer, I want to see an automatically calculated delivery cost and ETA before confirming my parcel, so I know the price and expected timeframe in advance.
  * As a Customer, I want to securely pay for my parcel (transitioning it from `PENDING_PAYMENT` to `WAITING_FOR_DRIVER`) so that it becomes available for delivery agents.
* **Tracking & Notifications**
  * As a Customer, I want to receive a notification (SMS/Email/Push) when my payment is successful.
  * As a Customer, I want to receive real-time notifications every time the delivery agent reaches a route segment, so I can track my parcel's precise location and remaining distance.
  * As a Customer, I want to receive a notification when my parcel has been successfully delivered at the destination agency.
  * As a Customer, I want to view a list of all my parcels and their current statuses.
  * As a Customer, I want to view my in-app notification inbox, see unread counts, and mark messages as read.

### 3. Delivery Agent (Driver) Stories
* **Authentication**
  * As a Delivery Agent, I want to log in to the platform using my credentials.
* **Trip Planning & Parcel Selection**
  * As a Delivery Agent, I want to declare a Trip by specifying my current (source) agency and my intended destination agency.
  * As a Delivery Agent, I want the system to automatically calculate the best route, total distance, and geographical segments for my trip.
  * As a Delivery Agent, I want to view a list of all available, paid parcels that need to go from my source agency to my destination agency.
  * As a Delivery Agent, I want to select and assign specific parcels to my trip based on my vehicle's carrying capacity.
* **Journey Execution**
  * As a Delivery Agent, I want to officially "start" my trip, changing its status to `ACTIVE`.
  * As a Delivery Agent, I want to mark specific route segments as "Reached" as I travel, which automatically informs the parcel owners of my progress.
  * As a Delivery Agent, I want my trip to automatically mark itself as `COMPLETED`, and all assigned parcels as `DELIVERED`, once I mark the final route segment as reached.

---

## Core System Workflows

### Parcel Lifecycle
1. **Creation**: Customer creates Parcel (`PENDING_PAYMENT`). Cost & ETA calculated.
2. **Payment**: Customer pays via PaymentService. Parcel status updates to `WAITING_FOR_DRIVER`.
3. **Assignment**: Driver creates a Trip, finds the parcel, and assigns it (`ASSIGNED`).
4. **Transit**: Driver starts the trip. Status updates dynamically as the driver reaches segments.
5. **Delivery**: Driver reaches the final segment. Trip completes, Parcel status becomes `DELIVERED`.

### Dynamic Route & Segment System
When a Driver creates a trip, the `DeliveryService` queries an OSRM routing engine. The continuous path is broken down into discrete "Segments" (checkpoints). As the driver moves, they hit an endpoint for each segment. This triggers a Kafka `SegmentReachedEvent` containing the distance traveled and remaining. The `NotificationService` intercepts this and updates the customers instantly.
