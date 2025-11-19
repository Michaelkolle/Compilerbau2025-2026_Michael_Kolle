from xml.dom import InvalidCharacterErr

#Aufgabe 3
class Token():
    def __init__(self,typ,value):
        self.typ = typ
        self.value = value

TOKEN_LPAREN   = "LPAREN"
TOKEN_RPAREN   = "RPAREN"
TOKEN_INTEGER  = "INTEGER"
TOKEN_STRING   = "STRING"
TOKEN_BOOLEAN  = "BOOLEAN"
TOKEN_IDENT    = "IDENT"
TOKEN_EOF      = "EOF"

class Queue():
    def __init__(self):
        self.queue = []

    def enqueue(self,element):
        self.queue.append(element)

    def dequeue(self):
        if not self.queue:
            return None
        return self.queue.pop(0)
    def peek(self):
        if not self.queue:
            return None
        return self.queue[0]

    def size(self):
        return len(self.queue)

lexer_queue = Queue()

def consume():
    pass

def is_letter(argument):
    if argument.isalpha():
        return True
    return False

def lParent():
    lexer_queue.dequeue()
    return Token(TOKEN_LPAREN, "(")

def rParent():
    lexer_queue.dequeue()
    return Token(TOKEN_RPAREN, ")")

def next_token():

    while lexer_queue.peek() is not None:

        c = lexer_queue.peek()

        #Whitespace
        if c in [' ', '\t', '\n']:
            WS()
            continue

        #Comments
        if c == ';':
            lexer_queue.dequeue()
            if lexer_queue.peek() == ';':
                comment()
                continue
            else:
                raise InvalidCharacterErr("Single ; is not allowed")

        #Klammern
        if c == '(':
            return  lParent()

        if c == ')':
           return rParent()

        #Strings
        if c == '"':
            return string()

        #Numbers
        if c.isdigit():
            return digit()

        #Identifiers
        if c.isalpha() or c in "+-*/<>=?":
            return ident()

        raise InvalidCharacterErr(f"Unexpected character: {c}")

    return Token(TOKEN_EOF, None)


def WS():
    while lexer_queue.peek() in [' ', '\t', '\n']:
        lexer_queue.dequeue()

def comment():
    while(lexer_queue.peek()) != '\n':
        lexer_queue.dequeue()
    lexer_queue.dequeue()  # das \n entfernen

def ident():
    buf = ""
    while lexer_queue.peek() is not None and \
            (lexer_queue.peek().isalnum() or lexer_queue.peek() in "+-*/<>=?"):
        buf += lexer_queue.dequeue()

    if buf == "true":
        return Token(TOKEN_BOOLEAN, True)
    if buf == "false":
        return Token(TOKEN_BOOLEAN, False)

    return Token(TOKEN_IDENT, buf)

def string():
    buf = ""
    lexer_queue.dequeue()
    while lexer_queue.peek() != '"':
        buf += lexer_queue.peek()
        lexer_queue.dequeue()

    lexer_queue.dequeue()
    return Token(TOKEN_STRING, buf)

def digit():
    buf = ""
    while lexer_queue.peek().isdigit():
        buf += str(lexer_queue.peek())
        lexer_queue.dequeue()


    return  Token(TOKEN_INTEGER, int(buf))


#Aufgabe 4

class Expr:
    pass

class Literal(Expr):
    def __init__(self, value):
        self.value = value

class Identifier(Expr):
    def __init__(self, name):
        self.name = name

class ListExpr(Expr):
    def __init__(self, operator, args):
        self.operator = operator
        self.args = args


class Parser:
    def __init__(self, tokens):
        self.tokens = tokens
        self.pos = 0

    def peek(self):
        if self.pos < len(self.tokens):
            return self.tokens[self.pos]
        return None

    def consume(self, expected_type=None):
        tok = self.peek()
        if tok is None:
            raise Exception("Parserfehler: nicht richtiges Ende der Eingabe")

        if expected_type and tok.typ != expected_type:
            raise Exception(f"Parserfehler: Erwartet {expected_type}, aber gefunden {tok.typ} ({tok.value})")

        self.pos += 1
        return tok

    def parse_program(self):
        expressions = []
        while self.peek().typ != TOKEN_EOF:
            expressions.append(self.parse_expr())
        return expressions

    def parse_expr(self):
        tok = self.peek()

        if tok.typ in [TOKEN_INTEGER, TOKEN_STRING, TOKEN_BOOLEAN]:
            self.consume()
            return Literal(tok.value)

        if tok.typ == TOKEN_IDENT:
            self.consume()
            return Identifier(tok.value)

        if tok.typ == TOKEN_LPAREN:
            return self.parse_list_expr()

        raise Exception(f"Parserfehler: Unerwartetes Token {tok.typ} ({tok.value})")

    def parse_list_expr(self):
        self.consume(TOKEN_LPAREN)

        op_tok = self.consume(TOKEN_IDENT)
        operator = op_tok.value

        args = []
        while self.peek().typ != TOKEN_RPAREN:
            args.append(self.parse_expr())

        self.consume(TOKEN_RPAREN)
        return ListExpr(operator, args)

def lex_string(input_str):
    #HIlfsfunktion
    lexer_queue.queue = list(input_str)  # jeden Char einzeln einfügen
    tokens = []

    while True:
        tok = next_token()
        tokens.append(tok)
        if tok.typ == TOKEN_EOF:
            break

    return tokens

def print_tokens(tokens):
    print("TOKENS:")
    for t in tokens:
        print(f"  {t.typ:10} -> {t.value}")
    print()

#Aufgabe 5

class ASTNode:
    pass


class Program(ASTNode):
    def __init__(self, expressions):
        self.expressions = expressions

    def __repr__(self):
        return f"Program({self.expressions})"


class Literal(ASTNode):
    def __init__(self, value):
        self.value = value

    def __repr__(self):
        return f"Literal({self.value})"


class Identifier(ASTNode):
    def __init__(self, name):
        self.name = name

    def __repr__(self):
        return f"Identifier({self.name})"


class Call(ASTNode):
    def __init__(self, operator, arguments):
        self.operator = operator    # Identifier
        self.arguments = arguments  # [ASTNode]

    def __repr__(self):
        return f"Call({self.operator}, {self.arguments})"

class ASTBuilder:

    def build(self, parse_tree_list):
        """Input: Liste von Expr-Knoten aus dem Parser
           Output: Program(AST)"""
        return Program([self.build_expr(e) for e in parse_tree_list])


    def build_expr(self, node):

        # Already literal from parser
        if isinstance(node, Literal):
            return Literal(node.value)

        # Identifier
        if isinstance(node, Identifier):
            return Identifier(node.name)

        # ListExpr → Call
        if isinstance(node, ListExpr):
            operator_ast = Identifier(node.operator)

            args_ast = [self.build_expr(arg) for arg in node.args]

            return Call(operator_ast, args_ast)

        raise Exception(f"Unknown parse tree node: {node}")

def lex_string(input_str):
    lexer_queue.queue = list(input_str)
    tokens = []
    while True:
        t = next_token()
        tokens.append(t)
        if t.typ == TOKEN_EOF:
            break
    return tokens


def test_ast_building():

    code = """
        (+ 1 (* 2 3))
        (print "Hello")
        (if (< 1 2) (print "yes") (print "no"))
    """

    print("Input")
    print(code)

    # LEXING
    tokens = lex_string(code)

    # PARSING
    parser = Parser(tokens)
    parse_tree = parser.parse_program()

    print("\nTree")
    for pt in parse_tree:
        print(pt)

    # BUILD AST
    builder = ASTBuilder()
    ast = builder.build(parse_tree)

    print("\nAST")
    print(ast)


if __name__ == "__main__":
    code = """
        ;; Mein Test Code
        (+ 1 (* 2 3))

        (print "hello world")

        (if (< 1 2)
            (print "yes")
            (print "no"))
        """

    print("Input:")
    print(code)

    tokens = lex_string(code)

    parser = Parser(tokens)

    print_tokens(tokens)

    test_ast_building()