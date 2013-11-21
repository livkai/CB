package frontend.lexer;

import java.io.BufferedReader;
import java.io.IOException;

/**
 * public class that encapsulates the lexer (necessary because Parser and Lexer
 * are in different packages
 *  
 */
public final class Lexer {
    private final Yylex lex;

    /**
     * Creates a new Lexer
     * 
     * @param in
     *            set the BufferedReader of the Lexer to this
     */
    public Lexer(final BufferedReader in) {
        lex = new Yylex(in);
    }

    /**
     * Returns the next Token in the stream
     * 
     * @return the next Token in the stream
     * @throws IOException
     *             if an error while processing the stream occures
     */
    public final Yytoken yylex() throws IOException {
        return lex.yylex();
    }
}
