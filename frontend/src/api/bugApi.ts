import apiClient from "./client";
import type { BugResponse, UpdateBugRequest } from "../types";
import type { CreateBugRequest } from "../types";

export const getBugById = (id: number, userId?: number) =>
  apiClient.get<BugResponse>(`/bugs/${id}`, {
    params: userId != null ? { userId } : undefined,
  });

export const updateBug = (id: number, data: UpdateBugRequest) =>
  apiClient.put<BugResponse>(`/bugs/${id}`, data);

export const deleteBug = (id: number) =>
  apiClient.delete(`/bugs/${id}`);



  export interface CommentResponse {
    id: number;
    text: string;
    pictureUrl?: string | null;
    creationDate: string;
    authorId: number;
    authorUsername: string;
    bugId: number;
  }

  export interface BugResponse {
    id: number;
    title: string;
    text: string;
    creationDate: string;
    pictureUrl?: string | null;
    status: "OPEN" | "IN_PROGRESS" | "SOLVED";
    authorId: number;
    authorUsername: string;
    comments?: CommentResponse[];
  }

  export const getAllBugs = () => apiClient.get<BugResponse[]>("/bugs");

  export const createBug = (data: CreateBugRequest) =>
    apiClient.post<BugResponse>("/bugs", data);
