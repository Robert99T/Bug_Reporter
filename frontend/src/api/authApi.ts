import apiClient from "./client";
import type { CurrentUser, RegisterRequest } from "../types";

export interface RegisterResponse {
  id: number;
  username: string;
  email: string;
  phoneNumber?: string | null;
  score: number;
  role: string;
}

export const loginUser = (username: string, password: string) =>
  apiClient.post<CurrentUser>("/auth/login", { username, password });

export const registerUser = (data: RegisterRequest) =>
  apiClient.post<RegisterResponse>("/users/register", data);