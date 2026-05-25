import apiClient from "./client";
import type { CurrentUser, RegisterRequest, UserResponse } from "../types";

export const loginUser = (username: string, password: string) =>
  apiClient.post<CurrentUser>("/auth/login", { username, password });

export const registerUser = (data: RegisterRequest) =>
  apiClient.post<UserResponse>("/users/register", data);