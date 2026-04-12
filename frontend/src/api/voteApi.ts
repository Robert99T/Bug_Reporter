import apiClient from "./client";
import type { VoteRequest, VoteResponse } from "../types";

export const voteBug = (bugId: number, data: VoteRequest) =>
  apiClient.post<VoteResponse>(`/bugs/${bugId}/votes`, data);

export const voteComment = (commentId: number, data: VoteRequest) =>
  apiClient.post<VoteResponse>(`/comments/${commentId}/votes`, data);

export const removeVote = (voteId: number) =>
  apiClient.delete(`/votes/${voteId}`);
