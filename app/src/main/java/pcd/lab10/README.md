# Lab10

## Distributed Actors in Akka

### Artery

### Clustering

Note: see the example project for build configuration.

- Akka cluster: a group of Actor Systems (that live in independent JVM) communicating P2P to form a whole
  - location transparency: no matter an actor is in the same node or not, its location is transparent 
  - no single point of failure
  - fault-tolerant
  - can dynamically increase and decrease the number of nodes

![akka cluster](../../../../../../res/lab10/akka-cluster.png)

Goal of the cluster module: provide fully automated features for actor distribution and failover.

Basic concepts:

- **node**:  logical member of cluster, identified by `hostname:port:uid` (there could be multiple nodes on the same physical machine)
- **cluster**: set of nodes joined together through _Cluster Membership Service_
- **leader**: cluster node that manages cluster convergence and membership state transitions

Basic configuration:

- **provider**: local/cluster. The first is used for apps that do not require distributions; the second to allow actors to be created in any node.
- **remote**: internal foundational mechanism
- **cluster.seed-nodes**: list of hosts ports and actor system names of the start nodes (`localhost` may not be equal to `127.0.0.1` depending on your setup).

```conf
akka {
  actor {
    provider = "cluster"
    serialization-bindings {
      "it.unibo.pcd.akka.Message" = jackson-cbor
    }
  }
  remote.artery {
    canonical {
      hostname = "127.0.0.1"
      port = 2551
    }
  }

  cluster {
    seed-nodes = [
      "akka://ClusterSystem@127.0.0.1:2551",
      "akka://ClusterSystem@127.0.0.1:2552"]
  }
}
```

<ins>**Cluster membership**</ins>

First option: joining a cluster programmatically knowing the address of a member, sending a `Join` message

```scala
cluster.manager ! Join(cluster.selfMember.address)
```

Second option: a few "founders" are required to start the process. Akka provides **seed nodes** for this purpose, which are starting point of the cluster and serve as the **first point of contact for other nodes**. Other nodes can join the cluster through one of the seed nodes once the first seed node is started and up.

![akka cluster seeds nodes](../../../../../../res/lab10/akka-cluster-seeds.jpg)

- _Configuration_: passing a config of initial node addresses. Used for local development or testing. Not recommended to use in production (we will use it in the following).
- _Cluster bootstrap_: programmed to find the nodes in cloud environments such as Kubernetes, AWS, Azure, ... . This is the recommended approach.
- _Programmatically_: using an implementation written ad-hoc.

See `basics.Joining` source:
```scala
@main def withSeedAndLeave(): Unit =
  val system1 = startup(port = 2551)(Behaviors.empty)
  val system2 = startup(port = 2552)(Behaviors.empty)
  Thread.sleep(10_000)
  Cluster(system1).manager ! Leave(Cluster(system1).selfMember.address)
```

![akka cluster lifecycle](../../../../../../res/lab10/akka-cluster-lifecycle.png)

Akka-Remote has started with TCP protocol and this canonical address
```
Remoting started with transport [Artery tcp]; listening on address [akka://ClusterSystem@127.0.0.1:2551] with UID [-3082908305885740943]

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Starting up
```

Node started successfully in cluster mode
```
Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Started up successfully
```

Not automatic downing available for unreachable nodes (see later).
```
No downing-provider-class configured, manual cluster downing required
```

```
Remoting started with transport [Artery tcp]; listening on address [akka://ClusterSystem@127.0.0.1:2552] with UID [-4963127196782143602]

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Starting up

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Started up successfully
```

Actor `ClusterSystem@127.0.0.1:2552` receives an "InitJoin" message from actor `ClusterSystem@127.0.0.1:2551`, which is attempting to join the cluster. However, the receiving node has not completed its initialization process yet, and therefore cannot accept new members into the cluster. The `InitJoinNack` message is used to reject the join request. 
```
Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Received InitJoin message from [Actor[akka://ClusterSystem@127.0.0.1:2551/system/cluster/core/daemon/firstSeedNodeProcess-1#209040171]], but this node is not initialized yet

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Received InitJoin message from [Actor[akka://ClusterSystem@127.0.0.1:2552/system/cluster/core/daemon/joinSeedNodeProcess-1#1123547397]], but this node is not initialized yet

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Received InitJoinNack message from [Actor[akka://ClusterSystem@127.0.0.1:2552/system/cluster/core/daemon#386892605]] to [akka://ClusterSystem@127.0.0.1:2551]
```

Seed automatically join itself.
```
Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Node [akka://ClusterSystem@127.0.0.1:2551] is JOINING itself (with roles [dc-default], version [0.0.0]) and forming new cluster
[2023-06-12 09:34:54,530]
```

After the output confirms that the leader node has switched to `Up` and is ready to accept other nodes. The role of the leader node is to manage the convergence of the cluster and the membership transitions. Only one node can be the leader at any time and any node of the cluster can become the leader.
```
Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - is the new leader among reachable nodes (more leaders may exist)

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Leader is moving node [akka://ClusterSystem@127.0.0.1:2551] to [Up]
```

Node `ClusterSystem@127.0.0.1:2552` join the cluster and gets an ack from the leader.
```
Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Received InitJoin message from [Actor[akka://ClusterSystem@127.0.0.1:2552/system/cluster/core/daemon/joinSeedNodeProcess-1#1123547397]] to [akka://ClusterSystem@127.0.0.1:2551]

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Sending InitJoinAck message from node [akka://ClusterSystem@127.0.0.1:2551] to [Actor[akka://ClusterSystem@127.0.0.1:2552/system/cluster/core/daemon/joinSeedNodeProcess-1#1123547397]] (version [2.6.19])

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Received InitJoinAck message from [Actor[akka://ClusterSystem@127.0.0.1:2551/system/cluster/core/daemon#442992817]] to [akka://ClusterSystem@127.0.0.1:2552]

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Node [akka://ClusterSystem@127.0.0.1:2552] is JOINING, roles [dc-default], version [0.0.0]

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Welcome from [akka://ClusterSystem@127.0.0.1:2551]

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Leader is moving node [akka://ClusterSystem@127.0.0.1:2552] to [Up]
```

After 10 secs is sent a `ClusterCommand` to make the leader leave the cluster. **When this happens, the next node - the one with the lowest IP - takes over the role of leader.** 
In the output, you can see that node 2551 marks itself as _Leaving_ and then _Exiting_ while still being the leader. **These state changes are propagated to all nodes in the cluster.** 
Looking at the terminal of node 2552 you can see the confirmation of the exit and removal or the leader and the election of a new leader.
```
Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Marked address [akka://ClusterSystem@127.0.0.1:2551] as [Leaving]

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Exiting (leader), starting coordinated shutdown

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Leader is moving node [akka://ClusterSystem@127.0.0.1:2551] to [Exiting]

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Exiting completed

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Shutting down...

Cluster Node [akka://ClusterSystem@127.0.0.1:2551] - Successfully shut down

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Exiting confirmed [akka://ClusterSystem@127.0.0.1:2551]

...

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - is the new leader among reachable nodes (more leaders may exist)

Cluster Node [akka://ClusterSystem@127.0.0.1:2552] - Leader is removing confirmed Exiting node [akka://ClusterSystem@127.0.0.1:2551]
```

There are other ways to leave the cluster:

- graceful exit via coordinated shutddown, e.g. `system.terminate()` or by root actor termination
- (graceful exit via HTTP or JMX)
- non-graceful exit, e.g. in case of an abrupt termination, the node will be detected as **unreachable** by other nodes and removed after _Downing_, see below.

---

As you have seen the seed node 2552 knew about the fact the leader was leaving, exiting and removed.
To do so, Akka uses **gossip protocol**: every node gossips to other nodes about its own state and the states that it has seen, making it possible for all nodes in the cluster to reach _convergence_, i.e. to **eventually** agree about the state of every node.

A leader is elected after convergence: the first node (in sort order) that is `Up` automatically becomes the leader (The full remote address of the node is used to sort nodes, like `akka.tcp://words@127.0.0.1:2551`.)

If a node is unreachable then gossip convergence is not possible and therefore most leader actions are impossible. 
By enabling `akka.cluster.allow-weakly-up-members` (which is enabled by default), joining nodes can be promoted to `WeaklyUp` even while convergence is not yet reached. Once gossip convergence can be established again, the leader will move `WeaklyUp` members to `Up`.



--- 


## Java RMI

## MOM


## References

- [Akka in Action](https://livebook.manning.com/book/akka-in-action-second-edition/welcome/v-13)
- [Akka documentation](https://doc.akka.io/docs/akka/current/typed/cluster-membership.html#state-diagrams)