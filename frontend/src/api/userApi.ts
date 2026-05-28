import apiClient from "./client";
import type { UserResponse } from "../types";

// ─── User Request DTO (for updates) ──────────────────────────────────
export interface UserRequestDTO {
  username: string;
  email: string;
  password: string;
  phoneNumber?: string;
}

export interface UserScoreResponse {
  userId: number;
  score: number;
}

// ─── Endpoints ───────────────────────────────────────────────────────

export const getAllUsers = () =>
  apiClient.get<UserResponse[]>("/users");

export const getUserById = (id: number) =>
  apiClient.get<UserResponse>(`/users/${id}`);

export const getUserScore = (id: number) =>
  apiClient.get<UserScoreResponse>(`/users/${id}/score`);

export const updateUser = (id: number, data: UserRequestDTO) =>
  apiClient.put<UserResponse>(`/users/${id}`, data);

export const deleteUser = (id: number) =>
  apiClient.delete<UserResponse>(`/users/${id}`);
