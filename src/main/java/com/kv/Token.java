package com.kv;

public class Token {
    final TokenType tokenType;
    final int line;
    final Object literal;
    final String lexeme;


    public Token(TokenType tokenType, int line, Object literal, String lexeme) {
        this.tokenType = tokenType;
        this.line = line;
        this.literal = literal;
        this.lexeme = lexeme;
    }

    public String toString(){
        return tokenType + " " + lexeme + " "+literal;
    }
}
