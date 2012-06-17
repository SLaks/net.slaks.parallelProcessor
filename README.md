This project is a take-home interview question.

The goal is to process file conversion requests (in parallel) from an infinite queue, and to do so in a resilient manner.

The conversion process has three failure modes:

 - Sporadic failures which can be recovered from by re-running the converter
 - Poison inputs which will always fail and cannot be converted
 - Temporary filesystem failures which will cause all conversions to fail until the filesystem comes back up.
 
Failures are non-descriptive; the program does not know why each failure happens
 
To handle the first two cases, each file should be attempted three times before giving up.
 
To handle filesystem issues, the program will check whether the filesystem is up (by reading a known marker file) after each failure.  
If the filesystem is down, it will run a remount script (one globally, not per-thread) which is expected to bring the filesystem back up.
If three successive remounts fail, the program is to give up and terminate.
While remounting the filesystem, no other threads should be doing anything.

 
In this implementation, all external interactions (conversions, queue, and filesystem) are mocked in the `external` package.

When analyzing the output log, it can be useful to paste to Excel and sort and/or filter.