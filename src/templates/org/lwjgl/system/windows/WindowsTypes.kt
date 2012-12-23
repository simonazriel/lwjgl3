/* 
 * Copyright LWJGL. All rights reserved.
 * License terms: http://lwjgl.org/license.php
 */
package org.lwjgl.system.windows

import org.lwjgl.generator.*

val WINDOWS_PACKAGE = "org.lwjgl.system.windows"

// UNICODE is defined WindowsLWJGL.h, so all T* types below are UTF16.

val BOOL = PrimitiveType("BOOL", PrimitiveMapping.INT) // Not boolean because of WinUser#GetMessage

val BYTE = PrimitiveType("BYTE", PrimitiveMapping.BYTE)
val WORD = PrimitiveType("WORD", PrimitiveMapping.SHORT)
val DWORD = PrimitiveType("DWORD", PrimitiveMapping.INT)
val UINT = PrimitiveType("UINT", PrimitiveMapping.INT)
val FLOAT = PrimitiveType("FLOAT", PrimitiveMapping.FLOAT)

val short = PrimitiveType("short", PrimitiveMapping.SHORT)
val int = PrimitiveType("int", PrimitiveMapping.INT)
val LONG = PrimitiveType("LONG", PrimitiveMapping.INT)

val int_p = PointerType("int", PointerMapping.DATA_INT)
val UINT_p = PointerType("UINT", PointerMapping.DATA_INT)
val FLOAT_p = PointerType("FLOAT", PointerMapping.DATA_FLOAT)

val LRESULT = PointerType(name = "LRESULT", includesPointer = true)
val WPARAM = PointerType(name = "WPARAM", includesPointer = true)
val LPARAM = PointerType(name = "LPARAM", includesPointer = true)

val TCHAR = CharType("TCHAR", CharMapping.UTF16)

val LPCTSTR = CharSequenceType(
	name = "LPCTSTR",
	includesPointer = true,
	charMapping = CharMapping.UTF16,
	nullTerminated = true
)

val LPCSTR = CharSequenceType(name = "LPCSTR", includesPointer = true)
val HMODULE = PointerType(name = "HMODULE", includesPointer = true)
val FARPROC = PointerType(name = "FARPROC", includesPointer = true)
val HWND = PointerType(name = "HWND", includesPointer = true)
val HMENU = PointerType(name = "HMENU", includesPointer = true)
val HINSTANCE = PointerType(name = "HINSTANCE", includesPointer = true)
val LPVOID = PointerType(name = "LPVOID", includesPointer = true)
val HDC = PointerType(name = "HDC", includesPointer = true)
val HGLRC = PointerType(name = "HGLRC", includesPointer = true)
val HGDIOBJ = PointerType(name = "HGDIOBJ", includesPointer = true)
val PROC = PointerType(name = "PROC", includesPointer = true)

val POINTFLOAT = StructType(
	name = "POINTFLOAT",
	definition = struct(WINDOWS_PACKAGE, "POINTFLOAT") {
		nativeImport ("WindowsLWJGL.h")
		FLOAT.member("x")
		FLOAT.member("y")
	}
)

val LPGLYPHMETRICSFLOAT = StructType(
	name = "LPGLYPHMETRICSFLOAT",
	includesPointer = true,
	definition = struct(WINDOWS_PACKAGE, "GLYPHMETRICSFLOAT") {
		javaDoc("Contains information about the placement and orientation of a glyph in a character cell.")
		nativeImport ("WindowsLWJGL.h")
		FLOAT.member("gmfBlackBoxX", "blackBoxX")
		FLOAT.member("gmfBlackBoxY", "blockBoxY")
		POINTFLOAT.member("gmfptGlyphOrigin", "glyphOrigin")
		FLOAT.member("gmfCellIncX", "cellIncX")
		FLOAT.member("gmfCellIncY", "cellIncY")
	}
)

val PIXELFORMATDESCRIPTOR_STRUCT = struct(WINDOWS_PACKAGE, "PIXELFORMATDESCRIPTOR") {
	javaDoc("Describes the pixel format of a drawing surface.")
	nativeImport ("WindowsLWJGL.h")
	WORD.member("nSize", "size")
	WORD.member("nVersion", "version")
	DWORD.member("dwFlags", "flags")
	BYTE.member("iPixelType", "pixelType")
	BYTE.member("cColorBits", "colorBits")
	BYTE.member("cRedBits", "redBits")
	BYTE.member("cRedShift", "redShirt")
	BYTE.member("cGreenBits", "greenBits")
	BYTE.member("cGreenShift", "greenShift")
	BYTE.member("cBlueBits", "blueBits")
	BYTE.member("cBlueShift", "blueShift")
	BYTE.member("cAlphaBits", "alphaBits")
	BYTE.member("cAlphaShift", "alphaShift")
	BYTE.member("cAccumBits", "accumBits")
	BYTE.member("cAccumRedBits", "accumRedBits")
	BYTE.member("cAccumGreenBits", "accumGreenBits")
	BYTE.member("cAccumBlueBits", "accumBlueBits")
	BYTE.member("cAccumAlphaBits", "accumAlphaBits")
	BYTE.member("cDepthBits", "depthBits")
	BYTE.member("cStencilBits", "stencilBits")
	BYTE.member("cAuxBuffers", "auxBuffers")
	BYTE.member("iLayerType", "layerType")
	BYTE.member("bReserved", "reserved")
	DWORD.member("dwLayerMask", "layerMask")
	DWORD.member("dwVisibleMask", "visibleMask")
	DWORD.member("dwDamageMask", "damageMask")
}
val PIXELFORMATDESCRIPTOR = StructType(name = "PIXELFORMATDESCRIPTOR", definition = PIXELFORMATDESCRIPTOR_STRUCT)
val LPPIXELFORMATDESCRIPTOR = StructType(name = "LPPIXELFORMATDESCRIPTOR", definition = PIXELFORMATDESCRIPTOR_STRUCT, includesPointer = true)

val WNDPROC = PointerType(name = "WNDPROC", includesPointer = true)

val HICON = PointerType(name = "HICON", includesPointer = true)
val HCURSOR = PointerType(name = "HCURSOR", includesPointer = true)
val HBRUSH = PointerType(name = "HBRUSH", includesPointer = true)

val ATOM = PrimitiveType("ATOM", PrimitiveMapping.SHORT)

private val WNDCLASSEX_STRUCT = struct(WINDOWS_PACKAGE, "WNDCLASSEX") {
	javaDoc("Contains the window class attributes that are registered by the {@link WinUser#RegisterClassEx} function.")
	nativeImport ("WindowsLWJGL.h")
	UINT.member("cbSize", "size")
	UINT.member("style")
	WNDPROC.member("lpfnWndProc", "wndProc")
	int.member("cbClsExtra", "clsExtra")
	int.member("cbWndExtra", "wndExtra")
	HINSTANCE.member("hInstance", "instance")
	HICON.member("hIcon", "icon")
	HCURSOR.member("hCursor", "cursor")
	HBRUSH.member("hbrBackground", "background")
	LPCTSTR.member("lpszMenuName", "menuName")
	LPCTSTR.member("lpszClassName", "className")
	HICON.member("hIconSm", "iconSm")
}

val WNDCLASSEX = StructType(name = "WNDCLASSEX", definition = WNDCLASSEX_STRUCT)
val LPWNDCLASSEX = StructType(name = "LPWNDCLASSEX", definition = WNDCLASSEX_STRUCT, includesPointer = true)

val LPOSVERSIONINFO = StructType(
	name = "LPOSVERSIONINFO",
	includesPointer = true,
	definition = struct(WINDOWS_PACKAGE, "OSVERSIONINFOEX") {
		javaDoc("""
			Contains operating system version information. The information includes major and minor
			version numbers, a build number, a platform identifier, and information about product
			suites and the latest Service Pack installed on the system.
		""")
		nativeImport ("WindowsLWJGL.h")
		DWORD.member("dwOSVersionInfoSize", "osVersionInfoSize")
		DWORD.member("dwMajorVersion", "majorVersion")
		DWORD.member("dwMinorVersion", "minorVersion")
		DWORD.member("dwBuildNumber", "buildNumber")
		DWORD.member("dwPlatformId", "platformId")
		TCHAR.member("szCSDVersion", "csdVersion", 128, true)
		WORD.member("wServicePackMajor", "servicePackMajor")
		WORD.member("wServicePackMinor", "servicePackMinor")
		WORD.member("wSuiteMask", "suiteMask")
		BYTE.member("wProductType", "productType")
		//BYTE.member("wReserved", "reserved")
	}
)

private val POINT_STRUCT = struct(WINDOWS_PACKAGE, "POINT") {
	javaDoc("Defines the x- and y- coordinates of a point.")
	nativeImport ("WindowsLWJGL.h")
	LONG.member("x")
	LONG.member("y")
}
val POINT = StructType(name = "POINT", definition = POINT_STRUCT)
val LPPOINT = StructType (name = "LPPOINT", definition = POINT_STRUCT, includesPointer = true)

private val MSG_STRUCT = struct(WINDOWS_PACKAGE, "MSG") {
	javaDoc("Contains message information from a thread's message queue.")
	nativeImport ("WindowsLWJGL.h")
	HWND.member("hwnd", "window");
	UINT.member("message")
	WPARAM.member("wParam")
	LPARAM.member("lParam")
	DWORD.member("time")
	POINT.member("pt", "point")
}
val MSG = StructType (name = "MSG", definition = MSG_STRUCT)
val LPMSG = StructType (name = "LPMSG", definition = MSG_STRUCT, includesPointer = true)

val POINTL = StructType (
	name = "POINTL",
	definition = struct(WINDOWS_PACKAGE, "POINTL") {
		javaDoc("Contains the coordinates of a point.")
		nativeImport ("WindowsLWJGL.h")
		LONG.member("x")
		LONG.member("y")
	}
)

val DEVMODE = StructType(
	name = "DEVMODE",
	definition = struct(WINDOWS_PACKAGE, "DEVMODE") {
		javaDoc("Contains information about the initialization and environment of a printer or a display device.")
		nativeImport("WindowsLWJGL.h")
		TCHAR.member("dmDeviceName", "deviceName", 32, true)
		WORD.member("dmSpecVersion", "specVersion")
		WORD.member("dmDriverVersion", "driverVersion")
		WORD.member("dmSize", "size")
		WORD.member("dmDriverExtra", "driverExtra")
		DWORD.member("dmFields", "fields")
		//union {
		//struct {
		/*
		short.member("dmOrientation", "orientation")
		short.member("dmPaperSize", "paperSize")
		short.member("dmPaperLength", "paperLength")
		short.member("dmPaperWidth", "paperWidth")
		short.member("dmScale", "scale")
		short.member("dmCopies", "copies")
		short.member("dmDefaultSource", "defaultSource")
		short.member("dmPrintQuality", "printQuality")
		*/
		//}
		//struct {
		POINTL.member("dmPosition", "position")
		//DWORD.member("dmDisplayOrientation", "displayOrientation") // Not supported on win2k
		//DWORD.member("dmDisplayFixedOutput", "displayFixedOutput") // Not supported on win2k
		//}
		//}
		/*
		short.member("dmColor", "color")
		short.member("dmDuplex", "duplex")
		short.member("dmYResolution", "yResolution")
		short.member("dmTTOption", "ttOption")
		short.member("dmCollate", "collate")
		TCHAR.member("dmFormName", "formName", 32, true)
		*/
		WORD.member("dmLogPixels", "logPixels")
		DWORD.member("dmBitsPerPel", "bitsPerPel")
		DWORD.member("dmPelsWidth", "pelsWidth")
		DWORD.member("dmPelsHeight", "pelsHeight")
		//union {
		DWORD.member("dmDisplayFlags", "displayFlags")
		//DWORD.member("dmNup", "nup")
		//}
		DWORD.member("dmDisplayFrequency", "displayFrequency")
		/*
		DWORD.member("dmICMMethod", "icmMethod")
		DWORD.member("dmICMIntent", "icmIntent")
		DWORD.member("dmMediaType", "mediaType")
		DWORD.member("dmDitherType", "ditherType")
		*/

		/*
		DWORD.member("dmReserved1", "reserved1")
		DWORD.member("dmReserved2", "reserved2")

		DWORD.member("dmPanningWidth", "panningWidth")
		DWORD.member("dmPanningHeight", "panningHeight")
		*/
	}
)

val PDISPLAY_DEVICE = StructType(
	name = "PDISPLAY_DEVICE",
	includesPointer = true,
	definition = struct(WINDOWS_PACKAGE, "DISPLAY_DEVICE") {
		javaDoc("Receives information about the display device specified by the devNum parameter of the {@link WinUser#EnumDisplayDevices} function.")
		nativeImport("WindowsLWJGL.h")
		DWORD.member("cb")
		TCHAR.member(nativeName = "DeviceName", size = 32, nullTerminated = true)
		TCHAR.member(nativeName = "DeviceString", size = 128, nullTerminated = true)
		DWORD.member("StateFlags")
		TCHAR.member(nativeName = "DeviceID", size = 128, nullTerminated = true)
		TCHAR.member(nativeName = "DeviceKey", size = 128, nullTerminated = true)
	}
)

val GOBJENUMPROC = CallbackType(
	name = "GOBJENUMPROC",
	callback = int.func(
		WINDOWS_PACKAGE,
	    "EnumObjectsProc",
	    "An application-defined callback function used with the {@link WinGDI#EnumObjects} function. It is used to process the object data.",
		"CALLBACK",
		false,
	    LPVOID.IN("logObject", "a pointer to a {@link LOGPEN} or {@link LOGBRUSH} structure describing the attributes of the object."),
		CALLBACK_DATA _ LPARAM.IN("data", "a pointer to the application-defined data passed by the EnumObjects function.")
	).nativeImport("WindowsLWJGL.h")
)