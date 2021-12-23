package com.android.androidproject;

public class GameModel {
    private final String game_name;
    private final String game_id;
    private final String game_pic;

    public GameModel(String game_name, String game_pic, String game_id) {
        this.game_name = game_name;
        this.game_pic = game_pic;
        this.game_id= game_id;
    }

    public String getGame_name() {
        return game_name;
    }

    public String getGame_pic() {
        return game_pic;
    }

    public String getGame_id() {
        return game_id;
    }
}
