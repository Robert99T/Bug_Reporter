// ─── Enums ───────────────────────────────────────────────────────────

export type BugStatus = "OPEN" | "IN_PROGRESS" | "SOLVED";

export type UserRole = "USER" | "MODERATOR";

export type VoteType = "UPVOTE" | "DOWNVOTE";

export type VoteTargetType = "BUG" | "COMMENT";

// ─── Auth / Context ──────────────────────────────────────────────────

export interface CurrentUser {
  id: number;
  username: string;
  role: UserRole;
}

// ─── User DTO ────────────────────────────────────────────────────────

export interface UserResponse {
  id: number;
  username: string;
  email: string;
  phoneNumber?: string | null;
  score: number;
  role: UserRole;
}

// ─── Vote DTOs ───────────────────────────────────────────────────────

export interface VoteRequest {
  userId: number;
  voteType: VoteType;
}

export interface VoteResponse {
  id: number;
  targetType: VoteTargetType;
  targetId: number;
  userId: number;
  voteType: VoteType;
}

// ─── Comment DTOs ────────────────────────────────────────────────────

export interface CommentResponse {
  id: number;
  text: string;
  creationDate: string;
  authorId: number;
  authorUsername: string;
  authorScore: number;
  bugId: number;
  pictureUrl?: string | null;
  voteScore: number;
  userVote?: VoteType | null;
}

export interface CreateCommentRequest {
  text: string;
  authorId: number;
  pictureUrl?: string;
}

export interface UpdateCommentRequest {
  text?: string;
  pictureUrl?: string;
}

// ─── Bug DTOs ────────────────────────────────────────────────────────

export interface BugResponse {
  id: number;
  title: string;
  text: string;
  creationDate: string;
  status: BugStatus;
  authorId: number;
  authorUsername: string;
  authorScore: number;
  pictureUrl?: string | null;
  tags?: string[];
  comments?: CommentResponse[];
  voteScore: number;
  userVote?: VoteType | null;
}

export interface UpdateBugRequest {
  title?: string;
  text?: string;
  pictureUrl?: string;
  status?: BugStatus;
  tags?: string[];
}
