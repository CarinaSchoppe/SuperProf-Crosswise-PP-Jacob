package gui;

import java.util.Arrays;

/**
 * Game Data class which stores Data to be saved or loaded from/to a file
 *
 * @author Jacob Klövekorn
 */
public final class GameData {
    /**
     * Players as PlayerData objects
     */
    private final PlayerData[] players;
    /**
     * current player as int
     */
    private final Integer currPlayer;
    /**
     * game field as double array int representation
     */
    private final int[][] field;
    /**
     * used action tiles as int array represenatation
     */
    private final int[] usedActionTiles;

    /**
     * Constructor
     *
     * @param players         Array of players
     * @param currPlayer      current player
     * @param field           game field
     * @param usedActionTiles used action tiles
     */
    public GameData(PlayerData[] players, Integer currPlayer, int[][] field, int[] usedActionTiles) {
        this.players = players;
        this.currPlayer = currPlayer;
        this.field = field;
        this.usedActionTiles = usedActionTiles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GameData gameData)) return false;

        if (!currPlayer.equals(gameData.currPlayer)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(players, gameData.players)) return false;
        if (!Arrays.deepEquals(field, gameData.field)) return false;
        return Arrays.equals(usedActionTiles, gameData.usedActionTiles);
    }


    @Override
    public String toString() {
        return "GameData{" +
                "players=" + Arrays.toString(players) +
                ", currentPlayer=" + currPlayer +
                ", field=" + Arrays.toString(field) +
                ", usedActionTiles=" + Arrays.toString(usedActionTiles) +
                '}';
    }

    //------------------------------------------------Getter------------------------------------------------------------

    PlayerData[] getPlayers() {
        return players.clone();
    }

    int getCurrentPlayer() {
        return currPlayer;
    }

    int[][] getField() {
        return field.clone();
    }

    int[] getUsedActionTiles() {
        return usedActionTiles.clone();
    }
}
