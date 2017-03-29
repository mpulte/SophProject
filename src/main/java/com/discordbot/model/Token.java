package com.discordbot.model;

/**
 * A token containing the token provided by Discord and a name to recognize the token by.
 */
public class Token {

    private String token;
    private String name;

    /**
     * @param token The token provided by Discord.
     * @param name  A name to recognize the token by.
     */
    public Token(String token, String name) {
        this.token = token;
        this.name = name;
    }

    /**
     * Accesses the token provided by Discord.
     *
     * @return the token provided by Discord.
     */
    public String getToken() {
        return token;
    }

    /**
     * Accesses the name to recognize the token by.
     *
     * @return the name to recognize the token by
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if an {@link Object} is a Token with the same token {@link String} as this token.
     *
     * @param obj the {@link Object} to check.
     * @return <tt>true</tt> if the {@link Object} is a Token with the same token {@link String} as this Setting,
     * otherwise <tt>false</tt>.
     */
    @Override
    public boolean equals(Object obj) {
        return obj instanceof Token && ((Token) obj).token.equals(token);
    }

}
