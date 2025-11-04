grammar Aufgabe3;

program: statement+ EOF ;

statement: assignment NEWLINE |expr NEWLINE | ifRule | whileRule;

assignment: ID ':=' expr;

expr: comparison;

comparison: addition (VGLOP addition)?;

addition: multiplication (('+' | '-') multiplication)*;

multiplication: atom (('*' | '/') atom)*;

whileRule:
    'while' condition 'do' NEWLINE?
        statement+
    'end' NEWLINE?;

ifRule :
    'if' condition 'do' NEWLINE?
         statement+
    ('else' 'do' NEWLINE?
        statement+
    )?
    'end' NEWLINE?;

condition
    : expr VGLOP expr      #Compare;

atom: NUMBER | STRING | TRUE |FALSE | ID |'(' expr ')';

TRUE    :  'true' ;
FALSE   :  'false' ;
ID      :  [a-zA-Z_][a-zA-Z0-9_]* ;
NUMBER  :  [0-9]+ ;
VGLOP   :  '==' | '!=' | '>' | '<';
STRING  :  '"' (~[\n\r"])* '"' ;
COMMENT :  '#' ~[\n\r]* -> skip ;
WS      :  [ \t]+ -> skip;
NEWLINE : [\n\r]+;
