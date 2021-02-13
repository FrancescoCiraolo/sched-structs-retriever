/**
 * Provides the classes necessary for use trace script for retrieving structured data.
 *
 * The main component is {@link com.github.francescociraolo.bcctrace.Trace},
 * with its method {@link com.github.francescociraolo.bcctrace.Trace#startTracing(
 * com.github.francescociraolo.bcctrace.TraceStreamHandler, java.util.Collection,
 * java.lang.String, com.github.francescociraolo.bcctrace.Request[])} which wraps the trace call.
 *
 * {@link com.github.francescociraolo.bcctrace.Request} and {@link com.github.francescociraolo.bcctrace.RequestHeader}
 * are used to manage the requests and the resulting values in a way more suitable for {@link java.util.stream.Stream}.
 * The obtained values are casted in the expected type automatically.
 *
 * Several {@link com.github.francescociraolo.bcctrace.ValueType}s are available.
 */
package com.github.francescociraolo.bcctrace;