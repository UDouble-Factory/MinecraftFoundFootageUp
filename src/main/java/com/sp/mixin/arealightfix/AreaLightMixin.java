package com.sp.mixin.arealightfix;

// Removed: In Veil 1.21, AreaLightData.store() already implements the correct buffer layout
// (including the matrix, color, size, angle as short, distance, and occlusion fields).
// The fix this mixin provided is now part of the official implementation.
