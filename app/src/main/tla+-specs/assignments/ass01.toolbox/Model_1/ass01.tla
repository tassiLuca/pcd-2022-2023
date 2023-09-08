------------------------------- MODULE ass01 -------------------------------

EXTENDS TLC, Integers, Sequences, FiniteSets

MaxSrcBufferSize == 2
SrcAnalysers == { "src-an1", "src-an2" }

(*--algorithm ass01

variable   
  srcTestFiles = << [ file |-> "a.java", nLoc |-> 1 ],
                    [ file |-> "b.java", nLoc |-> 3 ],
                    [ file |-> "c.java", nLoc |-> 1 ],
                    [ file |-> "d.java", nLoc |-> 1 ] >>,
  locBands = << 0, 0, 0, 0, 0 >>,
  srcBuffer = <<>>,             
  srcDiscoverStarted = FALSE,   
  srcBufferClosed = FALSE, 
  stopped = FALSE,     
  numSrcAnalysersDone = 0,
  numFilesToProcess = Len(srcTestFiles),
  numSrcAnalysed = 0;   
  allSrcAnalysed = FALSE;

define 
    BufferBounded == Len(srcBuffer) <= MaxSrcBufferSize 
    AllSrcAnalysedOrStopped == <>(stopped \/ (numSrcAnalysed = numFilesToProcess))
    NoElemsInBuffer == <>(stopped \/ (srcBufferClosed /\ Len(srcBuffer) = 0))
end define;

(* master agent *)

fair+ process master  = "master"
begin
  p1:
    srcBuffer := <<>>;
    srcBufferClosed := FALSE;
    srcDiscoverStarted := TRUE;  
    allSrcAnalysed := FALSE;   
  p2:
    await allSrcAnalysed \/ stopped;
end process;

(* single src-discover agent *)

fair+ process srcDiscover  = "src-disc"
variable src;
begin
  p1:
    await srcDiscoverStarted;
  p2: 
    while (srcTestFiles /= <<>> /\ ~stopped) do                
        src := Head(srcTestFiles);
        srcTestFiles := Tail(srcTestFiles);
  put: 
        await (Len(srcBuffer) < MaxSrcBufferSize) \/ stopped; 
        if ~stopped then
            srcBuffer := Append(srcBuffer, src);
        end if;
    end while;
  p3:
    srcBufferClosed := TRUE;
end process;

(* a number of src-analyser agents *)

fair+ process srcAnalyser \in SrcAnalysers
variable src, nloc;
begin 
consume:
  while ((srcBuffer /= <<>>) \/ (srcBuffer = <<>> /\ ~srcBufferClosed)) /\ ~stopped do
take: 
    await (srcBuffer /= <<>>) \/ srcBufferClosed \/ stopped;
    if (~stopped /\ srcBuffer /= <<>>) then
      src := Head(srcBuffer);
      srcBuffer := Tail(srcBuffer);
elab:
      locBands[src.nLoc] := locBands[src.nLoc] + 1;
      numSrcAnalysed := numSrcAnalysed + 1;
    else skip
    end if;
  end while;
notifyEnd:
  numSrcAnalysersDone := numSrcAnalysersDone + 1;     
  if numSrcAnalysersDone = Cardinality(SrcAnalysers) then
     allSrcAnalysed := TRUE;
  else 
     skip;     
  end if;
end process;

(* simulating stopped event coming from the GUI *) 

fair+ process guiAgent  = "gui"
begin
  p1:
    stopped := FALSE;     
  p2:
  either 
    stopped := TRUE;
  or 
    skip;
  end either ;
end process;


end algorithm;*)
\* BEGIN TRANSLATION (chksum(pcal) = "7794757d" /\ chksum(tla) = "146472a8")
\* Label p1 of process master at line 36 col 5 changed to p1_
\* Label p2 of process master at line 41 col 5 changed to p2_
\* Label p1 of process srcDiscover at line 50 col 5 changed to p1_s
\* Label p2 of process srcDiscover at line 52 col 5 changed to p2_s
\* Process variable src of process srcDiscover at line 47 col 10 changed to src_
CONSTANT defaultInitValue
VARIABLES srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
          srcBufferClosed, stopped, numSrcAnalysersDone, numFilesToProcess, 
          numSrcAnalysed, allSrcAnalysed, pc

(* define statement *)
BufferBounded == Len(srcBuffer) <= MaxSrcBufferSize
AllSrcAnalysedOrStopped == <>(stopped \/ (numSrcAnalysed = numFilesToProcess))
NoElemsInBuffer == <>(stopped \/ (srcBufferClosed /\ Len(srcBuffer) = 0))

VARIABLES src_, src, nloc

vars == << srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
           srcBufferClosed, stopped, numSrcAnalysersDone, numFilesToProcess, 
           numSrcAnalysed, allSrcAnalysed, pc, src_, src, nloc >>

ProcSet == {"master"} \cup {"src-disc"} \cup (SrcAnalysers) \cup {"gui"}

Init == (* Global variables *)
        /\ srcTestFiles = << [ file |-> "a.java", nLoc |-> 1 ],
                             [ file |-> "b.java", nLoc |-> 3 ],
                             [ file |-> "c.java", nLoc |-> 1 ],
                             [ file |-> "d.java", nLoc |-> 1 ] >>
        /\ locBands = << 0, 0, 0, 0, 0 >>
        /\ srcBuffer = <<>>
        /\ srcDiscoverStarted = FALSE
        /\ srcBufferClosed = FALSE
        /\ stopped = FALSE
        /\ numSrcAnalysersDone = 0
        /\ numFilesToProcess = Len(srcTestFiles)
        /\ numSrcAnalysed = 0
        /\ allSrcAnalysed = FALSE
        (* Process srcDiscover *)
        /\ src_ = defaultInitValue
        (* Process srcAnalyser *)
        /\ src = [self \in SrcAnalysers |-> defaultInitValue]
        /\ nloc = [self \in SrcAnalysers |-> defaultInitValue]
        /\ pc = [self \in ProcSet |-> CASE self = "master" -> "p1_"
                                        [] self = "src-disc" -> "p1_s"
                                        [] self \in SrcAnalysers -> "consume"
                                        [] self = "gui" -> "p1"]

p1_ == /\ pc["master"] = "p1_"
       /\ srcBuffer' = <<>>
       /\ srcBufferClosed' = FALSE
       /\ srcDiscoverStarted' = TRUE
       /\ allSrcAnalysed' = FALSE
       /\ pc' = [pc EXCEPT !["master"] = "p2_"]
       /\ UNCHANGED << srcTestFiles, locBands, stopped, numSrcAnalysersDone, 
                       numFilesToProcess, numSrcAnalysed, src_, src, nloc >>

p2_ == /\ pc["master"] = "p2_"
       /\ allSrcAnalysed \/ stopped
       /\ pc' = [pc EXCEPT !["master"] = "Done"]
       /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
                       srcBufferClosed, stopped, numSrcAnalysersDone, 
                       numFilesToProcess, numSrcAnalysed, allSrcAnalysed, src_, 
                       src, nloc >>

master == p1_ \/ p2_

p1_s == /\ pc["src-disc"] = "p1_s"
        /\ srcDiscoverStarted
        /\ pc' = [pc EXCEPT !["src-disc"] = "p2_s"]
        /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
                        srcBufferClosed, stopped, numSrcAnalysersDone, 
                        numFilesToProcess, numSrcAnalysed, allSrcAnalysed, 
                        src_, src, nloc >>

p2_s == /\ pc["src-disc"] = "p2_s"
        /\ IF (srcTestFiles /= <<>> /\ ~stopped)
              THEN /\ src_' = Head(srcTestFiles)
                   /\ srcTestFiles' = Tail(srcTestFiles)
                   /\ pc' = [pc EXCEPT !["src-disc"] = "put"]
              ELSE /\ pc' = [pc EXCEPT !["src-disc"] = "p3"]
                   /\ UNCHANGED << srcTestFiles, src_ >>
        /\ UNCHANGED << locBands, srcBuffer, srcDiscoverStarted, 
                        srcBufferClosed, stopped, numSrcAnalysersDone, 
                        numFilesToProcess, numSrcAnalysed, allSrcAnalysed, src, 
                        nloc >>

put == /\ pc["src-disc"] = "put"
       /\ (Len(srcBuffer) < MaxSrcBufferSize) \/ stopped
       /\ IF ~stopped
             THEN /\ srcBuffer' = Append(srcBuffer, src_)
             ELSE /\ TRUE
                  /\ UNCHANGED srcBuffer
       /\ pc' = [pc EXCEPT !["src-disc"] = "p2_s"]
       /\ UNCHANGED << srcTestFiles, locBands, srcDiscoverStarted, 
                       srcBufferClosed, stopped, numSrcAnalysersDone, 
                       numFilesToProcess, numSrcAnalysed, allSrcAnalysed, src_, 
                       src, nloc >>

p3 == /\ pc["src-disc"] = "p3"
      /\ srcBufferClosed' = TRUE
      /\ pc' = [pc EXCEPT !["src-disc"] = "Done"]
      /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
                      stopped, numSrcAnalysersDone, numFilesToProcess, 
                      numSrcAnalysed, allSrcAnalysed, src_, src, nloc >>

srcDiscover == p1_s \/ p2_s \/ put \/ p3

consume(self) == /\ pc[self] = "consume"
                 /\ IF ((srcBuffer /= <<>>) \/ (srcBuffer = <<>> /\ ~srcBufferClosed)) /\ ~stopped
                       THEN /\ pc' = [pc EXCEPT ![self] = "take"]
                       ELSE /\ pc' = [pc EXCEPT ![self] = "notifyEnd"]
                 /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, 
                                 srcDiscoverStarted, srcBufferClosed, stopped, 
                                 numSrcAnalysersDone, numFilesToProcess, 
                                 numSrcAnalysed, allSrcAnalysed, src_, src, 
                                 nloc >>

take(self) == /\ pc[self] = "take"
              /\ (srcBuffer /= <<>>) \/ srcBufferClosed \/ stopped
              /\ IF (~stopped /\ srcBuffer /= <<>>)
                    THEN /\ src' = [src EXCEPT ![self] = Head(srcBuffer)]
                         /\ srcBuffer' = Tail(srcBuffer)
                         /\ pc' = [pc EXCEPT ![self] = "elab"]
                    ELSE /\ TRUE
                         /\ pc' = [pc EXCEPT ![self] = "consume"]
                         /\ UNCHANGED << srcBuffer, src >>
              /\ UNCHANGED << srcTestFiles, locBands, srcDiscoverStarted, 
                              srcBufferClosed, stopped, numSrcAnalysersDone, 
                              numFilesToProcess, numSrcAnalysed, 
                              allSrcAnalysed, src_, nloc >>

elab(self) == /\ pc[self] = "elab"
              /\ locBands' = [locBands EXCEPT ![src[self].nLoc] = locBands[src[self].nLoc] + 1]
              /\ numSrcAnalysed' = numSrcAnalysed + 1
              /\ pc' = [pc EXCEPT ![self] = "consume"]
              /\ UNCHANGED << srcTestFiles, srcBuffer, srcDiscoverStarted, 
                              srcBufferClosed, stopped, numSrcAnalysersDone, 
                              numFilesToProcess, allSrcAnalysed, src_, src, 
                              nloc >>

notifyEnd(self) == /\ pc[self] = "notifyEnd"
                   /\ numSrcAnalysersDone' = numSrcAnalysersDone + 1
                   /\ IF numSrcAnalysersDone' = Cardinality(SrcAnalysers)
                         THEN /\ allSrcAnalysed' = TRUE
                         ELSE /\ TRUE
                              /\ UNCHANGED allSrcAnalysed
                   /\ pc' = [pc EXCEPT ![self] = "Done"]
                   /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, 
                                   srcDiscoverStarted, srcBufferClosed, 
                                   stopped, numFilesToProcess, numSrcAnalysed, 
                                   src_, src, nloc >>

srcAnalyser(self) == consume(self) \/ take(self) \/ elab(self)
                        \/ notifyEnd(self)

p1 == /\ pc["gui"] = "p1"
      /\ stopped' = FALSE
      /\ pc' = [pc EXCEPT !["gui"] = "p2"]
      /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
                      srcBufferClosed, numSrcAnalysersDone, numFilesToProcess, 
                      numSrcAnalysed, allSrcAnalysed, src_, src, nloc >>

p2 == /\ pc["gui"] = "p2"
      /\ \/ /\ stopped' = TRUE
         \/ /\ TRUE
            /\ UNCHANGED stopped
      /\ pc' = [pc EXCEPT !["gui"] = "Done"]
      /\ UNCHANGED << srcTestFiles, locBands, srcBuffer, srcDiscoverStarted, 
                      srcBufferClosed, numSrcAnalysersDone, numFilesToProcess, 
                      numSrcAnalysed, allSrcAnalysed, src_, src, nloc >>

guiAgent == p1 \/ p2

(* Allow infinite stuttering to prevent deadlock on termination. *)
Terminating == /\ \A self \in ProcSet: pc[self] = "Done"
               /\ UNCHANGED vars

Next == master \/ srcDiscover \/ guiAgent
           \/ (\E self \in SrcAnalysers: srcAnalyser(self))
           \/ Terminating

Spec == /\ Init /\ [][Next]_vars
        /\ SF_vars(master)
        /\ SF_vars(srcDiscover)
        /\ \A self \in SrcAnalysers : SF_vars(srcAnalyser(self))
        /\ SF_vars(guiAgent)

Termination == <>(\A self \in ProcSet: pc[self] = "Done")

\* END TRANSLATION 

\* END TRANSLATION 

\* END TRANSLATION 
=============================================================================
\* Modification History
\* Last modified Sun May 07 20:54:40 CEST 2023 by aricci
\* Created Sun Mar 28 15:32:19 CEST 2021 by aricci
