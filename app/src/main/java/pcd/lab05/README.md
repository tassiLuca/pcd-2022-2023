# Lab05

## Mandelbrot case study

This is a not so simple example of concurrent application which takes into account all the good design principles for multithreaded applications that we have studied so far.

## TLA+

TLA+ sources can be found [here](../../../tla+-specs/).

For a comprehensive guide on TLA+ see the following [website](https://learntla.com/index.html).

Here only the basics:

### Intro

- TLA+ is used to model systems "above the code level" - high-level models (aka architecture level):
  - before writing code, define a high-level model (architecture) of what the code should do and how it should do it
  - having only a vague, incomplete model leads to basic design errors that the best coding won't correct
- TLA+ is a language for writing precise high-level models of what code does and how it does it.
  - precise models are good not only fo tiny well-defined problems
  - the more complex a system is, the more important it is to make it as simple as possible. In complex systems, simplicity isn't achieved by coding tricks. It's achieved by rigorous thinking above the code level

:point_right: _A design error found after the code has been written is usually fixed with an ad hoc patch that is unlikely to eliminate all instances of the problem and is likely to introduce new errors._

:point_right: **Design errors should be caught by writing a precise high-level model before the code is written.**

- Models in programming/software engineering describe the behavior of a system as a set of discrete events:
  - no model is a completely accurate description of a real system 
  - a model is a description of some aspect of the system, written for some purpose.
- TLA+ models are called **_specifications_** 
- TLA+ models are state-based: execution of a system modeled as a sequence of states. A sequence of states is called **_behavior_**. An event is represented by a pair of consecutive states.

### Fundamentals

- :poop: the name of the module must match the name of the file, or tla+ will consider the spec invalid.
- :warning: :poop: All TLA+ specs must start with at least four `–` on each side of the `MODULE` and four `=` at the end. Everything outside these two boundaries is ignored, and people often put metadata there.
- The TLA+ keyword for an import is `EXTENDS`. If we EXTENDS Integers, we also get arithmetic.
- Single line comments are `\*`, comment blocks are `(* ... *)`
  - :question: The algorithm is inside a comment!
    ```tla
    (*--algorithm my_algorithm
    
        <stuffs>

    end algorithm;*)
    ```
  - We write algorithm in PlusCal and then TLA+ Toolbox translates it into TLA+ model
    - similar to simple programming languages, but with constructs for describing concurrency and non determinacy
    - less powerful that TLA+: not every TLA+ specification can be described in PlusCal 
    - more expressive than any programming language
    - any mathematical formula can be used as a PlusCal expression
      - syntax similar to $\LaTeX$
  - Shortcut to translate to PlusCal `Ctrl-T`, or `Cmd-T` on macOS.

- `Print(val, out)` prints `val` and `out` to User Output and then evaluates to out.
  - `PrintT(val)` is equivalent to writing `Print(val, TRUE)`

- **Operators**:
  - `=`: equals
  - `/=`: not equals
  - `/\`: AND
  - `\/`: OR
  - `:=`: assignment
  - `~`: NOT
- :poop: If you’re assigning a value to a variable for the very first time, you use `=`. However, if the variable already exists and you assign a new value to it, you use `:=`
- **Structured types**
  - **Sets**: unordered collections of elements. All elements in the set must have the same type.

    | **Operator**          | **Meaning**           | **Example**                                          |
    |:---------------------:|:---------------------:|:----------------------------------------------------:|
    | `x \in set`           | is element of set     | `>> 1 \in 1..2` $\rightarrow$ `TRUE`                 |
    | `x \notin set`        | is not element of set | `>> 1 \notint 1..2` $\rightarrow$ `FALSE`            |
    | `set1 \subseteq set2` | is subset of set      | `>> {1, 2} \subseteq {1, 2, 3}` $\rightarrow$ `TRUE` |
    | `set1 \union set2`    | set union             | `>> (1..2) \union (2..3)` $\rightarrow$ `{1, 2, 3}`  |
    | `set1 \intersect set2`| set intersection      | `>> (1..2) \intersect (2..3)` $\rightarrow$ `{2}`    |
    | `set1 \ set2`         | set difference        | `>> (1..2) \ (2..3)` $\rightarrow$ `{1}`             |
    | `Cardinality(set)`    | #elements in the set* | `>> Cardinality({1, 2, 3})` $\rightarrow$ 3          |

    \* requires `EXTENDS FiniteSets` 
  
  - **Tuples or Sequences**: ordered collections of elements with the index <ins>starting at 1</ins>
  
    | **Operator**          | **Meaning**           | **Example**                                          |
    |:---------------------:|:---------------------:|:----------------------------------------------------:|
    | `Head(seq)`           | head                  | `>> Head(<<1, 2>>)` $\rightarrow$ `1`                |
    | `Tail(seq)`           | tail                  | `>> Tail(<<1, 2>>)` $\rightarrow$ `2`                |
    | `Append(seq, x)`      | append                | `>> Append(<<1, 2>>, 3)` $\rightarrow$ `<<1, 2, 3>>` |
    | `seq \o seq2`         | combine               | `>> <<1>> \o <<2, 3>>` $\rightarrow$ `<<1, 2, 3>>`   |
    | `Len(seq)`            | length of sequence    | `>> <<1, 2, 3>>` $\rightarrow$ `3`                   |

  - **Structures** map string to values. 
    - You write them as `[key1 |-> val1, key2 |-> val2, ...]`. The values do not have to be the same type. You get the value with `struct.key`:
      ```tla
      >> [a |-> 1, b |-> <<1, {}>>].b
      <<1, {}>>
      ```
    - to generate a set of structures, we use a different syntax. Instead of writing `[key |-> val]`, we write `[key: set]`
      ```
      >> [a: {"a", "b"}, b: (1..2)]
      { [a |-> "a", b |-> 1], [a |-> "a", b |-> 2], [a |-> "b", b |-> 1], [a |-> "b", b |-> 2] }
      ```

  - **Functions**: A function maps a set of inputs (its domain) to a set of outputs. The mapping can either be set manually or via an expression. All functions have the form `[x \in set |-> P(x)]`.
    - A function that maps every element in a set of numbers to its double might be written as `[x \in numbers |-> x * 2]`.

- At the beginning of the algorithm are defined the global variables:
  ```tla
  (*--algorithm wire
      variables
          people = {"alice", "bob"},  \* a set
          acc = [p \in people |-> 5]; \* a map - dictionary
  begin skip;
  end algorithm;*)
  ```
  - `people` is an unordered collection (set)
  - `acc` is like a dictionary or map
    - for each value in a set, it maps to some output value
    - This means `acc["alice"] = acc["bob"] = 5` (this would be equivalent, in a language like Python, to writing `{"alice": 5, "bob": 5}`)
    - We could also make the function depend on each element. For example, we could write `double = [x \in 1..10 |-> 2*x]`.
  - TLA+ provides the `a..b` shorthand for all integers between `a` and `b` (inclusive):
    - `x \in 1..6;`: this means `x` is any possible element in the set. **TLC would first try running the whole algorithm with 1, 2, ..., 6. If we added a second variable `y` that also used `\in`, TLC would check every single possible combination of `x` and `y`**.

- Constants are values that are defined in the model ("Model Overview" tab) instead of the specification
  ```tla
  EXTENDS Integers, TLC
  CONSTANTS Capacity, Items, SizeRange, ValueRange
  ```

- Basilar language constructs:
  - `assert`: same as Java. You need to add EXTENDS TLC.
  - `skip`: A no-op.
  - `if`:
    ```tla
    if <condition1> then
        <body>
    elsif <condition2> then
        <body>
    else 
        <body>
    end if;
    ```
  - `case`:
    ```tla
    CASE x = 1 -> TRUE
        [] x = 2 -> TRUE
        [] x = 3 -> 7
        [] OTHER -> FALSE
    ```
  - `while`:
    ```tla
    while <condition> do
        <body>
    end while;
    ```
  - `macros`: To clean up specs a little, we can add macros before the begin. You can place assignments, assertions, and if statements in macros, but <ins>not</ins> while loops. You also <ins>cannot</ins> assign to any variable more than once. You can refer to outside values in the macro, and you can assign to outside variables.
    ```
    macro name(arg1, arg2) begin
        \* assignments
    end macro;
    begin
        name(x, y);
    end algorithm;
    ```

- In PlusCal, **each algorithm happening simultaneously belongs to its own process**. Each process can have its own code and its own local variables.
  ```tla
  process p \in 1..2
  variable x \in 1..3
  begin
    l1: print self;
    l2: print x;
  end process;
  ```

### Invariants and Temporal Properties

- **Invariant definition: something we want to be true in every state of the system. If it is false our specification has an error**
  ```tla
  define
      NoOverflowInvariant == (c < 3) 
  end define;
  ```
  - `\A` means “all elements in a set.” It’s used in the form `\A x \in set: P(x)`, which means “for all elements in the set, `P(x)` is true”. For example to check that all numbers in a set are less than a given number:
    - `AllLessThan(set, max) == \A num \in set: num < max`
  - `\E` means “there exists some element in the set.” It’s used in the form `\E x \in set: P(x)`, which means “there is at least one element in the set where `P(x)` is true”. For example to check that a given sequence has at least one element in a given set:
    - `SeqOverlapsSet(seq, set) == \E x \in 1..Len(seq): seq[x] \in set`
  - `P => Q` means that if `P` is true then `Q` is true.
  - `P <=> Q` mean that either `P` and `Q` are both true or `P` and `Q` are both false
- **Temporal Property**: using LTL
  - Eventually, diamond operator: `<>`
  - Always, box operator: `[]`
  - Leads-to: `~>`
  - Example: `EventuallyConsistent == <>[](acc["alice"] + acc["bob"] = 10)`

### Nondeterministic Behavior

(see `step1g_nondet`)

- **`Either`**
  ```tla
  either
      \* branch 1
  or
      \* branch 2
      \* ...
  or
      \* branch n
  end either;
  ```
  When you model-check your spec, TLC will check all branches simultaneously. We can use this to represent one of several possibilities happening. **There is no way to make one possibility more likely than the other. We generally assume that if some possible choice invalidates our spec, no matter how unlikely, it’s something we’ll want to fix.**

- **`With`**
  ```tla
  with var \in set do
      \* body
  end with;
  ```
  TLC will check what happens for all possible assignments of var to elements of set. If the set is empty, the spec halts until the set is not empty. `with` statements follow macro rules: no double-assignments and no `while` loops. You can place `with` statements inside macros.

### Concurrency

- TLA+ specs are full of **labels**. If we label a statement, **all statements included between this statement and the next labeled statement are executed atomically**. That is: not only a single statement, but the block of statements included between two labels (the last statement is always labeled as `Done`). For example
  ```tla
  p1: 
      while (i < N) do
          print "step1";
          print "step2";
      end while;
  p2:
      print "done"
  ```
  In the above snippet, the block of actions delimited between the labels `p1` and `p2`, including the test `(i < N)` and the two `print`, is executed atomically.

  Other example:
  ```
  p1:
      while (i < N) do
          print "step1";
  p2:     
          print "step2";
      end while;
  p3: 
      print "done"
  ```
  In the above snippet, there are 3 atomic blocks: `(i < N)` and `print "step 1"`, `print "step 2"`, `print "done"`.
  - you must have a label at the beginning of each process and before every "while"
  - you may not place a label inside a macro or a `with` statement
  - if you use an `if` or an `either` control structure and any possible branch has a label inside it, you must place a label after the end of the control structure
  - you may not assign the same variable twice in a label

- more processes in the spec means it is **concurrent**.
  - unlike with single-process algorithms, all processes must explicitly use (and begin with) labels.

- `await Expression` prevents a step from running until `Expression` is true. You can also use the keyword when:
  ```tla
  process reader = "reader"
  variable current_message = "none";
  begin Read:
      while TRUE do
          await queue /= <<>>; \* continues only when the queue is not empty
          current_message := Head(queue);
          queue := Tail(queue);
      end while;
  end process;
  ```
  - if all processes are prevented from continuing $\rightarrow$ _deadlock_!

- in the above example, if we want to model a set of readers:
  ```tla
  process reader \in {"r1", "r2"}
  ```
  TLC will create two copies of reader: one for each element, and assign each of them its own set of local variables. If a process has multiple copies, such as “r1” and “r2”, `self` is whatever value that given copy is assigned to.

- **Procedures**: to share multiple-step behavior between processes without `macro` limitations
  ```tla
  procedure add_to_queue(val="") begin
      Add:
          await Len(queue) < MaxQueueSize;
          queue := Append(queue, val);
          return;
  end procedure;
  ```
  - :poop: `return` does not return any value to the calling process. It simply ends the procedure.
  - In order to call a procedure in a process, we have to prefix it with `call`. A called procedure must be immediately followed by a label, the end of an enclosing block, a `goto`, or a `return`
  - procedures can use macros but macros cannot use procedures, so procedures must follow macros
    - processes can call procedures and macros, but procedures cannot use processes

- Since TLA+ could be used also for modeling distributed architectures:
  - **Stuttering problem**: when a process simply stops, e.g. the case where the server crashes in between steps or the power goes out... Those are all real and quite worrisome concerns, and they are all represented by the stuttering state.
    - see `step1b_liveness_not_ok_stuttering`: `ProperFinalValue` property is not satisfied and an error pops up
  - if we are not in a distributed scenario and we don't want TLA+ complaining about this situation label each process as **`fair`** (**weakly fair**) or **`fair+`** (**weakly fair**)
    - see `step1c_liveness_ok_fairness`, `step1d_fairness_not_ok`, `step1e_fairness_strong_ok`
    - _weakly fair_: if a condition stays enabled, the action will eventually happen:
      ```tla
      fair process light = "light"
      begin Cycle:
          while at_light do
            light := NextColor(light);
          end while;
      end process;

      fair process car = "car"
      begin Drive:
          when light = "green";
          at_light := FALSE;
      end process;
      ```
      What if the light keeps cycling between green and red? The Drive action is enabled, then disabled, then enabled again, ad infinitum. But weak fairness only guarantees the action will happen if it stays enabled. If the light is always green, the car will eventually drive through. But since it can keep cycling, the car is stuck.
    - _strongly fair_: even if a condition is repeatedly enabled, the action will eventually happen
      ```tla
      fair+ process car = "car"
      begin Drive:
          when light = "green";
          at_light := FALSE;
      end process;
      ```
      Even if the light keeps cycling, the Drive action is repeatedly enabled and so is guaranteed to happen. Note that this still requires the light to be weakly fair: if it’s unfair, it can simply cycle to red and stay there.

Steps in the repo 2-5 with P/C, R/W, Mutual Exclusion, ...