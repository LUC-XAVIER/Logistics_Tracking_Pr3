package com.example.logistics_tracking.enums;

/**
 * Parcel Status Enum
 *
 * Purpose: Defines all possible states a parcel can be in
 *
 * Lifecycle:
 * PENDING_PAYMENT → User hasn't paid yet
 *       ↓
 * IN_TRANSIT → Payment complete, delivery in progress
 *       ↓
 * DELIVERED → Parcel arrived at destination
 *
 * Additional states:
 * CANCELLED → User cancelled before delivery
 * RETURNED → Delivery failed, returning to sender
 * LOST → Parcel lost during transit (rare)
 *
 * Why enum instead of strings:
 * - Type safety (compiler catches typos)
 * - IDE autocomplete support
 * - Can't accidentally use invalid status
 * - Easy to add methods (e.g., isDelivered())
 */
public enum ParcelStatus {

    /**
     * Parcel created, awaiting payment
     * User sees: "Payment Required"
     */
    PENDING_PAYMENT,

    /**
     * Payment complete, delivery in progress
     * User sees: "Your parcel is on the way"
     * GPS tracking active
     */
    IN_TRANSIT,

    /**
     * Parcel successfully delivered
     * User sees: "Delivered"
     * Final state (success)
     */
    DELIVERED,

    /**
     * User or system cancelled parcel
     * User sees: "Cancelled"
     * Payment refunded if applicable
     */
    CANCELLED,

    /**
     * Delivery failed, returning to sender
     * User sees: "Returning to sender"
     * Reasons: wrong address, recipient refused, etc.
     */
    RETURNED,

    /**
     * Parcel lost during transit
     * User sees: "Lost - investigation in progress"
     * Triggers insurance claim process
     */
    LOST;

    /**
     * Helper method - check if parcel is in final state
     *
     * @return true if parcel lifecycle is complete
     */
    public boolean isFinalState() {
        return this == DELIVERED || this == CANCELLED || this == LOST;
    }

    /**
     * Helper method - check if parcel can be cancelled
     *
     * @return true if cancellation is allowed
     */
    public boolean canBeCancelled() {
        return this == PENDING_PAYMENT || this == IN_TRANSIT;
    }

    /**
     * Helper method - check if parcel is actively being delivered
     *
     * @return true if delivery is in progress
     */
    public boolean isActiveDelivery() {
        return this == IN_TRANSIT;
    }
}