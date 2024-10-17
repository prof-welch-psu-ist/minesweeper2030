## A byzantine minesweeper2030 approach

> NOTE: not intended to be a serious solution 

It is mostly an (ill-advised) attempt to utilize some newer Java language 
features, including:
* record types
* sealed interfaces and records as well as design patterns like singletons 
to represent certain (duplicated) square types that make up the board.
  * [here is an excellent article summarizing sealed interfaces and records](https://blog.jetbrains.com/idea/2020/09/java-15-and-intellij-idea/)
* pattern matching via [switch expressions](https://docs.oracle.com/en/java/javase/17/language/switch-expressions-and-statements.html)

Tips if reading: focus on the tests and how they are structured and organized; don't 
read too much into the `SquareBoard` builder class (not exactly clearly written) or the 
`compute` method -- which makes extensive use functional list operations (like fold). 

### jdk23 things

Most of the data structures are modeled "algebraically" as a sealed interface
type consisting of some number of implementing types (records/constructors).

For example, `TileType`:
```java
public sealed interface TileType {
enum Mine                   implements TileType {MineInst}  
enum Hidden                 implements TileType {HiddenInst}
record Uncovered(int count) implements TileType {}
```

note that the two `enum` subtypes of `TileType` above are shorthand for the below approach
(which is more akin to the singleton approach discussed in lecture):
```java
public sealed interface TileType {
    final class Mine implements TileType { // marked "final" to preclude extension
        public static final Mine MineInst = new Mine();
        private Mine() {}
    } 
    final class Hidden implements TileType {
        public static final Hidden HiddenInst = new Hidden();
        private Hidden() {}
    } 
    record Uncovered(int count) implements TileType {}
```
The "clever" thing with the first (and, in my opinion, clearer) snippet is that 
enums are actually java's first class language mechanism for expressing 
singleton objects....

[Scala](https://www.scala-lang.org/) is an example of JVM-based language that is functional-first
(somewhat closely related in lineage to Java) that has "true" first class support for 
singleton types. Here's how the algebraic type for `TileType` would look in scala 3:
```scala 3
sealed trait TileType // traits are like interfaces in Java (applies to: `sealed` too)
object TileType:
  case object Mine extends TileType     // the "object" keyword means Mine is a singleton
  case object Hidden extends TileType
  case class Uncovered(count: Int) extends TileType // case class == java record types
```

Modeling the various types of tiles that comprise the board allows you to 
"pattern match". A feature just added to Java in Sept 2023, e.g.:

```java 
String renderTileAsString(TileType tile) {
  return switch (tile) {
    case Mine.MineInst      -> "*";
    case Hidden.HiddenInst  -> "_";
    case Uncovered(var c)   -> c + ""; // could also say: case Uncovered(int count) -> c + "";
  };
}
```
The last case is especially interesting as we're "deconstructing" the record type we used
to model the idea of an uncovered square and matching on the record instance's internal 
structure -- where `c` is the adjacent mine count for the uncovered square we match... 

If we didn't use a deconstruction pattern here, we'd have to do it like so:

```java 
String renderTileAsString2(TileType tile) {
  return switch (tile) {
    case Mine.MineInst      -> "*";
    case Hidden.HiddenInst  -> "_";
    case Uncovered u        -> u.count() + "";
  };
}
```

Can read more about this at the actual JDK proposal docs:

> https://openjdk.org/jeps/405

Fun fact: the ability to pattern match on arbitrary subtypes (not to mention deconstructing them as shown above)
has basically rendered the entire (longstanding) gang-of-four ["visitor pattern"](https://en.wikipedia.org/wiki/Visitor_pattern) 
nearly obsolete. 

So pattern matching is a cool example of how OOP design patterns can be rendered 
obsolete with the addition of new first-class language features. 

### Handling errors

This type of pattern matching is used throughout -- the pattern/switch match 
will generally just be the singular expression "implementing" a given method.

Some of the data structures used in here (for encapsulating either a success 
value or a failure encountered) are very weird (re: the `Result` type from the 
immutableadts pkg). Perhaps the most byzantine part here... 

### persistent collections

The immutable collections framework, [vavr](https://github.com/vavr-io/vavr) is
used here to provide drop-in (immutable) replacements for all the standard
(mutable) Java collections.

This library gives us collections with many of the 'standard' functional operators:
* `map`, `filter`, `foldLeft`, etc.

Mostly make use of `vavr`s copy-on-write `Vector` type, which we wrap in a `Row` ...
where n `Row`s make up a (NxN) `SquareBoard` object. And though this vector
type is slower with $O(log n)$ `get(i)` (access) calls than a standard mutable
`ArrayList` which is constant -- $O(1)$ ... this is good enough (4x4) as our boards are
small + `Vector` is fully immutable.

By the $log_2$ function, I guess we'd only start paying somewhat larger runtime
penalties if the board were to become 1000x1000 or something... then we'd need to
store 1000 entries in our inner vector `v`

$$log_2 ( 1000 ) = 9.9$$

so returning the value at index `i` via `v.get(i)` will only take 9.9
steps internally to complete.... the vector internally stores the data in a balanced
tree-like data structure and this 9.9 means we will only ever need to zig-zag
down 9 levels of that tree... even if we get to $log_2(10000)$ we're only looking at
13.3 moves down the tree. So: the $log_2$ function grows extremely slow... meaning
algorithms with $log$ -based runtimes are highly scalable (even as they grow to store
massive amounts of data).

We will get to trees soon. There are lots of great examples of tree structures amenable
to immutability; including many others that are more naturally mutable:
from both a performance and implementation-simplicity perspective (heaps come to mind here).
