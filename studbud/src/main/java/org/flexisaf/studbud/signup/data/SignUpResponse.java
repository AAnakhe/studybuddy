package org.flexisaf.studbud.signup.data;

public record SignUpResponse(
        String fullName,
        String email,
        String phone
) {
}
