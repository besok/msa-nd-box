#### intro:
The basic idea to use conflict-free replicated data types is a flavor of eventual consistency that ensures 
conflicts can be merged automatically to produce a value that is guaranteed to be correct/consistent.

#### active vs passive replication
In active replication (or synchronous), all replicas are contacted inside a transaction to get their values updated. This replication mechanism can be implemented using a peer-to-peer approach, where all replicas have the same responsibilities and can be accessed by any client. Alternatively, a primary replica may have the responsibility of coordinating all other replicas.

Passive replication (or asynchronous) model assumes a replica will propagate the updates to the other replicas outside the context of a transaction. As usual, replicas must receive all the updates.

refs: [whitepaper](https://hal.inria.fr/file/index/docid/555588/filename/techreport.pdf)

--- 

this service implements 2 basic crdts:
 - pn-counter
   - operation based
   - state based
 - lww-register
