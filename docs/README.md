# Alzara User Guide

// Product screenshot goes here

// Product intro goes here

## Adding deadlines

// Describe the action and its outcome.

// Give examples of usage

Example: `keyword (optional arguments)`

// A description of the expected outcome goes here

```
expected output
```

## Viewing your schedule

Shows every deadline due, and every event happening, on a given date - deadlines match that exact date, events match any date within their start-to-end range (inclusive of both ends).

Example: `view yyyy-mm-dd`

Matches are numbered from 1 and sorted chronologically by their own date (a deadline by its due date, an event by its start date) - not by the order they were added.

```
view 2019-10-15
```

```
I see all on Oct 15 2019:
1.[E][ ] trip (from: Oct 14 2019 to: Oct 16 2019)
2.[D][ ] return book (by: Oct 15 2019)
```

If nothing is scheduled on the given date, Alzara says so instead of showing an empty list:

```
I see nothing on Oct 20 2019.
```


## Feature XYZ

// Feature details
