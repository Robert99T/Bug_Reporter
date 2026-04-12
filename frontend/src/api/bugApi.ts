import apiClient from "./client";
import type { BugResponse, UpdateBugRequest } from "../types";

export const getBugById = (id: number, userId?: number) =>
  apiClient.get<BugResponse>(`/bugs/${id}`, {
    params: userId != null ? { userId } : undefined,
  });

export const updateBug = (id: number, data: UpdateBugRequest) =>
  apiClient.put<BugResponse>(`/bugs/${id}`, data);

export const deleteBug = (id: number) =>
  apiClient.delete(`/bugs/${id}`);
