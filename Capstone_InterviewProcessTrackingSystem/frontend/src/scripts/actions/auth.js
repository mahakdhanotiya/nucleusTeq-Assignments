import { fetchHandler } from "../lib/handlers/fetch.js";

export async function signIn(email, password) {
  return fetchHandler("/auth/login", {
    method: "POST",
    body: { email, password }
  });
}

export async function signUp(fullName, email, password, role) {
  return fetchHandler("/auth/register", {
    method: "POST",
    body: { fullName, email, password, role }
  });
}
