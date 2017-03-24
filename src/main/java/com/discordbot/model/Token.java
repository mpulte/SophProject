package com.discordbot.model;

public class Token {

    private String token;
    private String name;

    public Token(String token, String name) {
        this.token = token;
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token && ((Token) obj).getToken().equals(token);
    }
}
