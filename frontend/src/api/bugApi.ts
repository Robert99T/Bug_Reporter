import apiClient from "./client";
import type { UpdateBugRequest, CreateBugRequest, BugResponse as BugResponseType } from "../types";

export const getBugById = (id: number, userId?: number) =>
  apiClient.get<BugResponseType>(`/bugs/${id}`, {
    params: userId != null ? { userId } : undefined,
  });

export const updateBug = (id: number, data: UpdateBugRequest) =>
  apiClient.put<BugResponseType>(`/bugs/${id}`, data);

export const deleteBug = (id: number) =>
  apiClient.delete(`/bugs/${id}`);

export type BugResponse = BugResponseType;

export const getAllBugs = () => apiClient.get<BugResponseType[]>("/bugs");

export const createBug = (data: CreateBugRequest) =>
  apiClient.post<BugResponseType>("/bugs", data);