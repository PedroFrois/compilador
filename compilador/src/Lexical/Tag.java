package Lexical;

public enum Tag {
    // special tokens
    INVALID_TOKEN,
    UNEXPECTED_EOF,
    END_OF_FILE,

    // symbols
    SEMICOLON,//
    COLON,//
    OPEN_PAR,//
    CLOSE_PAR,//
    // keywords
    START,
    EXIT,

    PRINT,
    SCAN,
    
    IF,
    THEN,
    ELSE,
    END,
    DO,
    WHILE,
    //types
    INT_T,
    FLOAT_T,
    STRING_T,

    // operators
    ASSIGN,
    PLUS,
    MINUS,
    MULT,
    DIV,
    //comparation
    EQUAL,
    GREATER,
    GREATER_EQUAL,
    LESS,
    LESS_EQUAL,
    DIFF,
    //logical
    NOT,
    OR,
    AND,
    // others
    IDENTIFIER,
    //constants
    INT_C,
    FLOAT_C,
    STRING_C
};
