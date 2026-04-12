import apiClient from "./client";
import type {
  CommentResponse,
  CreateCommentRequest,
  UpdateCommentRequest,
} from "../types";

export const getCommentsByBugId = (bugId: number, userId?: number) =>
  apiClient.get<CommentResponse[]>(`/bugs/${bugId}/comments`, {
    params: userId != null ? { userId } : undefined,
  });

export const createComment = (bugId: number, data: CreateCommentRequest) =>
  apiClient.post<CommentResponse>(`/bugs/${bugId}/comments`, data);

export const updateComment = (id: number, data: UpdateCommentRequest) =>
  apiClient.put<CommentResponse>(`/comments/${id}`, data);

export const deleteComment = (id: number) =>
  apiClient.delete(`/comments/${id}`);
