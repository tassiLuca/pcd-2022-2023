---------------------------- MODULE step4_philo ----------------------------

EXTENDS Integers, Sequences, TLC, FiniteSets
CONSTANTS NumPhilosophers
ASSUME NumPhilosophers > 0
NP == NumPhilosophers

(* --algorithm dining_philosophers

variables 
    forks = [fork \in 1..NP |-> 1],
    forks_alloc = [ p \in 1..NP |-> << p, (p + 1) % NP>> ];
    
macro wait(s) begin
  await s > 0;  
  s := s - 1;
end macro;

macro signal(s) begin
  s := s + 1;
end macro;


macro acquire_fork(fork) begin
    await forks[fork] > 0;
    forks[fork] := 0;
end macro;

macro release_fork(fork) begin
    forks[fork] := 1;
end macro;


process philosopher \in 1..NP
begin MainLoop:
  while TRUE do
    either
      Think: print "think";
    or
      l1: acquire_fork(self);
      l2: acquire_fork(((self + 1) % NP) + 1); 
      Eat: print "eat";
      l3: release_fork(self);
      l4: release_fork(((self + 1) % NP) + 1);         
    end either;
  end while;
end process;
end algorithm; *)


\* BEGIN TRANSLATION (chksum(pcal) = "921dcde0" /\ chksum(tla) = "d7eb76bf")
VARIABLES forks, forks_alloc, pc

vars == << forks, forks_alloc, pc >>

ProcSet == (1..NP)

Init == (* Global variables *)
        /\ forks = [fork \in 1..NP |-> 1]
        /\ forks_alloc = [ p \in 1..NP |-> << p, (p + 1) % NP>> ]
        /\ pc = [self \in ProcSet |-> "MainLoop"]

MainLoop(self) == /\ pc[self] = "MainLoop"
                  /\ \/ /\ pc' = [pc EXCEPT ![self] = "Think"]
                     \/ /\ pc' = [pc EXCEPT ![self] = "l1"]
                  /\ UNCHANGED << forks, forks_alloc >>

Think(self) == /\ pc[self] = "Think"
               /\ PrintT("think")
               /\ pc' = [pc EXCEPT ![self] = "MainLoop"]
               /\ UNCHANGED << forks, forks_alloc >>

l1(self) == /\ pc[self] = "l1"
            /\ forks[self] > 0
            /\ forks' = [forks EXCEPT ![self] = 0]
            /\ pc' = [pc EXCEPT ![self] = "l2"]
            /\ UNCHANGED forks_alloc

l2(self) == /\ pc[self] = "l2"
            /\ forks[(((self + 1) % NP) + 1)] > 0
            /\ forks' = [forks EXCEPT ![(((self + 1) % NP) + 1)] = 0]
            /\ pc' = [pc EXCEPT ![self] = "Eat"]
            /\ UNCHANGED forks_alloc

Eat(self) == /\ pc[self] = "Eat"
             /\ PrintT("eat")
             /\ pc' = [pc EXCEPT ![self] = "l3"]
             /\ UNCHANGED << forks, forks_alloc >>

l3(self) == /\ pc[self] = "l3"
            /\ forks' = [forks EXCEPT ![self] = 1]
            /\ pc' = [pc EXCEPT ![self] = "l4"]
            /\ UNCHANGED forks_alloc

l4(self) == /\ pc[self] = "l4"
            /\ forks' = [forks EXCEPT ![(((self + 1) % NP) + 1)] = 1]
            /\ pc' = [pc EXCEPT ![self] = "MainLoop"]
            /\ UNCHANGED forks_alloc

philosopher(self) == MainLoop(self) \/ Think(self) \/ l1(self) \/ l2(self)
                        \/ Eat(self) \/ l3(self) \/ l4(self)

Next == (\E self \in 1..NP: philosopher(self))

Spec == Init /\ [][Next]_vars

\* END TRANSLATION 
=============================================================================
\* Modification History
\* Last modified Sun Mar 28 15:27:11 CEST 2021 by aricci
\* Created Sun Mar 28 15:25:23 CEST 2021 by aricci
