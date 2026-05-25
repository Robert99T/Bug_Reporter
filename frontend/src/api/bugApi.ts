import apiClient from "./client";
import type { UpdateBugRequest, CreateBugRequest, BugResponse } from "../types";

// ─── Filter parameters for GET /bugs ─────────────────────────────────
export interface BugFilterParams {
  userId?: number;
  search?: string;
  tag?: string;
  authorId?: number;
}

export const getAllBugs = (filters?: BugFilterParams) =>
  apiClient.get<BugResponse[]>("/bugs", {
    params: filters,
  });

export const getBugById = (id: number, userId?: number) =>
  apiClient.get<BugResponse>(`/bugs/${id}`, {
    params: userId != null ? { userId } : undefined,
  });

export const createBug = (data: CreateBugRequest) =>
  apiClient.post<BugResponse>("/bugs", data);

export const updateBug = (id: number, data: UpdateBugRequest) =>
  apiClient.put<BugResponse>(`/bugs/${id}`, data);

export const deleteBug = (id: number) =>
  apiClient.delete(`/bugs/${id}`);