package com.teger.flscript.tokenizer;

public class TokenBuilder {
    private TokenType type;
    private Object name;

    public TokenBuilder type(TokenType type) {
        this.type = type;
        return this;
    }

    public TokenBuilder name(String name) {
        this.name = name;
        return this;
    }

    public TokenType getType() {
        return type;
    }

    public Object getName() {
        return name;
    }

    public Token build() {
        return new Token(this);
    }

}
