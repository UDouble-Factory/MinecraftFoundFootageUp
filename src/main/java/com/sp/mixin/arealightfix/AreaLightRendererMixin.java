package com.sp.mixin.arealightfix;

// Removed: In Veil 1.21, AreaLightRenderer already uses the correct lightSize (Float.BYTES * 23 + 2)
// and setupBufferState now uses VertexArrayBuilder API with the correct 9-attribute layout.
// The old GL-call-based fix is superseded by the official implementation.
