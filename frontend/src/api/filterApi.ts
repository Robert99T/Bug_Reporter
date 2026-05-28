import apiClient from "./client";
import type { UserResponse } from "../types";

export const getAllTags = () =>
  apiClient.get<string[]>("/api/tags");

export const getAllUsersForFilter = () =>
  apiClient.get<UserResponse[]>("/api/users");
