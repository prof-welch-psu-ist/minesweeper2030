package edu.psu.ist;

// singletons to represent the mined and covered square types

/**
 * Represents the tile type with three possibilities:
 * <ul>
 *     <li>{@link Uncovered}</li> denotes a selected tile showing the number of
 *      adjacent mines (including diagonal)
 *     <li>{@link Hidden} denotes the unrevealed tile (a singleton)</li>
 *     <li>{@link Mine} denotes the mined/trapped tile (a singleton)</li>
 * </ul>
 */
public sealed interface TileType {
    enum Mine                   implements TileType {MineInst}
    enum Hidden                 implements TileType {HiddenInst}
    record Uncovered(int count) implements TileType {}

    static Mine mine() { return Mine.MineInst; }
    static Hidden hidden() { return Hidden.HiddenInst; }

    default boolean isMine() {
        return switch (this) {
            case Mine _ -> true;
            default     -> false;
        };
    }

    default String cellAsString() {
        return switch (this) {
            case Mine.MineInst      -> "*";
            case Hidden.HiddenInst  -> "_";
            case Uncovered(var c)   -> c + "";
        };
    }
}
