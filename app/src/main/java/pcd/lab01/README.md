# Lab01

## Programmazione thread tradizionale

- La classe `Thread` identifica le istanze degli oggetti attivi
  - facciamo una distinzione fra ogetti attivi e passivi: buona disciplina è tenerala separata
    - componenti attivi: thread che rappresentano concettualmente processi che vengono progettati perché incapsuli un certo compito interagendo con altri...
    - componenti passivi: gli oggetti "tradizionali" che hanno un'interfaccia e su cui applichiamo le regole "classiche"

`step0`
- Un programma sequenziale ha sempre almeno il Main Thread.
  - In realtà non è così: il garbage collector, le GUI...
- il SO (non real-time) su cui è mandato in esecuzione non è deterministico lo scheduling
- incapsulamento del comportamento!
  - ha il suo stato
  - e il comportamento non è dato da un'interfaccia perché non è progettato per fornire un servizio
  - ma è lui che esegue!!! => nessun metodo pubblico, fuorché la `run`!
    - metodo protetto: per estendibilità. Estendere un componente attivo non è come per i componenti passivi
- `System.out` è un oggetto condiviso
  - i flussi di esecuzione si incrociano
- come specifico 

- non è possibile terminare l'esecuzione dell'oggetto
  - ha senso nel campo più alto dell'incapsulamento: tutto ciò che condiziona e determina il comportamento è nel codice e del flusso di controllo del componente attivo

- Java19: [`Thread`](https://docs.oracle.com/en/java/javase/19/docs/api/java.base/java/lang/Thread.html)