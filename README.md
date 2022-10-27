# Synced In-Memory Cache

A fast in-memory cache, synced with a persistent/centralized one. 

Assuming existence of a persistent/centralized DB - this library provides a mirrored, fast, in-memory cache, 
backed and synced by that DB - Enjoying both the fastest data access ***and*** the persistent, distributed, centralized nature of the DB.

This cache is read-only and designed for optimized, small-cache "gets".