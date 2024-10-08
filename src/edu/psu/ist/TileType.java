package edu.psu.ist;

// singletons to represent the mined and covered square types
/**
 * Represents the tile type with three types:
 * <ul>
 *     <li>{@link Uncovered}</li> denotes a selected tile showing the number of
 *      adjacent mines (including diagonal)
 *     <li>{@link Safe} denotes the mineless tile (a singleton)</li>
 *     <li>{@link Mine} denotes the mined/trapped tile (a singleton)</li>
 * </ul>
 */
public sealed interface TileType {
    record Uncovered(int count)         implements TileType {}
    enum Mine                           implements TileType {MineInst}
    enum Safe                           implements TileType {SafeInst}

    default boolean isMine() {
        return switch (this) {
            case Mine _ -> true;
            default     -> false;
        };
    }

    default String cellAsString() {
        return switch (this) {
            case Mine.MineInst      -> "*";
            case Safe.SafeInst      -> "_";
            case Uncovered(var c)   -> c + "";
        };
    }
}
