package com.snowflake;

import java.math.BigInteger;
import java.time.Instant;

public final class Snowflake implements Comparable<Snowflake> {
    /** The UNIX time that represents epoch (Thu Nov 15, 2001)К. */
    public static final long EPOCH = 1005771600000L;
    /**
     * Constructs a {@code Snowflake} utilizing an <i>unsigned</i> ID.
     *
     * @param id The <i>unsigned</i> ID to construct a {@code Snowflake}.
     * @return A constructed {@code Snowflake} with the <i>unsigned</i> ID.
     */
    public static Snowflake of(final long id) {
        return new Snowflake(id);
    }

    /**
     * Constructs a {@code Snowflake} utilizing an <i>unsigned</i> ID.
     *
     * @param id The <i>unsigned</i> ID to construct a {@code Snowflake}. Must be non-null.
     * @return A constructed {@code Snowflake} with the <i>unsigned</i> ID.
     * @throws NumberFormatException If {@code id} is not an <i>unsigned</i> ID.
     */
    public static Snowflake of(final String id) {
        return new Snowflake(Long.parseUnsignedLong(id));
    }

    /**
     * Constructs a {@code Snowflake} utilizing a timestamp. The constructed {@code Snowflake} is only guaranteed to
     * contain accurate information about its {@link #getTimestamp() timestamp}; the other portions are undefined.
     *
     * @param timestamp The timestamp to construct a {@code Snowflake}. Must be non-null.
     * @return A constructed {@code Snowflake} with the timestamp.
     */
    public static Snowflake of(final Instant timestamp) {
        return of((timestamp.toEpochMilli() - EPOCH) << 22);
    }

    /**
     * Constructs a {@code Snowflake} utilizing a BigInteger representing an <i>unsigned</i> ID.
     *
     * @param id The BigInteger representing an <i>unsigned</i> ID to construct a {@code Snowflake}. Must be non-null.
     * @return A constructed {@code Snowflake} with an <i>unsigned</i> ID.
     */
    public static Snowflake of(final BigInteger id) {
        return of(id.longValue());
    }

    /**
     * Constructs a {@code Snowflake} represented as a {@code long} utilizing an <i>unsigned</i> ID.
     *
     * @param id The <i>unsigned</i> ID to construct a {@code Snowflake}. Must be non-null.
     * @return A constructed {@code Snowflake} with the <i>unsigned</i> ID.
     * @throws NumberFormatException If {@code id} is not an <i>unsigned</i> ID.
     */
    public static long asLong(final String id) {
        return Long.parseUnsignedLong(id);
    }

    /**
     * Constructs a {@code Snowflake} represented as a {@link String} utilizing an <i>unsigned</i> ID.
     *
     * @param id The <i>unsigned</i> ID to construct a {@code Snowflake}. Must be non-null.
     * @return A constructed {@code Snowflake} with the <i>unsigned</i> ID.
     * @throws NumberFormatException If {@code id} is not an <i>unsigned</i> ID.
     */
    public static String asString(final long id) {
        return Long.toUnsignedString(id);
    }

    /** The <i>unsigned</i> ID. */
    private final long id;
    /**
     * The machine or process ID, its value can't be modified after initialization.
     */
    private final long workerId;
    /**
     * The process ID running on, its value can't be modified after initialization.
     */
    private final long processId;
    /**
     * For every ID that is generated on that process, this number is incremented
     */
    private final long increment;

    public Snowflake() {
        this((System.currentTimeMillis() - Snowflake.EPOCH) << 22);
    }

    /**
     * Constructs a {@code Snowflake} utilizing an <i>unsigned</i> ID.
     *
     * @param id The <i>unsigned</i> ID to construct a {@code Snowflake}.
     */
    private Snowflake(final long id) {
        this.id = id;
        this.workerId = (id & 0x3E0000) >> 17;
        this.processId = (id & 0x1F000) >> 12;
        this.increment = id & 0xFFF;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getProcessId() {
        return processId;
    }

    /**
     * Gets the <i>unsigned</i> ID of this {@code Snowflake} as a primitive long.
     *
     * @return The <i>unsigned</i> ID of this {@code Snowflake} as a primitive long.
     */
    public long asLong() {
        return id;
    }

    /**
     * Gets the <i>unsigned</i> ID of this {@code Snowflake} as an object String.
     *
     * @return The <i>unsigned</i> ID of this {@code Snowflake} as an object String.
     */
    public String asString() {
        return Long.toUnsignedString(id);
    }

    /**
     * Gets the timestamp of this {@code Snowflake}.
     *
     * @return The timestamp of this {@code Snowflake}.R
     */
    public Instant getTimestamp() {
        return Instant.ofEpochMilli(EPOCH + (id >>> 22));
    }

    /**
     * Gets the <i>unsigned</i> ID of this {@code Snowflake} as a BigInteger.
     *
     * @return The <i>unsigned</i> ID of this {@code Snowflake} as a BigInteger.
     */
    public BigInteger asBigInteger() {
        return new BigInteger(asString());
    }

    /**
     * Compares this snowflake to the specified snowflake.
     * <p>
     * The comparison is based on the timestamp portion of the snowflakes.
     *
     * @param other The other snowflake to compare to.
     * @return The comparator value.
     */
    @Override
    public int compareTo(Snowflake other) {
        return Long.signum((id >>> 22) - (other.id >>> 22));
    }

    /**
     * Indicates whether some other object is "equal to" this {@code Snowflake}.
     * The other object is considered equal if:
     * <ul>
     * <li>It is also a {@code Snowflake} and;</li>
     * <li>Both instances have equal {@link #asLong() IDs}.</li>
     * </ul>
     *
     * @param obj An object to be tested for equality.
     * @return {@code true} if the other object is "equal to" this one, false otherwise.
     */
    @Override
    public boolean equals(final Object obj) {
        return (obj instanceof Snowflake) && (((Snowflake) obj).id == id);
    }

    /**
     * Gets the hash code value of the {@link #asLong() ID}.
     *
     * @return The hash code value of the {@link #asLong() ID}.
     */
    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    /**
     * Gets the String representation of this {@code Snowflake}.
     * <p>
     * The format returned by this method is unspecified and may vary between implementations; however, it is guaranteed
     * to always be non-empty. This method is not suitable for obtaining the ID; use {@link #asString()} instead.
     *
     * @return The String representation of this {@code Snowflake}.
     * @see #asString()
     */
    @Override
    public String toString() {
        return "Snowflake{" + asString() + "}";
    }
}
