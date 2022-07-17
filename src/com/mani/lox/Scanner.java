package com.mani.lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mani.lox.Lox.error;
import static com.mani.lox.TokenType.*;

/*
    The core of the scanner is a loop. Starting at the first character of the source code,
    the scanner figures out what lexeme the character belongs to,
    and consumes it and any following characters that are part of that lexeme.
    When it reaches the end of that lexeme, it emits a token.
 */
public class Scanner {

    private final String source;
    private int start = 0;
    private int current = 0;
    private int line = 1;

    private final List<Token> tokens = new ArrayList<>();

    private static final Map<String,TokenType> keywords;

    static  {
        keywords = new HashMap<>();
        keywords.put("and",AND);
        keywords.put("class",CLASS);
        keywords.put("else",ELSE);
        keywords.put("false",FALSE);
        keywords.put("for",FOR);
        keywords.put("fun",FUN);
        keywords.put("if",IF);
        keywords.put("nil",NIL);
        keywords.put("or",OR);
        keywords.put("print",PRINT);
        keywords.put("return",RETURN);
        keywords.put("super",SUPER);
        keywords.put("this",THIS);
        keywords.put("true",TRUE);
        keywords.put("var",VAR);
        keywords.put("while",WHILE);
    }

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens(){
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(EOF,"",null,line));
        return tokens;
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }


    private void scanToken() {
        char c = advance();
        switch (c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken((RIGHT_BRACE)); break;
            case ',': addToken(COMMA);break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;
            // Process !=, ==, <=, >=
            case '!': addToken(match('=')?BANG_EQUAL:BANG); break;
            case '=': addToken(match('=')?EQUAL_EQUAL:EQUAL); break;
            case '<': addToken(match('=')?LESS_EQUAL:LESS); break;
            case '>': addToken(match('=')?GREATER_EQUAL:GREATER); break;
            case '/':
                if(match('/')) {
                    while(peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                break;
            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            case 'o':
                if(match('r')){
                    addToken(OR);
                }
                break;
            default:
                if(isDigit(c)) {
                    number();
                } else if (isAlpha(c)){
                    identifier();
                }
                else {
                    error(line, "Unexpected Character");
                }
                break;

        }
    }


    private void identifier() {
        while (isAlphaNumeric(peek())) advance();
        String text = source.substring(start,current);
        TokenType type = keywords.get(text);
        if(type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    private boolean isAlpha(char c) {
        return ( c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'A') ||
                c == '_';
    }

    public boolean isDigit(char c) {
        return c >='0' && c <='9';
    }

    public void number() {
        while (isDigit(peek())) advance();
        if(peek() == '.' && isDigit(peekNext())) {
            advance();
        }
        while (isDigit(peek())) advance();
        addToken(NUMBER,Double.parseDouble(source.substring(start,current)));
    }

    public char peekNext() {
        if(current + 1 >= source.length()) return '\0';
        return source.charAt(current+1);
    }

    public void string(){
        while(peek() != '"' && !isAtEnd()) {
            if(peek() == '\n') line++;
            advance();
        }
        if(isAtEnd()) {
            error(line,"unterminated string.");
            return;
        }
        advance();
        String value = source.substring(start+1,current-1);
        addToken(STRING,value);
    }
    private char peek() {
        if(isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // Logic to parse != <= >= ==
    private boolean match(char expected) {
        if(isAtEnd()) return false;
        if(source.charAt(current)!=expected) return false;
        current++;
        return true;
    }

    private void addToken(TokenType type) {
        addToken(type,null);
    }


    private void addToken(TokenType type, Object literal) {
        String text = source.substring(start,current);
        tokens.add(new Token(type,text,literal,line));
    }


    private char advance() {
        return source.charAt(current++);
    }
}
