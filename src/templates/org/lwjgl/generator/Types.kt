/* 
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.generator

import java.nio.*
import org.lwjgl.PointerBuffer

public open class NativeType(
	/** The type used in the native API. */
	val name: String,
	/** The type we map the native type to. */
	val mapping: TypeMapping
) {

	// Lets get rid a level of indirection

	/** The JNI function argument type. */
	val jniFunctionType: String
		get() = mapping.jniFunctionType

	/** The native method argument type. */
	val nativeMethodType: Class<out Any>
		get() = mapping.nativeMethodType

	/** The Java method argument type. */
	val javaMethodType: Class<out Any>
		get() = mapping.javaMethodType

}

// Specialization for primitives.
public open class PrimitiveType(name: String, mapping: PrimitiveMapping): NativeType(name, mapping)
// Specialization for string characters.
public class CharType(name: String, mapping: CharMapping): PrimitiveType(name, mapping)

// Pointers
public open class PointerType(
	/** The type used in the native API. */
	name: String,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.NAKED_POINTER,
	/** If true, the nativeType typedef includes a pointer. */
	val includesPointer: Boolean = false
): NativeType(name, mapping)

// Structs
public class StructType(
	/** The type used in the native API. */
	name: String,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.DATA_BYTE,
	/** If true, the nativeType typedef includes a pointer. */
	includesPointer: Boolean = false,
	/** The struct size in bytes. */
	val definition: Struct
): PointerType(name, mapping, includesPointer)

// Strings
public class CharSequenceType(
	/** The type used in the native API. */
	name: String,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.DATA_BYTE,
	/** If true, the nativeType typedef includes a pointer. */
	includesPointer: Boolean = false,
	/** The CharSequence charset. */
	val charMapping: CharMapping = CharMapping.ASCII,
	/** If true, the nativeType typedef includes a pointer. */
	val nullTerminated: Boolean = true
): PointerType(name, mapping, includesPointer)

// Callback functions
public class CallbackType(
	name: String,
    val callback: CallbackFunction
): PointerType(name = name, includesPointer = true)

// TODO: This is "too public", is there a better way?
public fun NativeType.func(
	packageName: String,
	funcName: String,
	documentation: String,
	convention: String,
	async: Boolean,
	vararg parameters: Parameter
): CallbackFunction = ReturnValue(this).func(packageName, funcName, documentation, convention, async, *parameters)
public fun ReturnValue.func(
	packageName: String,
	funcName: String,
	documentation: String,
	convention: String,
	async: Boolean,
    vararg parameters: Parameter
): CallbackFunction {
	val func = CallbackFunction(this, funcName, documentation, parameters, packageName, convention, async)
	CallbackRegistry add func
	return func
}

// --- [ TYPE MAPPINGS ] ---

public open class TypeMapping(
	/** The JNI function argument type. */
	val jniFunctionType: String,
	/** The native method argument type. */
	val nativeMethodType: Class<out Any>,
	/** The Java method argument type. */
	val javaMethodType: Class<out Any>
) {

	class object {
		val VOID = TypeMapping("void", Void.TYPE, Void.TYPE)
		val BOOLEAN = TypeMapping("jboolean", javaClass<Boolean>(), javaClass<Boolean>())
	}

}

public open class PrimitiveMapping(
	jniFunctionType: String,
	javaMethodType: Class<out Any>,
    val bytes: Int
): TypeMapping(jniFunctionType, javaMethodType, javaMethodType) {

	class object {
		val BYTE = PrimitiveMapping("jbyte", javaClass<Byte>(), 1)
		val CHAR = PrimitiveMapping("jchar", javaClass<Char>(), 2)
		val SHORT = PrimitiveMapping("jshort", javaClass<Short>(), 2)
		val INT = PrimitiveMapping("jint", javaClass<Int>(), 4)
		val LONG = PrimitiveMapping("jlong", javaClass<Long>(), 8)

		val FLOAT = PrimitiveMapping("jfloat", javaClass<Float>(), 4)
		val DOUBLE = PrimitiveMapping("jdouble", javaClass<Double>(), 8)
	}

}

public class CharMapping(
	jniFunctionType: String,
	javaMethodType: Class<out Any>,
	bytes: Int,
    val charset: String
): PrimitiveMapping(jniFunctionType, javaMethodType, bytes) {

	class object {
		val ASCII = CharMapping("jbyte", javaClass<Byte>(), 1, "ASCII")
		val UTF8 = CharMapping("jbyte", javaClass<Byte>(), 1, "UTF8")
		val UTF16 = CharMapping("jchar", javaClass<Char>(), 2, "UTF16")
	}

}

public open class PointerMapping(
	javaMethodType: Class<out Any>,
    val byteShift: String? = null
): TypeMapping("jlong", javaClass<Long>(), javaMethodType) {

	class object {
		val NAKED_POINTER = PointerMapping(javaClass<Long>())

		/** Useful for void * params that will be AutoTyped. */
		val DATA = PointerMapping(javaClass<Buffer>())

		val DATA_POINTER = PointerMapping(javaClass<PointerBuffer>(), "PointerBuffer.getPointerSizeShift()")

		fun PointerMapping(javaMethodType: Class<out Any>, byteShift: Int) = PointerMapping(javaMethodType, Integer.toString(byteShift))

		val DATA_BOOLEAN = PointerMapping(javaClass<ByteBuffer>(), 0)
		val DATA_BYTE = PointerMapping(javaClass<ByteBuffer>(), 0)
		val DATA_SHORT = PointerMapping(javaClass<ShortBuffer>(), 1)
		val DATA_INT = PointerMapping(javaClass<IntBuffer>(), 2)
		val DATA_LONG = PointerMapping(javaClass<LongBuffer>(), 3)
		val DATA_FLOAT = PointerMapping(javaClass<FloatBuffer>(), 2)
		val DATA_DOUBLE = PointerMapping(javaClass<DoubleBuffer>(), 3)

		val primitiveMap = hashMapOf<PointerMapping, String>(
			DATA_POINTER to "pointer",

			DATA_BOOLEAN to "boolean",
			DATA_BYTE to "byte",
			DATA_SHORT to "short",
			DATA_INT to "int",
			DATA_LONG to "long",
			DATA_FLOAT to "float",
			DATA_DOUBLE to "double"
		)
	}

	val isMultiByte = byteShift != null && byteShift != "0"

}