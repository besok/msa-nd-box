#### intro:
The ZAB protocol ensures that the Zookeeper replication is done in order and is also responsible for the election of master/leader nodes and the restoration of any failed nodes. In a Zookeeper ecosystem, the leader node is the heart of everything; every cluster has one leader node and the rest of the nodes are followers. All incoming client requests and state changes are received at first by the leader with responsibility to replicate it across all its followers (and itself). All incoming read requests are also load balanced by the leader within itself and its followers.

- refs: [whitepaper](http://www.tcs.hut.fi/Studies/T-79.5001/reports/2012-deSouzaMedeiros.pdf)

#### notes:
A tiny implementation of ZAB protocol based on HTTP async calls.
