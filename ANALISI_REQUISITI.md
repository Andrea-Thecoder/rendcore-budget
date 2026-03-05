# rendcore-backend

## Analisi della richiesta

Di seguito è riportata la descrizione della richiesta formulata dall’azienda e l’analisi preliminare svolta al fine di
soddisfare le specifiche progettuali assegnate.

### Richiesta

L’azienda X ha richiesto lo sviluppo di un applicativo backend finalizzato alla gestione del budget aziendale.
Il sistema dovrà consentire la registrazione e la gestione delle entrate, delle uscite strutturate e delle richieste di
spesa aggiuntiva.
Per ulteriori dettagli, si rimanda al paragrafo [Rendcore – Applicativo Budget](#rendcore---applicativo-budget).

Inoltre, l’azienda ha richiesto la realizzazione di un secondo applicativo destinato alla gestione dei dipendenti.
I dettagli relativi a tale componente sono illustrati nel
paragrafo [Rendcore – Applicativo Dipendenti](#rendcore---applicativo-dipendenti).

Infine l'azienda richiede un livello di sicurezza tramite il login, con visibilità in base ai ruoli, e integrazione di
sicurezza avanzata.

#### Dettagli della richiesta

##### Rendcore - Applicativo Budget

L’applicativo **Rendcore – Budget** ha l’obiettivo di consentire la registrazione, la gestione e il monitoraggio delle
voci di bilancio aziendale, sia in entrata che in uscita.

###### Funzionalità principali

- Inserimento di nuove voci di budget, specificando:
    - la tipologia (**entrata** o **uscita**);
    - la natura (**strutturale** o **occasionale**);
    - l’importo e la descrizione della voce.
- Modifica delle voci non ancora approvate.
- Eliminazione delle voci non approvate.
- Visualizzazione delle voci registrate con possibilità di applicare **filtri dinamici** (per tipologia, stato, periodo,
  importo, ecc.).

###### Gestione degli stati e tipologie

Il sistema dovrà gestire una serie di **liste di stato** (*tipologiche*) predefinite, utilizzate per classificare e
monitorare l’avanzamento di ciascuna voce di budget.  
A titolo esemplificativo, gli stati potranno includere:

- Entrata / Uscita
- Programmato
- Strutturale
- Approvato / In attesa di approvazione / Rifiutato

Tali tipologie saranno configurabili e potranno essere aggiornate in base alle esigenze aziendali.

##### Rendcore - Applicativo Dipendenti
L’applicativo **Rendcore – Dipendenti** ha l’obiettivo di consentire la gestione completa del personale aziendale,
garantendo la corretta registrazione, modifica e consultazione dei dati anagrafici e contrattuali di ciascun dipendente.

###### Funzionalità principali

- Inserimento di nuovi dipendenti nel sistema, con registrazione delle informazioni personali e aziendali.
- Modifica dei dati di posizione interna e contrattuali già presenti.
- Archiviazione dei record relativi a dipendenti non più attivi.
- -Eliminazione dei record dei dipendenti non più attivi in conformità alle regole della GDPR.
- Visualizzazione dell’elenco completo del personale, con possibilità di applicare **filtri di ricerca** (per ruolo,
  reparto, livello, tipo di contratto, stato occupazionale, ecc.).
- Collegamento con l’applicativo **Rendcore – Budget** per l’associazione delle richieste di spesa ai relativi richiedenti
  (dipendenti o responsabili).

###### Struttura dei dati gestiti

Per ciascun dipendente il sistema dovrà memorizzare:
- **Anagrafica personale:** nome, cognome, codice fiscale, recapiti, data di nascita.
- **Anagrafica aziendale:** ruolo, livello interno, tipologia di contratto, RAL (Retribuzione Annua Lorda), reparto di appartenenza e stato (attivo/non attivo).
- **Relazioni funzionali:** eventuali collegamenti con altri moduli (es. richieste di spesa nel modulo Budget).

###### Requisiti aggiuntivi

Il sistema dovrà garantire:
- la **riservatezza dei dati** attraverso opportune politiche di accesso basate sui ruoli;
- la **tracciabilità delle modifiche** ai dati dei dipendenti;
- la possibilità di integrazione con moduli futuri relativi alla gestione delle presenze o della formazione.


#### Analisi Tecnica

#### Architettura generale
Il sistema richiesto **rendcore-backend** sarà sviluppato come applicativo **RESTful** basato su architettura **client-server**

L’applicativo sarà composto da due moduli principali:
- **Modulo Budget**
- **Modulo Dipendenti**

Lo Stack tecnologico per entrambi i moduli è il seguente:
- Java 21
- Quarkus 3.29
- PostgreSQL 17
- Docker

#### Struttura dei moduli

- **Modulo Budget**  
  Gestisce tutte le voci di bilancio (entrate, uscite, richieste di spesa).  
  Interagisce con il database per creare, modificare, eliminare e filtrare le transazioni.

- **Modulo Dipendenti**  
  Gestisce i dati anagrafici e contrattuali dei dipendenti.  
  Fornisce le informazioni necessarie per associare una richiesta di spesa al dipendente richiedente.

Entrambi i moduli saranno esposti tramite API REST e utilizzeranno un livello comune di autenticazione e validazione.

#### Struttura del database
Vedi documento apposito (WIP).

### Sicurezza

Il sistema implementerà un **meccanismo di autenticazione e autorizzazione basato su JWT (JSON Web Token)**.  
Si valuta l'utilizzo di Keycloak per un livello maggiore di sicurezza.
L’accesso alle API sarà regolato da **ruoli utente** (es. Amministratore, Responsabile, Dipendente), ognuno con diversi livelli di permessi.

- Tutte le richieste API richiederanno un token valido.
- Gli utenti potranno accedere solo alle funzioni consentite dal proprio ruolo.
- I dati sensibili saranno cifrati a livello di database dove necessario.

## Vincoli e dipendenze

Il **modulo Budget** dipende dal **modulo Dipendenti** per il recupero delle informazioni relative al personale aziendale necessario all’associazione delle voci di spesa ai rispettivi richiedenti.


