# A custom made compiler for the below mentioned grammar
# Grammar
The syntactical definition is using the following conventions: 
Terminals (lexical elements, or tokens) are represented in single quotes 'likeThis'.
  - Non-terminals are represented in italics likeThis.
  - The empty phrase is represented by EPSILON.
  - EBNF-style repetition notation is represented using curly brackets {like this}. It represents zero or more
  occurrence of the sentential form enclosed in the brackets.
  - EBNF-style optionality notation is represented using square brackets [like this]. It represents zero or one
  occurrence of the sentential form enclosed in the brackets.
  - The non-terminal **<prog>** is the starting symbol of the grammar. 
  
  
        prog -> {classDecl} {funcDef} 'main' funcBody ';'
        classDecl -> 'class' 'id' [':' 'id' {',' 'id'}] '{' {varDecl} {funcDecl} '}' ';'
        funcDecl -> type 'id' '(' fParams ')' ';'
        funcHead -> type ['id' 'sr'] 'id' '(' fParams ')'
        funcDef -> funcHead funcBody ';'
        funcBody -> '{' {varDecl} {statement} '}'
        varDecl -> type 'id' {arraySize} ';'
        statement -> assignStat ';'
         | 'if' '(' expr ')' 'then' statBlock 'else' statBlock ';'
         | 'for' '(' type 'id' assignOp expr ';' relExpr ';' assignStat ')' statBlock ';'
         | 'read' '(' variable ')' ';'
         | 'write' '(' expr ')' ';'
         | 'return' '(' expr ')' ';'
        assignStat -> variable assignOp expr
        statBlock -> '{' {statement} '}' | statement | EPSILON
        expr -> arithExpr | relExpr
        relExpr -> arithExpr relOp arithExpr
        arithExpr -> arithExpr addOp term | term
        sign -> '+' | '-'
        term -> term multOp factor | factor
        factor -> variable
         | functionCall
         | 'intNum' | 'floatNum'
         | '(' arithExpr ')'
         | 'not' factor
         | sign factor
        variable -> {idnest} 'id' {indice}
        functionCall -> {idnest} 'id' '(' aParams ')'
        idnest -> 'id' {indice} '.'
         | 'id' '(' aParams ')' '.'
        indice -> '[' arithExpr ']'
        arraySize -> '[' 'intNum' ']'
        type -> 'integer' | 'float' | 'id'
        fParams -> type 'id' {arraySize} {fParamsTail} | EPSILON
        aParams -> expr {aParamsTail} | EPSILON
        fParamsTail -> ',' type 'id' {arraySize}
        aParamsTail -> ',' expr
        assignOp -> '='
        relOp -> 'eq' | 'neq' | 'lt' | 'gt' | 'leq' | 'geq'
        addOp -> '+' | '-' | 'or'
        multOp -> '*' | '/' | 'and' 
       
# Tokens 
Keywords-->        main | class |
                if | then | else | for | read | write | return |
                integer | float

Opreators-->         eq (==) | neq (<>) | lt (<) | gt (>) | leq (<=) | geq (>=)
                  + | - | * | /
                   not (!) | and (&&) | or (||)
                    =
                  sr (::)

Punctuation-->        | , | . | ; | [ | ] | { | } | ( | )
