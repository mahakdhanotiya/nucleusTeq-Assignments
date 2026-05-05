import { fetchHandler } from "../lib/handlers/fetch.js";

export async function signIn(email, password) {
  return fetchHandler("/auth/login", {
    method: "POST",
    body: { email, password }
  });
}

export async function signUp(fullName, email, mobileNumber, dateOfBirth, gender, role) {
  return fetchHandler("/auth/register", {
    method: "POST",
    body: {
      fullName,
      email,
      mobileNumber,
      dateOfBirth: dateOfBirth || null,
      gender: gender || null,
      role
    }
  });
}

export async function setPassword(token, password) {
  return fetchHandler("/auth/set-password", {
    method: "POST",
    body: { token, password }
  });
}

export async function getMe() {
  return fetchHandler("/auth/me", {
    method: "GET",
    requireAuth: true
  });
}
