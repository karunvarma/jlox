package com.kv;

import java.util.ArrayList;
import java.util.List;
import com.kv.TokenType.*;
import static com.kv.TokenType.*;

/**
 * Scanner class is responsible for generating the tokens
 */
public class Scanner {

    private final String source;
    private final List<Token> tokenList = new ArrayList<>();
    private int current = 0;
    private int line = 1;
    private int start = 0;

    public Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens(){
        while(!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokenList.add(null);
        return tokenList;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void scanToken(){
        char c = advance();
        switch (c){
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;
            case '/':
                if(match('/')){
                    /**
                     * as of now we are currently supporting the single line comment
                     * we need to consume 
                     */
                    while(peek() != '\n' && !isAtEnd()) advance();
                }
                else {
                    addToken(SLASH);
                }
            default:
                Lox.error(line,"Unexpected Character");
                break;
        }
    }


    private boolean match(char expected) {

        // it is currently looking for next character
        // So we need to check whether next char exist or not
        if(isAtEnd()) return false;
        if(source.charAt(current) != expected) return false;

        // we found the expected char,so advance to the next character
        current++;
        return true;
    }


    private void addToken(TokenType tokenType) {
        addToken(tokenType,null);
    }

    private void addToken(TokenType tokenType, Object literal) {

        // [start....current)
        String text = source.substring(start,current);
        tokenList.add(new Token(tokenType,line,literal,text));

    }


    private char advance() {
        return source.charAt(current++);
    }
}
