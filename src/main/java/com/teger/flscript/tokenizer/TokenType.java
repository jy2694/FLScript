package com.teger.flscript.tokenizer;

public enum TokenType {
    //variable
    identifier(0x00, null),
    //literal
    string(0x10, null),
    number(0x11, null),
    _true(0x12, "true"),
    _false(0x12, "false"),
    //Operator
    dot(0x20, "."),

    pow(0x21, "**"),
    asterisk(0x22, "*"),
    divide(0x22, "/"),
    quotient(0x22, "//"),
    mod(0x22, "%"),
    plus(0x23, "+"),
    minus(0x23, "-"),

    equals(0x24, "=="),
    greater(0x24, ">"),
    less(0x24, "<"),

    not(0x26, "!"),
    xor(0x26, "^"),
    and(0x26, "&"),
    or(0x26, "|"),

    assign(0x27, "="),
    //Default Function
    print(0x30, "print"),
    println(0x3A, "println"),
    string_casting(0x31, "string"),
    number_casting(0x32, "number"),
    sin(0x33, "sin"),
    cos(0x34, "cos"),
    tan(0x35, "tan"),
    to_radian(0x36, "radian"),
    to_degree(0x37, "degree"),
    spawn(0x38, "spawn"),
    bool_casting(0x39, "bool"),
    //control statement
    _if(0x50, "if"),
    _while(0x53, "while"),
    //brackets
    lbracket(0x60, "("),
    rbracket(0x60, ")"),
    lbrace(0x61, "{"),
    rbrace(0x61, "}"),
    none(0xFF, null);


    private int code;
    private String str;

    TokenType(int code, String str){
        this.code = code;
        this.str = str;
    }

    public int getCode() {
        return code;
    }

    public String getString() {
        return str;
    }

    public static TokenType getTokenTypeByStrings(String str){
        for(TokenType type : values()) {
            if(type.getString() != null && type.getString().equals(str))
                return type;
        }
        return TokenType.identifier;
    }
}
