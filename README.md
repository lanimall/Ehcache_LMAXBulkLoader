Ehcache_LMAXBulkLoader
======================

A sample application demonstrating how to fast bulkload random generated objects in ehcache/terracotta using the LMAX disruptor multi-threaded framework (more http://lmax-exchange.github.io/disruptor/)

Quick start
---------------------------------------------
There are 2 maven profiles created: "small" (default, with smaller JVM memory footprint) and "large" (to test with large dataset and memory environments). Feel free to tweak the JVM parameters appropriately for your test environment.
Each of the build profile generates 2 executable scripts per platform:
 - 1 script for standlone ehcache - BigMemory Go (ehcache-standalone.xml)
 - 1 script for distributed ehcache - BigMemoryMax  (ehcache-distributed.xml)

Finally, I'm using by default ehcache-ee version 2.7.2 (and relted terracotta-toolkit-runtime-ee version 4.0.2 for distributed scenario)...feel free to adjust as needed.

Steps:
 - Build, Package, and Generate the run scripts. 2 profiles created: "small" (default, with smaller JVM memory footprint) and "large" (to test with large dataset and memory environments):
 -- mvn clean package appassembler:assemble (use the default "small" profile)
 -- mvn clean package appassembler:assemble -P large
 - Run based on platform:
 -- Linux/OSX/UNIX: sh target/appassembler/bin/LaunchLoader-Standalone <object count> (for standlone ehcache - BigMemory Go) or sh target/appassembler/bin/LaunchLoader-Distributed <object count> (for distributed ehcache - BigMemoryMax)
 -- Windows: target/appassembler/bin/LaunchLoader-Standalone.bat <object count> (for standlone ehcache - BigMemory Go) or sh target/appassembler/bin/LaunchLoader-Distributed.bat <object count> (for distributed ehcache - BigMemoryMax)
