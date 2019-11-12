package com.bundle.conversion;

public class Constant {
	
	/**
	 * The 'data' is either 0 or 1, specifying this resource is either undefined
	 * or empty, respectively.
	 */
	public static final int 
	TYPE_NULL				=0,
	TYPE_REFERENCE			=1,
	TYPE_ATTRIBUTE			=2,
	TYPE_STRING				=3,
	TYPE_FLOAT				=4,
	TYPE_DIMENSION			=5,
	TYPE_FRACTION			=6,
	TYPE_FIRST_INT			=16,
	TYPE_INT_DEC			=16,
	TYPE_INT_HEX			=17,
	TYPE_INT_BOOLEAN		=18,
	TYPE_FIRST_COLOR_INT	=28,
	TYPE_INT_COLOR_ARGB8	=28,
	TYPE_INT_COLOR_RGB8		=29,
	TYPE_INT_COLOR_ARGB4	=30,
	TYPE_INT_COLOR_RGB4		=31,
	TYPE_LAST_COLOR_INT		=31,
	TYPE_LAST_INT			=31;

	public static final int
	COMPLEX_UNIT_PX			=0,
	COMPLEX_UNIT_DIP		=1,
	COMPLEX_UNIT_SP			=2,
	COMPLEX_UNIT_PT			=3,
	COMPLEX_UNIT_IN			=4,
	COMPLEX_UNIT_MM			=5,
	COMPLEX_UNIT_SHIFT		=0,
	COMPLEX_UNIT_MASK		=15,
	COMPLEX_UNIT_FRACTION	=0,
	COMPLEX_UNIT_FRACTION_PARENT=1,
	COMPLEX_RADIX_23p0		=0,
	COMPLEX_RADIX_16p7		=1,
	COMPLEX_RADIX_8p15		=2,
	COMPLEX_RADIX_0p23		=3,
	COMPLEX_RADIX_SHIFT		=4,
	COMPLEX_RADIX_MASK		=3,
	COMPLEX_MANTISSA_SHIFT	=8,
	COMPLEX_MANTISSA_MASK	=0xFFFFFF;


	public static final String NULL = "0x00";
	public static final String NULL1 = "0x0";
	/**
	 * The 'data' holds a reference to another resource table entry.
	 */
	public static final String REFERENCE  = "0x01";
	public static final String REFERENCE1 = "0x1";

	/**
	 * The 'data' holds an attribute resource identifier.
	 */
	public static final String ATTRIBUTE  = "0x02";
	public static final String ATTRIBUTE1 = "0x2";

	/**
	 * The 'data' holds an index into the containing resource table's global
	 * value string pool.
	 */
	public static final String STRING  = "0x03";
	public static final String STRING1 = "0x3";

	/**
	 * The 'data' holds a single-precision floating point number.
	 */
	public static final String FLOAT  = "0x04";
	public static final String FLOAT1 = "0x4";

	/**
	 * The 'data' holds a complex number encoding a dimension value, such as
	 * "100in".
	 */
	public static final String DIMENSION  = "0x05";
	public static final String DIMENSION1 = "0x5";

	/**
	 * The 'data' holds a complex number encoding a fraction of a container.
	 */
	public static final String FRACTION  = "0x06";
	public static final String FRACTION1 = "0x6";

	/**
	 * The 'data' holds a dynamic ResTable_ref, which needs to be resolved
	 * before it can be used like a REFERENCE.
	 */
	public static final String DYNAMIC_REFERENCE  = "0x07";
	public static final String DYNAMIC_REFERENCE1 = "0x7";

	/**
	 * The 'data' holds an attribute resource identifier, which needs to be
	 * resolved before it can be used like a ATTRIBUTE.
	 */
	public static final String DYNAMIC_ATTRIBUTE   = "0x08";
	public static final String DYNAMIC_ATTRIBUTE1  = "0x8";

	/**
	 * The 'data' is a raw integer value of the form n..n.
	 */
	public static final String INT_DEC = "0x10";

	/**
	 * The 'data' is a raw integer value of the form 0xn..n.
	 */
	public static final String INT_HEX = "0x11";

	/**
	 * The 'data' is either 0 or 1, for input "false" or "true" respectively.
	 */
	public static final String INT_BOOLEAN = "0x12";

	/**
	 * The 'data' is a raw integer value of the form #aarrggbb.
	 */
	public static final String INT_COLOR_ARGB8 = "0x1c";

	/**
	 * The 'data' is a raw integer value of the form #rrggbb.
	 */
	public static final String INT_COLOR_RGB8 = "0x1d";

	/**
	 * The 'data' is a raw integer value of the form #argb.
	 */
	public static final String INT_COLOR_ARGB4 = "0x1e";

	/**
	 * The 'data' is a raw integer value of the form #rgb.
	 */
	public static final String INT_COLOR_RGB4 = "0x1f";
}
