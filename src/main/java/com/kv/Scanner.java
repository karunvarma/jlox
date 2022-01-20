package com.kv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kv.TokenType.*;
import static com.kv.TokenType.*;

/**
 * Scanner class is responsible for generating the tokens
 */
public class Scanner {

    private static final Map<String,TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("fun",    FUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);
    }

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

        // TODO need to add end of the line code
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
                     * we need to consume until we see a new line char
                     * or till we reach the end of the string
                     */
                    while(peek() != '\n' && !isAtEnd()) advance();
                }
                else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
               // ignore the white space characters
               break;
            case '\n':
               line++;
               break;
            case '"':
                stringLiteral();
                break;
            default:
                if(isDigit(c)){
                    numberLiteral();
                }
                /**
                 * any lexeme that starting with a letter or underscore will
                 * be treated as a identifier
                 */
                else if(isAlpha(c)){
                    identifier();
                }
                else Lox.error(line,"Unexpected Character");
                break;
        }
    }

    private void identifier() {
        while(isAlphaNumeric(peek())){
            advance();
        }

        String text = source.substring(start,current);
        TokenType type = keywords.get(text);
        if(type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlpha(char c){
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c == '_'));
    }

    private boolean isAlphaNumeric(char c){
        return isDigit(c) || isAlpha(c);
    }

    private void numberLiteral() {

        // consume all the digits
        while(isDigit(peek())) advance();

        // handle the fraction part
        if(peek() == '.'){

            // consume the '.' char
            advance();

            if(isDigit(peek())){

                // consume the entire fraction part
                while(isDigit(peek())) advance();
            }
            else {

                // TODO custom
                Lox.error(line,"trailing decimal point is not supported");
                return;
            }
            // handle error
        }

        // we consumed all the digits that are part of string literal
        addToken(NUMBER,Double.parseDouble(source.substring(start,current)));
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    /**
     * This function is responsible for consuming the string literal
     */
    private void stringLiteral() {

        // consume till we reach the end or till we find the '"' char
        while(peek() != '"' && isAtEnd() == false){

            // the string literal can be of more than two lines
            // so if we also need to handle it
            if(peek() == '\n') line++;
            advance();
        }

        // handle unterminated String by reaching the end of string
        if(isAtEnd()){
            Lox.error(line,"Unterminated String literal");
            return;
        }

        // consume the closing '"'
        advance();

        // get the string literal substring by stripping of the surrounding quotes
        String literal = source.substring(start + 1,current - 1);

        addToken(STRING,literal);
    }

    /**
     * peek() will the return character located at the current pointer
     * if all the characters are consumed then it returns a '\0'
     *
     * it does not advance to any next char
     * @return
     */
    private char peek() {
        // https://stackoverflow.com/questions/11294850/the-ascii-value-of-0-is-same-as-ascii-value-of-0
        /** when we try to look for the next char and if the string is Empty at
         *  that time we are returning null aka ('\0')
         */
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    /**
     * this function checks whether the pointer to current char is same
     * as the expected char
     * if yes : advance to  next char
     * @param expected char
     * @return return true or false based on equality
     */
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
