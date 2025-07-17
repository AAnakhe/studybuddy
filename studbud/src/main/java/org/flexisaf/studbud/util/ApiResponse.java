package org.flexisaf.studbud.util;

public record ApiResponse(Boolean success, Object data, String message) {}
