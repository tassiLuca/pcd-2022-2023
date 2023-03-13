# Lab02

- Una classe si dice _thread-safe_ se è stata pensata e concepita per funzionare correttamente in contesti multithreaded
  - ci permette di incapsulare la responsabilità della correttezza del codice se mandato in esecuzione su più thread e solleva la responsabilità del client che deve usarlo
- se stiamo parlando di oggetti che non hanno stato o lo stato è immutabile, allora possono essere condivisi da più thread e non ci dobbiamo preoccupare della thread-safety
  - nella programmazione funzionale non ci saranno problemi di corretteza
- l'attenzione si pone quando c'è uno stato condiviso
  - è sufficiente che l'oggetto esponga dei metodi pubblici che permettono la modifica
  - laddove non ci proteggiamo => race condition

Race conditions

2 famiglie:
- lost updates: due thread che incrementano lo stesso contatore
- check-and-act: errore molto frequente

- ogni oggeetto java ha un intrinsec lock
  - ```java
    synchronized(lock) {
       statement 
    }
    ```