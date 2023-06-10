# Actors

## Intro

Actors are entities that **encapsulate state and behavior** and communicate exclusively by exchanging **messages** which are placed into the recipient's **mailbox**.

TODO...

## Akka actors

### Basics

- implementation of the Actor model on the JVM
- it extends the basic Actor model
- provides APIs for developing actor-based systems with Java and Scala DLS with two flavors:
  - _Akka typed_: new, type-safe API
  - _Akka Classic_: original API (still fully supported)

**Akka actor systems**: TODO

```scala
// "Actor" module definition
object Counter:
  // APIs i.e. message that actors should received / send
  enum Command:
    case Tick

  export Command.*
  
  // Behavior of the actor
  def apply(from: Int, to: Int): Behavior[Command] =
    Behaviors.receive { (context, msg) =>
      msg match
        case Tick if from != to =>
          context.log.info(s"Count: $from")
          Counter(from - from.compareTo(to), to) // same of apply(from - from.compareTo(to), to)
        case _ => Behaviors.stopped
    }
```

- `object Counter` is the actor module definition, including message types and behavior
- an enum is used to describe the type of messages exchanged by the actors
- when you create an actor you must specify the messages it can receive (it cannot receive any type of message), i.e. its type, which in Akka terminology is called the **actor's protocol**
  - `Behavior[T]` represents a behavior for processing messages of type `T`
- Build behavior by passing a function to a factory such as `Behaviors.receive(f)`
  - creates the behavior instance immediately before the actor is running
  - state is updated by returning a new behavior that holds the new immutable state, in a pure functional way
  - termination
- An actor is given by a `Behavior[T]` and an `ActorContext[T]` in which the behavior is executed, which provides access to, notably:
  - `context.system(actor system)`
  - `context.self(ActorRef[T] of the executing actor)`
  - `context.spawn(behaviour,name,props)` (creation of child actors, asynchronously)
  - `context.log` (access to logging system)
- Finally, you need to set the behavior for the next message
  - choosing the same behavior, with `Behavior.same`
  - returning a new behavior <ins>**on different immutable state**</ins>
    - :point_right: no mutable state! Everything is immutable
    - after processing a `Tick` message, the next behavior will be `Counter(1, 2)`. This way the **behavior has new input values and the state is transferred from one message to the next**.
  - shutdown through termination of the top-level actor: `Behavior.stopped`
    - once the actor stops, it cannot process any more messages. All messages remaining in its queue or new messages sent to its address are forwarded by the system to another actor named **deadLetters**.
- Recurring pattern: `Behaviors` are nested: `def apply(): Behavior[Command]` it is composed with a `Behaviors.receive` that contains `Behaviors.same`/`Behaviors.stopped`/...

```scala
@main def functionalApi: Unit =
  val system = ActorSystem[Command](guardianBehavior = Counter(0, 2), name = "counter")
  for (_ <- 0 to 2) system ! Tick
```

- the definition of the `apply` method of `ActorSystem` is the following (recall that every object in Scala is created via the `apply` method):
  ```scala
  def apply[T](guardianBehavior: Behavior[T], name: String): ActorSystem[T]
  ```
- the counter actor is instantiated as an `ActorSystem` which is typed as `[Command]`. It means this actor can receive `Command`s messages only (in this case only `Tick`).
- `ActorSystem` is a reference to an actor, an `ActorRef` (`ActorSystem extends ActorRef`). This is the address that you can use to send messages to the actor. To do this, you use the `tell` method, which is often represented with the bang operator, that is, **`!`**.
  - Fire-and-Forget semantic
  - <ins>Akka delivery guarantee: **at-most-once**</ins>
  - <ins>Akka ordering guarantee: **message ordering per sender-receiver pair**</ins>
- The importance of types: if you try to send an `Int` to an actor that cannot receive `Int`, you get an error when compiling. This type checking is one of the biggest improvements over previous versions of Akka. Another advantage is that if you change the protocol of an actor while refactoring and the old protocol is still used elsewhere in the code, the compiler jumps to the rescue.

The state can be implemented by behaviors. **Any real use case protocol is implemented with several types belonging to a hierarchy with a sealed trait at the top** (a sealed trait is a trait that allows the compiler to check if your pattern matching over its cases it is exhaustive. When it is not exhaustive the compiler will raise a warning).

```scala
sealed trait PlayerMessage
case object NewInput extends PlayerMessage
enum GuessOutcome extends PlayerMessage:
  case Guessed
  case Loss
  case NotGuessed(hint: Hint, remainingAttempts: Int)
```

Depending on the state of the actor the same message can do very different things.

Example: we want to model a wallet with two states, represented by two behaviors, **Activated** and **Deactivated**. If the wallet is deactivated the total amount cannot be changed.

```scala
object WalletOnOff {

  sealed trait Command
  final case class Increase(amount: Int) extends Command
  final case object Deactivate extends Command
  final case object Activate extends Command

  // by default its state is activated
  def apply(): Behavior[Command] = activated(0)

  def activated(total: Int): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      message match {
        case Increase(amount) =>
          val current = total + amount
          context.log.info(s"increasing to $current")
          activated(current)
        case Deactivate =>
          deactivated(total)
        case Activate =>
          Behaviors.same
      }
    }
  
  def deactivated(total: Int): Behavior[Command] = {
    Behaviors.receive { (context, message) =>
      message match {
        case Increase(_) =>
          context.log.info(s"wallet is deactivated. Can't increase")
          Behaviors.same
        case Deactivate =>
          Behaviors.same
        case Activate =>
          context.log.info(s"activating")
          activated(total)
      }
    }
  }
```

Another useful factory dealing with timer is `Behaviors.withTimers` that create a scheduler builder. With this builder you can use `.startSingleTimer` to create a timer that schedules a message once:

```scala
  def activated(total: Int): Behavior[Command] =
    Behaviors.receive { (context, message) =>
      Behaviors.withTimers { timers =>
        message match {
          case Increase(amount) =>
            val current = total + amount
            context.log.info(s"increasing to $current")
            activated(current)
          case Deactivate(t) =>
            // Start a timer that will send `Activate` msg once to the 
            // self actor after the given delay
            timers.startSingleTimer(Activate, t.second)
            deactivated(total)
          case Activate =>
            Behaviors.same
        }
      }
    }
```

<ins>**Stashing**</ins>: stashing enables an actor to temporarily buffer all or some messages that cannot or should not be handled using the actor's current behavior.
  - if the actor has to load some initial state or initialize some resources before it can accept the first real message 
  - when the actor is waiting for something to complete before processing the next message.

```scala
def apply(): Behavior[Command] = 
  Behaviors.withStash(100) { buffer =>
    Behaviors.setup[Command] { ctx => start(ctx, buffer) } 
  }

private def start(
  ctx: ActorContext[Command],
  buffer: StashBuffer[Command]
): Behavior[Command] =
  ctx.pipeToSelf(db.load(id)) {
    case Success(value) => InitialState(value)
    case Failure(cause) => DBError(cause) 
  }
  Behaviors.receiveMessage {
    case InitialState(value) => // now we are ready to handle stashed messages
      buffer.unstashAll(activeBehavior(value)) 
    case DBError(cause) => throw cause 
    case other => 
      buffer.stash(other) // stash all other messages for later processing
      Behaviors.same
  }
  
private def activeBehavior(state: String): Behavior[Command] = ???
```

It is important to note that this buffer only lives in memory and is lost when the actor restarts or stops. This can happen with any exception, but with a persistent failure it can be handled so that the buffer is not lost. You can handle this scenario by using `EventSourcedBehavior.onPersistFailure` with a `SupervisorStrategy.restartWithBackoff` available in the Akka persistence API (see Fault Tolerance below section).

### "One actor is no actor"

> _“A colony of ants is more than just an aggregate of insects that are living together. One ant is no ant. Two ants, and you begin to get something entirely new. Put a million together with the workers divided into different castes, each doing a different function—cutting the leaves, looking after the queen, taking care of the young, digging the nest out, and so on—and you’ve got an organism weighing about 11 kilograms [24 pounds], about the size of a dog, and dominating an area the size of a house”._
>
> David Suzuki. The Sacred Balance: Rediscovering Our Place in Nature.

- actors belong to a hierarchy, a top-down relationship between parents and children.

```scala
object GuessNumberMain extends App:
  case object StartPlay

  val system = ActorSystem(
    // guardian actor
    Behaviors.receive[StartPlay.type] { (context, _) =>
      context.log.info("Starting a game.")
      val game = context.spawn(
        GuessGame.game(Random.nextInt(100), 
        numberOfAttempts = 5), 
        "guess-listener"
      )
      val player = context.spawn(
        GuessGame.humanPlayer(game),
        "user"
      )
      player ! GuessGame.NewInput
      Behaviors.same
    },
    "hello-world-akka-system"
  )
  system ! StartPlay
```

- here the behavior is written directly inside the `ActorSystem` instead of inside the `apply` method of a Guardian module, but it's semantically the same!
- `Spawn`: used to create actors from another actor, you use the context by using `context.spawn([someActorsApplyMethod], [someName])`. The signature is the same as the one for the ActorSystem and also creates an ActorRef to which you can send messages.
  - :bulb: <ins>**Design principle: actors should always be as small as possible. Create a subtree of actors to handle some complex request.**</ins>
- `Behaviors.setup`: this factory creates a behavior that is executed only once - when the actor is instantiated. It creates a behavior from a function with only one input parameter, the context. This is its first event in life.

**How to send a message and expect a reply?**

Very common pattern: **Request-Response**, in which the actor I message replies.

```scala
case class Request(query: String, replyTo: ActorRef[Response]) 
case class Response(result: String) 

def server() = Behaviors.receiveMessage[Request] {
  case Request(..., replyTo) => { 
    ...
    replyTo ! Response(...)
    ...
  } 
} 

// Somewhere 
server ! Request(..., context.self)
```

In Akka, `ask` (`ref ? req`) API that returns a future that will be completed when the response is sent back.
**Akka `ask` is a request-response with (implicit) timeout.**

```scala
trait ActorContext ... {
  ...
  def ask[Req, Res](
    target: RecipientRef[Req], 
    createRequest: ActorRef[Res] => Req
  )(
    mapResponse: Try[Res] => T
  )(
    implicit responseTimeout: Timeout, 
    classTag: ClassTag[Res]
  ): Unit
  ... 
}
```
Usage:
```scala
object Worker {
  ...
  final case class Parse(replyTo: ActorRef[Worker.Response]) extends Command
  ... 
}

context.ask(worker, Worker.Parse) {
      case Success(Worker.Done) =>
        Report(s"$text parsed by ${worker.path.name}")
      case Failure(ex) => ...
}
```

- `Worker.Parse` may look like an object, but as you just saw, `createRequest` is a function with the signature `ActorRef[Res] => Req` and not an object. Remember that thanks to the compiler, the case class `Worker.Parse(replyTo: ActorRef[Worker.Response])` creates a method like the following:
  ```scala
  def apply(replyTo: ActorRef[Worker.Response]): Worker.Parse
  ```
  The compiler concludes that you are referring to the apply function of this case class.
- The `mapResponse` is the definition of the callback as Success or Failure, depending on whether the response arrives on time.
- the implicit `responseTimeout: Timeout` is the amount of time the request waits for a response.
- You should not pay much attention to the implicit `classTag`. It is there for historical reasons and for binary compatibility.

As we have seen the guardian actor is responsible for initialization of tasks and create the initial actors of the application, but sometimes you might want to spawn new actors from the outside of the guardian actor (for example creating one actor per HTTP request).

Since it is a common pattern there is a predefined message protocol and implementation of a behavior for this.
It can be used as the guardian actor of the `ActorSystem`, possibly combined with `Behaviors.setup` to start some initial tasks or actors. Child actors can then be started from the outside by telling or asking `SpawnProtocol`.
Using `ask` a `Future` of the `ActorRef` is returned.

```scala
object SpawningProtocol extends App:
  case object Ping
  case object Pong

  val system = ActorSystem(
    Behaviors.setup[SpawnProtocol.Command](_ => SpawnProtocol()),
    "myroot"
  )

  import akka.actor.typed.scaladsl.AskPattern.*
  given ExecutionContext = system.executionContext
  given Scheduler = system.scheduler // for ask pattern
  given Timeout = Timeout(3.seconds) // for ask pattern

  def pongerBehavior: Behavior[Ping.type] = 
    Behaviors.receive[Ping.type] { (ctx, _) =>
      ctx.log.info("pong"); 
      Behaviors.stopped
    }
  val ponger: Future[ActorRef[Ping.type]] = system.ask(
      SpawnProtocol.Spawn(pongerBehavior, "ponger", Props.empty, _)
    )
  for (pongerRef <- ponger) pongerRef ! Ping
```

### Discovering

3 ways to obtain Actor references:

- by creating actors with `ActorContext.spawn`
- by passing actor references as constructor arguments or part of messages
- by discovery via a **receptionist**
  - Idea: the receptionist registers and subscribes actors to a specific key. Thus, all subscribers to that key are notified when registration occurs.

<ins>Example</ins>: have an application that allows to register customers in an hotel, informing the hotel concierges about the high-end registrations, so they can prepare for VIP clients.

<ins>Solution</ins>

- when the VIP guests enter the hotel, the person at the front desk enters the guest's data into the hotel app which creates the `VIPGuest` actor and sends it the `EnterHotel` msg which triggers its registration to the concierge `goldenKey` by sending a `Register` message to the `context.system.receptionist`:
  ```scala
  object VIPGuest {

    sealed trait Command
    final case object EnterHotel extends Command
    final case object LeaveHotel extends Command

    def apply() = Behaviors.receive[Command] { (context, message) =>
      message match {
        case EnterHotel =>
          context.system.receptionist ! Receptionist
            .Register(HotelConcierge.goldenKey, context.self)
          Behaviors.same

        case LeaveHotel =>
          context.system.receptionist ! Receptionist
            .Deregister(HotelConcierge.goldenKey, context.self)
          Behaviors.same
      }
    }
  }
  ```
  - For the key, you need to define the `ServiceKey`. In this case it is defined inside the `HotelConcierge` actor.

- `HotelConcierge` actor subscribes to a key, called `goldenKey`
  ```scala
  object HotelConcierge {
    val goldenKey = ServiceKey[VIPGuest.Command]("concierge-key")

    sealed trait Command
    private final case class ListingResponse(listing: Receptionist.Listing) extends Command

    def apply() = Behaviors.setup[Command] { context =>
      val listingNotificationAdapter =
        context.messageAdapter[Receptionist.Listing](ListingResponse)

      context.system.receptionist ! Receptionist
        .Subscribe(goldenKey, listingNotificationAdapter)

      Behaviors.receiveMessage {
        case ListingResponse(goldenKey.Listing(listings)) =>
          listings.foreach { actor =>
            context.log.info(s"${actor.path.name} is in")
          }
          Behaviors.same
      }
    }
  }
  ```
  - it needs an adapter since the notification it receives does not belong to its protocol. It belongs to the receptionist API. It is an `akka.actor.typed.receptionist.Receptionist.Listing`. So the adapter needs to wrap this message into `ListingResponse` message which belongs to the concierge.
    - In Akka, an adapter actor is a special type of actor that acts as a bridge between different actor systems or components within an application.
    - The `VIPGuest` actor (when receiving `EnterHotel` msg) sends a `Register` one to the `context.system.receptionist` which notifies the adapter. The adapter wraps the message into a `ListingResponse` and sends it to the `HotelConcierge` actor.
  - With the adapter the concierge can subscribe by sending a `Subscribe`
message with the `goldenKey`
  - when the concierge receives the list of registration it logs the name of each guest. This happens every time a guest registers.

### Fault Tolerance

- having a system faults free is an illusion: having a system highly available and distributed is not possible. Simply because some parts of the system aren't under your control, and these can break (e.g. the network).
- things break $\rightarrow$ recovery strategy
- **Validation errors should be modeled as part of the actor protocol**
- **Failure - Philosophy: wrap it up and let it crash**:
  - no catch of exceptions
  - keeps things clear: the actor code contains only happy path processing logic and **no** error handling or fault recovery logic
  - the mailbox for a failing actor is suspended until the supervising behavior in the recovery flow has decided what to do with the exception. The messages received will be stashed.
- the supervisor uses the exception to decide what to do with the faulty actors it supervises
  - _restart_: the actor maintains the same actor ref
    - restart with backoff
    - restart with limit (for example no more than 10 times in a 10s period)
  - _resume_ : the same actor instance should continue to process messages. the exception that would cause the actor to crash is logged only by the supervisor.
  - _stop_: the actor must be terminated. It will no longer participate in the processing of messages.
- **Principle**: the most dangerous actors (actors most likely to crash) should be as far down the hierarchy as possible. Faults that occur far down the hierarchy can be handled or monitored by more actors than a fault that occurs higher up the hierarchy. If a fault occurs at the top level of the actor system, it could restart all top-level actors or even shut down the actor system.

Customising supervision: wrap the actor behavior using `Behaviors.supervise(s)`. Typically you would wrap the actor with supervision in the parent when spawning it as
a child.
```scala
Behaviors.supervise(someBehavior())
  .onFailure(SupervisorStrategy.restart)
```

> **_Note_**: Stop is the default supervisor strategy (stopping is the safest choice).

- In Akka typed can also deal with **signal-type messages**.
  - signals can only be sent from the underlying system to an actor
- it is possible for each actor to establish a relationship with another actor by observing when that actor is terminated or fails. This is called **watching**.

```scala
object SupervisionExample {
  def apply(): Behavior[String] =
    Behaviors
      .receivePartial[String] {
        ...
      }
      .receiveSignal {
        case (context, Terminated(actorRef)) =>
          Behaviors.stopped
      } 
}
```

- `receiveSignal` is the API to handle signal messages
- `Terminated(ref)`: This signal is sent to a supervisor actor when one of its child actors terminates. It allows supervisors to handle the termination of child actors and potentially take corrective action
- Other important signals:
  - `PreStart`(`PostStop`): This signal is sent to an actor before (after) it starts (it has stopped) processing messages. It allows actors to perform initialization tasks or acquire necessary resources (It allows actors to release resources or perform cleanup tasks).
  - `PreRestart`: This signal is sent to an actor when it is about to be restarted due to a failure. It allows actors to perform any necessary preparation before restarting.
  - `PostRestart`: This signal is sent to an actor after it has been restarted. It allows actors to recover state or perform any necessary actions after a restart.
  - `Failure`: This signal is sent to an actor when a message it sends to another actor fails to be delivered. It allows actors to handle message delivery failures and potentially take recovery measures.
  - `Kill`: This signal is sent to an actor to forcefully terminate it. It is a system-level signal that can be used to shut down misbehaving or unresponsive actors.
- <ins>In Akka Typed, a parent must watch a child to be notified about its termination</ins>. If the parent does not handle `Terminated` it will itself fail with a `DeathPactException`.

It is possible to handle different exceptions with different supervision strategies.

```scala
Behavior.supervise(
  Behavior.supervise(
     Behaviors.supervise(
       behavior
     ).onFailure[ExceptionX](SupervisorStrategy.resume)
  ).onFailure[ExceptionY](SupervisorStrategy.restart)
).onFailure[ExceptionZ](SupervisorStrategy.stop)
```

### Akka test kit
