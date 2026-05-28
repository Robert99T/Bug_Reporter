import apiClient from "./client";

export const banUser = (userId: number) =>
  apiClient.post<{ message: string }>(`/moderation/users/${userId}/ban`);

export const unbanUser = (userId: number) =>
  apiClient.post<{ message: string }>(`/moderation/users/${userId}/unban`);
