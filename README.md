# Espace

A turn-based space-conquest game written in Java/Swing in 2002, in the Galcon / Konquest style. Up to 8 players share one computer and take turns (hotseat). They send fleets between planets to take over the map.

The code, comments and in-game text are in French.

## Running it

It still compiles and runs on a modern JDK (tested with JDK 17) without any code changes. Run these commands from the repository root:

```bash
javac -encoding ISO-8859-1 -d build $(find . -name '*.java')
java -cp build Initialisation
```

- `-encoding ISO-8859-1` is needed because the sources predate UTF-8 and contain French accents.
- Run from the repository root. The game loads planet names from `starname.txt` in the current directory.
- `java -cp build Initialisation --allowCheats` turns on cheat commands (see [Cheats](#cheats)).
- `Initialisation` is the real entry point. The other classes with a `main` (`Espace`, `FenetreChat`, `PanneauIP`, ...) are standalone test harnesses.
- The NetBeans `AbsoluteLayout` helper used by the setup screens is included as source under `org/`.

## Setup screen (`Initialisation`)

- **Config tab:** number of planets (at least 4 per player, 200 max), plus the count and refresh interval of the animated starfield.
- **One tab per player:** use **Joueur+ / Joueur-** to add or remove players (2 to 8). Each player picks a name, a colour and a race.
- **Randomize** rolls a new map, **Charge** loads a saved game, and **Ok** starts the game.

### Races

Every race has five stats that multiply how it performs in play:

| Race       | Attack | Defence | Production | Movement | Intelligence |
|------------|-------:|--------:|-----------:|---------:|-------------:|
| Federation | 1.20   | 0.60    | 1.00       | **4.00** | 1.25         |
| Vulcain    | 0.75   | 0.75    | **2.00**   | 1.00     | **2.00**     |
| Klingon    | **2.50** | 0.50  | 1.00       | 2.00     | 0.75         |
| The Borg   | 2.00   | 2.00    | 2.00       | 2.00     | **0.00**     |
| Batards    | 5.5 points split evenly, then tuned with the +/- buttons |||||

With the custom race (Batards), raising a stat takes 0.04 from each of the others. Lowering a stat gives the points back, spread across the others. No stat can go below 0.25.

### The map

- The galaxy is a 64×40 grid. Each cell is 10×10 px.
- Each player starts on a random home planet that produces 10 ships per turn.
- Other planets produce 6 to 21 ships per turn. Each also has a hidden "toughness" multiplier (0.75 to 1.25) and small hidden tech bonuses.
- Every planet no player owns belongs to **"certains pirates inconnus.."**, a neutral race with attack 0, defence 0.5 and production 0.5. Pirates never attack, but their planets slowly build up defenders.

## Playing a turn

### 1. Each player's phase

The top bar shows the current player's planets, fleets, ships (with production per turn in parentheses) and race stats. The bottom bar shows the planet under the mouse, the selected source and destination, the order box and the **Terminé** button.

- **Hover** over a planet to see its name, owner, ship count and production. Once a source is selected, hovering also shows how many turns a fleet would take to reach that planet. Pirate planets show no numbers, so you attack them blind.
- **Press** on one of your planets to make it the source. **Release** on another planet to make it the destination.
- **Type a number** in the order box and press Enter. The fleet leaves right away and the ships are removed from the source planet.
- **Right-click the source** before sending to make the order a **standing order**. The same number of ships is then sent from that planet to that destination at the start of every turn. The planet's name shows in red in the tooltip. Ctrl + middle-click the planet to cancel it. A standing order also ends if the planet is captured.
- **Shift-drag a rectangle**, pick a destination, then type `envoie 0.5` (or just `envoie` for 100%). Every planet you own inside the rectangle sends that fraction of its ships.
- **`concentrate`** sends every ship from all your planets to the selected destination when you end your phase.
- **`save`** asks for a file name and saves the game as a gzip file (see `Saves/`).
- Click **Terminé** to end your phase. Play passes to the next player who is still alive.

### 2. Resolution (`Espace.avanceTour`)

Once every player has played:

1. **Production:** each planet adds `production × owner's production stat` ships.
2. **Movement:** each fleet's countdown drops by one. Travel time is
   `1 + 0.39 × distance ÷ owner's movement stat` turns.
3. **Arrivals:**
   - A fleet reaching a planet its owner holds reinforces it.
   - Otherwise there is a fight:
     - Attack **A = ships × attacker's attack stat**
     - Defence **D = ships × planet toughness × defender's defence stat**
     - If **A > D**, the attacker takes the planet and keeps `(A − 0.66·D) ÷ attack` ships.
     - Otherwise the defender keeps `(D − 0.66·A) ÷ (toughness × defence)` ships.
   - A **Résultats** popup lists every arrival and battle.
4. **Tech from pirates:** capturing a pirate planet adds that planet's hidden bonuses to your race's attack, defence, production and movement stats, for good. The gain is scaled by intelligence and shrinks as the stat grows, so the Borg (intelligence 0) never learn anything. Capturing a planet from another player gives no tech.
5. **Elimination:** a player with no planets and no fleets in flight gets a "Looser !" popup and is out. When only one player is left, a "Winner !" popup names them.
6. **Standing orders** are sent, and the first player's next phase starts.

### Strategy

The game is about trading speed, economy and firepower off against each other:

- Federation reaches targets well before anyone else.
- Vulcains out-produce everyone and learn the most from captured planets.
- Klingons hit hard but defend badly.
- Pirate planets get tougher every turn, so there is steady pressure to expand early.

## Cheats

When started with `--allowCheats`, the order box also accepts these commands. They apply to the selected destination planet or to the current player:

| Command                 | Effect                                              |
|-------------------------|-----------------------------------------------------|
| `asgnProprietaire`      | Current player takes the selected planet            |
| `asgnProd N`            | Sets the selected planet's production to N          |
| `asgnNb N`              | Sets the selected planet's ship count to N          |
| `addFlotte N T`         | Spawns a fleet of N ships arriving at the selected planet in T turns |
| `add <mask> <value>`    | Adds `value` to race stats chosen by bitmask: 1 = attack, 2 = defence, 4 = production, 8 = movement, 16 = intelligence |
| `avance` / `recule`     | Switches to the next or previous player             |

## Known quirks

- `concentrate 0.5` does not work because the percentage is parsed as an integer. Only `concentrate` with no argument (100%) works.
- In `Race.diminue`, the intelligence branch adds `mDeplacement` where it should add `mIntelligence`, so tuning a custom race's intelligence gives the wrong numbers.
- Enemy players' ship counts show on hover. Only pirate planets are hidden.

## Repository contents

| Path                          | What it is                                        |
|-------------------------------|---------------------------------------------------|
| `Initialisation.java`         | Setup screen and entry point                      |
| `Espace.java`                 | Map, mouse input, turn resolution, save/load      |
| `Planete.java`                | Planets, production and combat                    |
| `Flotte.java`, `Commande.java`, `GestionnaireCommande.java` | Fleets in flight and standing orders |
| `Proprietaire.java`, `GestionnaireProprietaire.java`, `Race.java` | Players, turn order and races |
| `Donnees.java`, `Statistiques.java` | Bottom (planet/order box) and top (player stats) panels |
| `ConfigurationEspace.java`, `ConfigurationJoueur.java` | Setup tabs                  |
| `Stars.java`, `Vecteur2D.java` | Animated starfield background                    |
| `FenetreChat.java`, `PanneauIP*.java`, `ZoneIP.java`, `Additionnable.java` | Beginnings of a network lobby/chat UI; not wired into the game |
| `*.form`                      | NetBeans GUI designer files                       |
| `EspaceV00.zip` – `EspaceV09.zip`, `Org.zip` | Old version snapshots                |
| `Saves/`                      | Saved games                                       |
| `starname.txt`                | Planet name list                                  |
