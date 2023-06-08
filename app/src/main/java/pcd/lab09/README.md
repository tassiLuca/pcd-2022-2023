# Actors

## Intro

Actors are entities that **encapsulate state and behavior** and communicate exclusively by exchanging **messages** which are placed into the recipient's **mailbox**.

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

### "One actor is no actor"

> _“A colony of ants is more than just an aggregate of insects that are living together. One ant is no ant. Two ants, and you begin to get something entirely new. Put a million together with the workers divided into different castes, each doing a different function—cutting the leaves, looking after the queen, taking care of the young, digging the nest out, and so on—and you’ve got an organism weighing about 11 kilograms [24 pounds], about the size of a dog, and dominating an area the size of a house”._
>
> David Suzuki. The Sacred Balance: Rediscovering Our Place in Nature.

- actors belong to a hierarchy, a top-down relationship between parents and children.

```scala

```
