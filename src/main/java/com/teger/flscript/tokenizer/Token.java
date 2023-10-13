package com.teger.flscript.tokenizer;

import java.util.ArrayList;
import java.util.List;

public class Token extends TokenBuilder {
    private TokenType type;
    private Object name;

    public Token(TokenBuilder builder) {
        this.type = builder.getType();
        this.name = builder.getName();
    }

    public static TokenBuilder builder() {
        return new TokenBuilder();
    }

    public static Token[] parse(String exp) {
        List<Token> tokens = new ArrayList<>();
        exp = exp.trim();

        StringBuilder tmp = new StringBuilder();
        TokenType now = TokenType.none;
        char starter = '\0';

        for(int i = 0; i < exp.length(); i ++) {
            char c = exp.charAt(i);
            if(isCutCharacter(c)) {
                //string 처리
                if(c == '\"' || c == '\'') {
                    if(now != TokenType.string) {
                        now = TokenType.string;
                        starter = c;
                    } else if(c == starter) {
                        now = TokenType.none;
                        tokens.add(Token.builder()
                                .type(TokenType.string)
                                .name(tmp.toString())
                                .build());
                        tmp = new StringBuilder();
                    }
                    continue;
                }
                if(now == TokenType.string) {
                    tmp.append(c);
                    continue;
                }

                //다른 예약어 및 연산자 처리
                if(c == '*'){
                    if(i < exp.length()-1 && exp.charAt(i+1) == '*') {
                        i ++;
                        tokens.add(Token.builder()
                                .type(TokenType.pow)
                                .build());
                    } else {
                        tokens.add(Token.builder()
                                .type(TokenType.asterisk)
                                .build());
                    }
                } else if(c == '/') {
                    if(i < exp.length()-1 && exp.charAt(i+1) == '/') {
                        i ++;
                        tokens.add(Token.builder()
                                .type(TokenType.quotient)
                                .build());
                    } else {
                        tokens.add(Token.builder()
                                .type(TokenType.divide)
                                .build());
                    }
                } else if(c == '=') {
                    if(i < exp.length()-1 && exp.charAt(i+1) == '=') {
                        i ++;
                        tokens.add(Token.builder()
                                .type(TokenType.equals)
                                .build());
                    } else {
                        tokens.add(Token.builder()
                                .type(TokenType.assign)
                                .build());
                    }
                } else {
                    if(c != ' ') {
                        if(c == '.' && now == TokenType.number) {
                            tmp.append(c);
                            continue;
                        }
                        Token token = Token.builder()
                                .type(TokenType.getTokenTypeByStrings(Character.toString(c)))
                                .name(Character.toString(c))
                                .build();
                        tokens.add(token);
                    }
                }
                if(!tmp.isEmpty()) {
                    if(now == TokenType.number) {
                        Token token = Token.builder()
                                .type(TokenType.number)
                                .name(tmp.toString())
                                .build();
                        if(c != ' ') tokens.add(tokens.size()-1, token);
                        else tokens.add(token);
                        tmp = new StringBuilder();
                        now = TokenType.none;
                    } else {
                        Token token = Token.builder()
                                .type(TokenType.getTokenTypeByStrings(tmp.toString()))
                                .name(tmp.toString())
                                .build();
                        if(c != ' ') tokens.add(tokens.size()-1, token);
                        else tokens.add(token);
                        tmp = new StringBuilder();
                        now = TokenType.none;
                    }
                }
                if(c == ' ') {
                    now = TokenType.none;
                }
            } else {
                tmp.append(c);
                if(Character.toString(c).matches("[0-9.]") && now == TokenType.none && tmp.toString().length() == 1) {
                    now = TokenType.number;
                }
            }
        }
        if(!tmp.isEmpty()) {
            Token token = Token.builder()
                    .type(now == TokenType.number ? now : TokenType.getTokenTypeByStrings(tmp.toString()))
                    .name(tmp.toString())
                    .build();
            tokens.add(token);
            tmp = new StringBuilder();
        }
        return tokens.toArray(Token[]::new);
    }

    @Override
    public String toString() {
        return "<" + type.toString() + (name != null ? " :: " + (type == TokenType.number ? String.format("%.4f", Double.parseDouble(name.toString())) : name) : "") + ">";
    }

    public TokenType getType() {
        return type;
    }

    public Object getName() {
        return name;
    }

    private static boolean isCutCharacter(char c) {
        return switch (c) {
            case '\"', '\'', '(', ')', '{', '}', '[', ']', '+', '-', '*', '/', '%', '^', '!', '=', '&', '|', '.', ' ' -> true;
            default -> false;
        };
    }

}
