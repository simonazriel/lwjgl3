/*
 * Copyright LWJGL. All rights reserved.
 * License terms: https://www.lwjgl.org/license
 */
package org.lwjgl.generator

import org.lwjgl.*
import java.nio.*
import kotlin.reflect.*

open class NativeType(
	/** The type used in the native API. */
	val name: String,
	/** The type we map the native type to. */
	val mapping: TypeMapping
) {

	// Lets get rid a level of indirection

	/** The JNI function argument type. */
	internal val jniFunctionType: String
		get() = mapping.jniFunctionType

	/** The native method argument type. */
	internal val nativeMethodType: String
		get() = mapping.nativeMethodType.simpleName

	/** The Java method argument type. */
	internal open val javaMethodType: String
		get() = if (this.mapping === PrimitiveMapping.BOOLEAN4) "boolean" else mapping.javaMethodType.simpleName

	override fun toString(): String =
		"${this.javaClass.simpleName}: $name | ${mapping.jniFunctionType} | ${mapping.nativeMethodType} | ${mapping.javaMethodType}"
}

// Specialization for primitives.
open class PrimitiveType(name: String, mapping: PrimitiveMapping) : NativeType(name, mapping)

// Specialization for integers.
open class IntegerType(name: String, mapping: PrimitiveMapping, val unsigned: Boolean = false) : PrimitiveType(name, mapping)

val String.enumType: IntegerType get() = IntegerType(this, PrimitiveMapping.INT)

// Specialization for string characters.
class CharType(name: String, mapping: CharMapping) : PrimitiveType(name, mapping)

// Pointers
open class PointerType(
	/** The type used in the native API. */
	name: String,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.OPAQUE_POINTER,
	/** If true, the nativeType typedef includes a pointer. */
	val includesPointer: Boolean = false,
	/** Optional element type. See the secondary constructors below. */
	val elementType: NativeType? = null
) : NativeType(name, mapping)

/** Converts primitive to array */
val PrimitiveType.p: PointerType get() = PointerType(this.name, (this.mapping as PrimitiveMapping).toPointer, elementType = this)

fun PrimitiveType.p(name: String) = PointerType(
	name,
	(this.mapping as PrimitiveMapping).toPointer,
	elementType = this,
	includesPointer = true
)

/** pointer to pointer. */
private fun PointerType.pointerTo(): String {
	val builder = StringBuilder(name)
	if (!includesPointer) {
		if (!name.endsWith('*'))
			builder.append(' ')
		builder.append('*')
	}

	return builder.toString()
}

val PointerType.p: PointerType get() = PointerType(this.pointerTo(), PointerMapping.DATA_POINTER, elementType = this)
val PointerType.p_const_p: PointerType get() = PointerType("${this.pointerTo()} const", PointerMapping.DATA_POINTER, elementType = this)

val String.p: PointerType get() = PointerType(this, includesPointer = false)
val String.opaque_p: PointerType get() = PointerType(this, includesPointer = true)

// Objects (pointer wrappers)
open class ObjectType(
	/** The Java wrapper class. */
	val className: String,
	/** The type used in the native API. */
	name: String = className,
	/** If true, the nativeType typedef includes a pointer. */
	includesPointer: Boolean = true
) : PointerType(name, PointerMapping.OPAQUE_POINTER, includesPointer) {
	override val javaMethodType: String
		get() = className
}

// Structs, 3 cases:
//
//     1) struct value
//         * val foo = struct(...).nativeType
//         * foo is <StructType with includesPointer = false>
//         * mapped to ByteBuffer
//     2) pointer to struct value(s), i.e. array of struct values
//         * val foo_p = foo.p
//         * foo_p is <StructType with includesPointer = true>
//         * mapped to ByteBuffer
//     3) pointer to pointer to struct value(s):
//         * val foo_pp = foo_p.p
//         * foo_pp is <PointerType with includesPointer = false>
//         * mapped to PointerBuffer
//
// The special case is #2. One would normally expect a PointerBuffer there (#1 is a ByteBuffer),
// but since struct arrays are packed, there's no need for an extra indirection. The difference
// between #2 and #3 is handled in the StructType.p extension property below.
class StructType(
	/** The struct size in bytes. */
	val definition: Struct,
	/** The type used in the native API. */
	name: String = definition.nativeName,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.DATA_BYTE,
	/** If true, the nativeType typedef includes a pointer. If false, the argument will be passed-by-value. */
	includesPointer: Boolean = false,
	elementType: NativeType? = null
) : PointerType(name, mapping, includesPointer, elementType) {
	override val javaMethodType: String
		get() = definition.className
}

/** Converts a struct value to a pointer to a struct value. */
val StructType.p: PointerType get() = if (this.includesPointer)
	PointerType(this.pointerTo(), PointerMapping.DATA_POINTER, elementType = this)
else
	StructType(this.definition, this.pointerTo(), includesPointer = true, elementType = this)

fun StructType.p(name: String) = StructType(this.definition, name, includesPointer = true)

// Strings
class CharSequenceType(
	/** The type used in the native API. */
	name: String,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.DATA_BYTE,
	/** If true, the nativeType typedef includes a pointer. */
	includesPointer: Boolean = false,
	/** The CharSequence charset. */
	val charMapping: CharMapping = CharMapping.ASCII
) : PointerType(name, mapping, includesPointer)

/** Converts CharType to CharSequenceType. */
fun CharSequenceType(
	charType: CharType,
	/** The type we map the native type to. */
	mapping: PointerMapping = PointerMapping.DATA_BYTE
) = CharSequenceType(charType.name, mapping = mapping, charMapping = (charType.mapping as CharMapping))

val CharType.p: CharSequenceType get() = CharSequenceType(this)

// Callbacks
class CallbackType(
	val signature: CallbackFunction,
	name: String = signature.className
) : ObjectType(signature.className, name) {
	override val javaMethodType: String
		get() = "${super.javaMethodType}I"
}

// Arrays (automatically used for array overloads)
class ArrayType(
	type: PointerType,
	mapping: PointerMapping = type.mapping as PointerMapping
) : PointerType(
	type.name,
	mapping,
	type.includesPointer,
	type.elementType
)

// typedefs
fun typedef(typedef: PrimitiveType, name: String) = PrimitiveType(name, typedef.mapping as PrimitiveMapping)

fun typedef(typedef: IntegerType, name: String) = IntegerType(name, typedef.mapping as PrimitiveMapping, typedef.unsigned)
fun typedef(typedef: PointerType, name: String) = PointerType(name, typedef.mapping as PointerMapping, true, typedef.elementType)

// --- [ TYPE MAPPINGS ] ---

open class TypeMapping(
	/** The JNI function argument type. */
	val jniFunctionType: String,
	/** The native method argument type. */
	val nativeMethodType: Class<*>,
	/** The Java method argument type. */
	val javaMethodType: Class<*>
) {

	constructor(
		/** The JNI function argument type. */
		jniFunctionType: String,
		/** The native method argument type. */
		nativeMethodClass: KClass<*>,
		/** The Java method argument type. */
		javaMethodClass: KClass<*>
	) : this(jniFunctionType, nativeMethodClass.java, javaMethodClass.java)

	companion object {
		val VOID = TypeMapping("void", Void.TYPE, Void.TYPE)
	}

	internal val jniSignatureStrict: String get() = when (this.nativeMethodType) {
		Boolean::class.java -> "Z"
		Byte::class.java    -> "B"
		Char::class.java    -> "C"
		Double::class.java  -> "D"
		Float::class.java   -> "F"
		Int::class.java     -> "I"
		Long::class.java    -> "J"
		Short::class.java   -> "S"
		Void.TYPE           -> "V"
		else                -> "L${this.nativeMethodType.name};"
	}

	internal val jniSignature: String get() = if (this.nativeMethodType === Long::class.java && this !== PrimitiveMapping.LONG) "P" else jniSignatureStrict

	internal val jniSignatureJava: String get() = if (this.nativeMethodType === Long::class.java)
		if (this === PrimitiveMapping.LONG) "J" else "P"
	else
		""

	internal val jniSignatureArray: String get() = when ((this as PointerMapping).primitive) {
		"double" -> "_3D"
		"float"  -> "_3F"
		"int"    -> "_3I"
		"long"   -> "_3J"
		"short"  -> "_3S"
		else     -> throw IllegalStateException()
	}

}

open class PrimitiveMapping(
	jniFunctionType: String,
	javaMethodType: KClass<*>,
	val bytes: Int,
	val toPointer: PointerMapping
) : TypeMapping(jniFunctionType, javaMethodType, javaMethodType) {

	companion object {
		val BOOLEAN = PrimitiveMapping("jboolean", Boolean::class, 1, PointerMapping.DATA_BOOLEAN)
		val BOOLEAN4 = PrimitiveMapping("jint", Int::class, 4, PointerMapping.DATA_INT)

		val BYTE = PrimitiveMapping("jbyte", Byte::class, 1, PointerMapping.DATA_BYTE)
		val SHORT = PrimitiveMapping("jshort", Short::class, 2, PointerMapping.DATA_SHORT)
		val INT = PrimitiveMapping("jint", Int::class, 4, PointerMapping.DATA_INT)
		val LONG = PrimitiveMapping("jlong", Long::class, 8, PointerMapping.DATA_LONG)

		// Integer type with enough precision to store a pointer
		val POINTER = PrimitiveMapping("jlong", Long::class, Int.MAX_VALUE /* should not be used */, PointerMapping.DATA_POINTER)

		val FLOAT = PrimitiveMapping("jfloat", Float::class, 4, PointerMapping.DATA_FLOAT)
		val DOUBLE = PrimitiveMapping("jdouble", Double::class, 8, PointerMapping.DATA_DOUBLE)
	}

}

class CharMapping(
	jniFunctionType: String,
	javaMethodType: KClass<*>,
	bytes: Int,
	toPointer: PointerMapping,
	val charset: String
) : PrimitiveMapping(jniFunctionType, javaMethodType, bytes, toPointer) {

	companion object {
		val ASCII = CharMapping("jbyte", Byte::class, 1, PointerMapping.DATA_BYTE, "ASCII")
		val UTF8 = CharMapping("jbyte", Byte::class, 1, PointerMapping.DATA_BYTE, "UTF8")
		val UTF16 = CharMapping("jchar", Char::class, 2, PointerMapping.DATA_SHORT, "UTF16")
	}

}

open class PointerMapping(
	javaMethodType: KClass<*>,
	val byteShift: String? = null
) : TypeMapping("jlong", Long::class, javaMethodType) {

	companion object {
		val OPAQUE_POINTER = PointerMapping(Long::class)

		/** Useful for void * params that will be AutoTyped. */
		val DATA = PointerMapping(ByteBuffer::class)

		val DATA_POINTER = PointerMapping(PointerBuffer::class, "POINTER_SHIFT")

		fun PointerMapping(javaMethodType: KClass<*>, byteShift: Int) = PointerMapping(javaMethodType, Integer.toString(byteShift))

		val DATA_BOOLEAN = PointerMapping(ByteBuffer::class, 0)
		val DATA_BYTE = PointerMapping(ByteBuffer::class, 0)
		val DATA_SHORT = PointerMapping(ShortBuffer::class, 1)
		val DATA_INT = PointerMapping(IntBuffer::class, 2)
		val DATA_LONG = PointerMapping(LongBuffer::class, 3)
		val DATA_FLOAT = PointerMapping(FloatBuffer::class, 2)
		val DATA_DOUBLE = PointerMapping(DoubleBuffer::class, 3)
	}

	internal val isMultiByte = byteShift != null && byteShift != "0"

	internal val box = super.javaMethodType.simpleName.substringBefore("Buffer")
	internal val primitive: String get() = when (this) {
		DATA_BOOLEAN -> "boolean"
		DATA_POINTER -> "long"
		else         -> box.toLowerCase()
	}

	internal val mallocType: String get() = when (box) {
		"Byte" -> ""
		else   -> box
	}

}

val NativeType.isPointer: Boolean
	get() = this is PointerType || this.mapping === PrimitiveMapping.POINTER

val NativeType.isPointerData: Boolean
	get() = this is PointerType && this.mapping !== PointerMapping.OPAQUE_POINTER

val TypeMapping.isPointerSize: Boolean
	get() = this === PointerMapping.DATA_INT || this === PointerMapping.DATA_POINTER

internal val TypeMapping.isArray: Boolean
	get() = this is PointerMapping && this.isMultiByte && this !== PointerMapping.DATA_POINTER