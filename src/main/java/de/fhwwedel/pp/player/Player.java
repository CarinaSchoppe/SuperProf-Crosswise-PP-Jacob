/*
 * Copyright Notice for Crosswise-PP
 * Copyright (c) at Crosswise-Jacob 2022
 * File created on 7/27/22, 11:22 AM by Carina The Latest changes made by Carina on 7/27/22, 11:21 AM All contents of "Player" are protected by copyright. The copyright law, unless expressly indicated otherwise, is
 * at Crosswise-Jacob. All rights reserved
 * Any type of duplication, distribution, rental, sale, award,
 * Public accessibility or other use
 * requires the express written consent of Crosswise-Jacob.
 */

package de.fhwwedel.pp.player;

import de.fhwwedel.pp.game.Game;
import de.fhwwedel.pp.gui.GameWindow;
import de.fhwwedel.pp.util.exceptions.NoTokenException;
import de.fhwwedel.pp.util.game.Position;
import de.fhwwedel.pp.util.game.Team;
import de.fhwwedel.pp.util.game.Token;
import de.fhwwedel.pp.util.game.TokenType;
import de.fhwwedel.pp.util.special.Action;
import de.fhwwedel.pp.util.special.Constants;
import de.fhwwedel.pp.util.special.GameLogger;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class Player {

    private final ArrayList<Token> tokens = new ArrayList<>();
    private final int playerID;
    private final boolean isActive;
    private final String name;
    private int points = 0;
    private Team team;

    public Player(int playerID, boolean isActive, String name) {
        this.playerID = playerID;
        this.isActive = isActive;
        this.name = name;
    }

    public void create() {
        this.team = Team.addPlayerToTeam(this);
    }

    //TODO: after turn: call Game#nextPlayer()

    public boolean normalTokenTurn(final Token token, final Position position) {
        if (hasToken(token))
            return false;
        var field = Game.getGame().getPlayingField().getCorrespondingPlayingField(position);
        if (field.getToken().getTokenType() != TokenType.NONE)
            return false;
        tokens.remove(getCorrespondingToken(token));
        field.setToken(token);
        GameLogger.logMove(this, token, position, Action.PLACE);
        //TODO: Update GUI
        return true;
    }

    private boolean hasToken(Token token) {
        for (var t : tokens) {
            if (t.equals(token))
                return false;
        }
        return true;
    }

    public boolean replacerTokenTurn(final Token token, final Position fieldTokenPosition,
            final Position handTokenPosition) {
        if (hasToken(token))
            return false;
        if (token.getTokenType() != TokenType.REPLACER)
            return false;
        if (hasToken(getCorrespondingToken(handTokenPosition)))
            return false;

        var field =
                Game.getGame().getPlayingField().getCorrespondingPlayingField(fieldTokenPosition);
        if (field.getToken().getTokenType() == TokenType.NONE)
            return false;
        var fieldToken = getCorrespondingToken(token);
        var handToken = getCorrespondingToken(handTokenPosition);
        tokens.remove(fieldToken);
        tokens.remove(handToken);
        field.setToken(handToken);
        if (GameWindow.getGameWindow() != null)
            GameWindow.getGameWindow().replacerAmountText();
        tokens.add(field.getToken());
        assert fieldToken != null;
        GameLogger.logMove(this, fieldToken, field, Action.REMOVE);
        assert handToken != null;
        GameLogger.logMove(this, handToken, field, Action.PLACE);
        return true;
    }

    private Token getCorrespondingToken(Position position) {
        if (!position.isHand())
            return null;
        return tokens.get(position.getHandPosition());
    }

    public boolean removerTokenTurn(final Token token, final Position position) {
        if (token.getTokenType() != TokenType.REMOVER)
            return false;

        if (hasToken(token))
            return false;

        var field = Game.getGame().getPlayingField().getCorrespondingPlayingField(position);
        if (field.getToken().getTokenType() == TokenType.NONE)
            return false;

        tokens.remove(getCorrespondingToken(token));
        tokens.add(field.getToken());
        field.setToken(new Token(TokenType.NONE));
        field.getToken().setPosition(field);
        if (GameWindow.getGameWindow() != null)
            GameWindow.getGameWindow().removerAmountText();
        GameLogger.logMove(this, token, field, Action.REMOVE);

        return true;

        //TODO: Update GUI for added Token
    }

    public boolean moverTokenTurn(final Token token, Position start, Position end) {
        if (token.getTokenType() != TokenType.MOVER)
            return false;
        if (hasToken(token))
            return false;
        var fieldStart = Game.getGame().getPlayingField().getCorrespondingPlayingField(start);
        var fieldEnd = Game.getGame().getPlayingField().getCorrespondingPlayingField(end);

        if (fieldStart.getToken().getTokenType() == TokenType.NONE)
            return false;
        if (fieldEnd.getToken().getTokenType() != TokenType.NONE)
            return false;

        tokens.remove(getCorrespondingToken(token));

        fieldEnd.setToken(fieldStart.getToken());
        fieldStart.setToken(new Token(TokenType.NONE));

        if (GameWindow.getGameWindow() != null)
            GameWindow.getGameWindow().moverAmountText();
        GameLogger.logMove(this, token, fieldStart, Action.REMOVE);
        GameLogger.logMove(this, token, fieldEnd, Action.PLACE);

        return true;
    }

    public boolean swapperTokenTurn(final Token token, final Position first,
            final Position second) {
        if (token.getTokenType() != TokenType.SWAPPER)
            return false;
        if (hasToken(token))
            return false;
        var fieldFirst = Game.getGame().getPlayingField().getCorrespondingPlayingField(first);
        var fieldSecond = Game.getGame().getPlayingField().getCorrespondingPlayingField(second);
        if (fieldFirst.getToken().getTokenType() == TokenType.NONE)
            return false;
        if (fieldSecond.getToken().getTokenType() == TokenType.NONE)
            return false;
        tokens.remove(getCorrespondingToken(token));
        var temp = fieldFirst.getToken();
        fieldFirst.setToken(fieldSecond.getToken());
        fieldSecond.setToken(temp);
        if (GameWindow.getGameWindow() != null)
            GameWindow.getGameWindow().swapperAmountText();
        GameLogger.logMove(this, fieldSecond.getToken(), fieldFirst, Action.PLACE);
        GameLogger.logMove(this, fieldFirst.getToken(), fieldSecond, Action.PLACE);
        //TODO: Update GUI
        return true;
    }

    private Token getCorrespondingToken(Token token) {
        for (var t : tokens) {
            if (t.equals(token))
                return t;
        }
        return null;
    }

    public void drawToken() throws NoTokenException {
        if (Game.getGame().getTokenDrawPile().isEmpty())
            throw new NoTokenException("No more tokens left in the Pile!");
        if (tokens.size() >= Constants.HAND_SIZE)
            return;
        var token = Game.getGame().getTokenDrawPile()
                .get(new Random().nextInt(Game.getGame().getTokenDrawPile().size()));
        Game.getGame().getTokenDrawPile().remove(token);
        tokens.add(token);
        GameLogger.logDraw(this, token);

        //TODO: Add token to Players GUI
    }

    public int tokenAmountInHand(TokenType token) {
        //the amount of tokens with the same TokenType in hand
        int amount = 0;
        for (var t : tokens) {
            if (t.getTokenType() == token)
                amount++;
        }
        return amount;
    }

    public HashSet<Integer> getHandSymbolTokenPositions() {
        Token[] handCopy = this.getTokens().toArray(new Token[0]);
        HashSet<Integer> returnSet = new HashSet<>();
        for (int i = 0; i < handCopy.length; i++) {
            if (handCopy[i].getTokenType().getValue() <= Constants.UNIQUE_SYMBOL_TOKENS) {
                returnSet.add(i);
            }
        }
        return returnSet;
    }

    public TokenType[] convertHandToTokenTypeArray() {
        TokenType[] array = new TokenType[Constants.HAND_SIZE];
        for (var index = 0; index < tokens.size(); index++) {
            array[index] = tokens.get(index).getTokenType();

        }
        return array;
    }

    public ArrayList<Token> getTokens() {
        return tokens;
    }

    public int getPlayerID() {
        return playerID;
    }

    public boolean isActive() {
        return isActive;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public String getName() {
        return name;
    }

    public String handRepresentation() {
        StringBuilder builder = new StringBuilder();
        builder.append("[");
        for (var t : tokens) {
            builder.append(t.getTokenType().getValue());
            builder.append(", ");
        }
        if (builder.length() > 2) {
            builder.deleteCharAt(builder.length() - 1);
            builder.deleteCharAt(builder.length() - 1);
        }
        builder.append("]");
        return builder.toString();
    }

    public Team getTeam() {
        return this.team;
    }
}
