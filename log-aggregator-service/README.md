#### intro:
The basic idea is to collect all logs from all microservices to one place. 
This implementation does it through a file system.


- each microservice writes log records to log file limited by size.
- when this file is more than the threshold it is split itself up 
- each microservice has background thread monitoring this process.
- as soon as the log file has split itself up this thread send old log files to this service
- and removes old files (clean up process)
- log aggregator should collect all logs together by timestamp regarding timestamp  
---
#### notes:
- client logic in [LogAggregator](../discovery/src/main/java/ie/home/msa/sandbox/discovery/client/LogAggregator.java)
 - checks logs every 5 sec
 - by default the variable log-service-name is 'none'. In that case logs is not collected
- it uses simple implementation of file storage.
 - it has an index file in root directory containing all microservices by address
 - for coming logs it creates a new file by timestamp
   