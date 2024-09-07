# Snowflakes
These IDs are guaranteed to be unique across, except in some unique scenarios in which child objects share their parent's ID. Because Snowflake IDs are up to 64 bits in size (e.g. a uint64), they are always returned as strings in the HTTP API to prevent integer overflows in some languages.

#### Snowflake ID Broken Down in Binary

```
111111111111111111111111111111111111111111 11111 11111 111111111111
64                                         22    17    12          0
```

#### Snowflake ID Format Structure (Left to Right)

| Field               | 	Bits     | 	Number of bits | 	Description                                                                  | 	Retrieval                         |
|---------------------|-----------|-----------------|-------------------------------------------------------------------------------|------------------------------------|
| Timestamp           | 	63 to 22 | 	42 bits        | 	Milliseconds since Epoch, the first second of 2015 or 1420070400000.         | 	(snowflake >> 22) + 1420070400000 |
| Internal worker ID  | 	21 to 17 | 5 bits          |                                                                               | (snowflake & 0x3E0000) >> 17       |
| Internal process ID | 16 to 12  | 	5 bits		       |                                                                               | (snowflake & 0x1F000) >> 12        |
| Increment           | 	11 to 0  | 	12 bits        | 	For every ID that is generated on that process, this number is incremented   | 	snowflake & 0xFFF                 |


#### Convert Snowflake to DateTime

#### Snowflake IDs in Pagination

We typically use snowflake IDs in many of our API routes for pagination. The standardized pagination paradigm we utilize is one in which you can specify IDs **before** and **after** in combination with **limit** to retrieve a desired page of results. You will want to refer to the specific endpoint documentation for details.

It is useful to note that snowflake IDs are just numbers with a timestamp, so when dealing with pagination where you want results from the beginning of time (in Epoch, but **0** works here too) or before/after a specific time you can generate a snowflake ID for that time.

#### Generating a snowflake ID from a Timestamp Example

```
(timestamp_ms - EPOCH) << 22
```