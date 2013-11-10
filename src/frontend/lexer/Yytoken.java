package frontend.lexer;

/**
 * Token class of the Lexer
 *  
 */
public final class Yytoken {

    private final int line;

    private final String text;

    private final int type;

    /**
     * Creates a new Token
     * 
     * @param type
     *            set the type to this
     * @param text
     *            set the text to this
     * @param line
     *            set the line number of the token to this
     */
    public Yytoken(int type, String text, int line) {
        this.line = line;
        this.text = text;
        this.type = type;
    }

    /**
     * Returns the line number in which the token occured
     * 
     * @return line number
     */
    public final int line() {
        return line;
    }

    /**
     * Returns the text of the token
     * 
     * @return token text
     */
    public final String text() {
        return text;
    }

    /**
     * Returns the type of the token
     * 
     * @return type of the token
     */
    public final int type() {
        return type;
    }
}
