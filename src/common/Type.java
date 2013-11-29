package common;

import java.util.ArrayList;
import frontend.ast.Expr;
import frontend.ast.Const;
import frontend.ast.VarDecl;

/**
 * A representation of a type.
 */
public class Type {
    private static final Type TYPE_INT = new Type(null);
    private static final Type TYPE_REAL = new Type(null);

    private final Type arrayElementType;
    protected final ArrayList<Expr> dimensions = new ArrayList<Expr>();
 
    /**
     * Creates a new instance of a type object.
     * Consider using the static methods defined below instead of this
     * constructor.
     */
    private Type(final Type arrayElementType) {
        this.arrayElementType = arrayElementType;
    }
    
    public Type(){
    	this.arrayElementType = null;
    }

    /**
     * Returns a type object representing the type "int".
     *
     * @return a type object for the type "int"
     */
    public static Type getIntType() {
        return TYPE_INT;
    }

    /**
     * Returns a type object representing the type "real".
     *
     * @return a type object for the type "real"
     */
    public static Type getRealType() {
        return TYPE_REAL;
    }

    /**
     * Returns a type object representing an array type. The type of the array
     * elements may be specified as well.
     *
     * @param arrayElementType
     *  The type of the array elements. Use an array type here to create
     *  arrays of arrays. Must not be <code>null</code>.
     * @param size
     *  The number of elements of the array. Must not be <code>null</code>.
     * @return a type object for an array type
     */
    public static Type getArrayType(final Type arrayElementType, final Expr size) {
        assert arrayElementType != null;
        assert size != null;

        if (arrayElementType.isArrayType()) {
            return Type.addDimension(arrayElementType, size);
        } else {
            return Type.addDimension(new Type(arrayElementType), size);
        }
    }
    

    /**
     * Checks whether this type object represents the type "int".
     *
     * @return <code>true</code> if this type object represents the type
     *  "int", <code>false</code> otherwise.
     */
    public boolean isIntType() {
        return this == TYPE_INT;
    }

    /**
     * Checks whether this type object represents the type "real".
     *
     * @return <code>true</code> if this type object represents the type
     *  "real", <code>false</code> otherwise.
     */
    public boolean isRealType() {
        return this == TYPE_REAL;
    }

    /**
     * Checks whether this type object represents an array type
     *
     * @return <code>true</code> if this type object represents an array
     *  type, <code>false</code> otherwise.
     */
    public boolean isArrayType() {
        return arrayElementType != null;
    }

    /**
     * Checks whether this type object represents a primitive type
     *
     * @return <code>true</code> if this type object represents a primitive
     *  type, <code>false</code> otherwise.
     */
    public boolean isPrimitiveType() {
        return arrayElementType == null;
    }

    /**
     * If this type object describes an array type, returns the type object of
     * the array elements. Otherwise, returns <code>null</code>.
     */
    public Type getArrayElementType() {
        return arrayElementType;
    }

    /**
     * Provides an iterator over the dimensions of an array type.
     */
    public Iterable<Expr> getDimensions() {
        return java.util.Collections.unmodifiableList(dimensions);
    }

    public final Expr getDim(int i) {
        return dimensions.get(i);
    }

    public final void setDim(int i, Expr x) {
        dimensions.set(i, x);
    }

    public int getNumDimensions() {
        return dimensions.size();
    }

    /**
     * Get the size of the array in the given dimension
     * 
     * @param dim
     * 			dim of which we want the size
     * @return
     * 			size of the array in the given dimension
     */
    public int getDimSize(int dim) {
        return ((Const)dimensions.get(dim)).toInt();
    }

    public String toString() {
        if (arrayElementType != null) {
            return arrayElementType.toString() + "[]";
        } else {
            if (this == TYPE_INT) {
                return "int";
            } else if (this == TYPE_REAL) {
                return "real";
            } else {
                return "unknown";
            }
        }
    }

    private static Type addDimension(final Type arrayType, final Expr size) {
        arrayType.dimensions.add(size);
        return arrayType;
    }
    
}

