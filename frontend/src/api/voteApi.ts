import apiClient from "./client";
import type { VoteRequest, VoteResponse } from "../types";

export const voteBug = (bugId: number, data: VoteRequest) =>
  apiClient.post<VoteResponse>(`/bugs/${bugId}/votes`, data);

export const voteComment = (commentId: number, data: VoteRequest) =>
  apiClient.post<VoteResponse>(`/comments/${commentId}/votes`, data);

// Note: removeVote has been intentionally removed.
// The backend handles vote toggling via the POST endpoints above —
// calling POST with the same vote type again removes the vote (204 No Content).
