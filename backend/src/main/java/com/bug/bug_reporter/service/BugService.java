    package com.bug.bug_reporter.service;

    import com.bug.bug_reporter.dto.BugResponse;
    import com.bug.bug_reporter.dto.CommentResponse;
    import com.bug.bug_reporter.dto.CreateBugRequest;
    import com.bug.bug_reporter.dto.UpdateBugRequest;
    import com.bug.bug_reporter.model.Bug;
    import com.bug.bug_reporter.model.Comment;
    import com.bug.bug_reporter.model.Tag;
    import com.bug.bug_reporter.model.User;
    import com.bug.bug_reporter.repository.BugRepository;
    import com.bug.bug_reporter.repository.TagRepository;
    import com.bug.bug_reporter.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.stereotype.Service;


    import java.time.LocalDateTime;
    import java.util.List;
    import java.util.Set;
    import java.util.stream.Collectors;

    import static com.bug.bug_reporter.utility.Utility.getOrCreateTag;

    @Service
    @RequiredArgsConstructor
    public class BugService {

        private final BugRepository bugRepository;
        private final UserRepository userRepository;
        private final TagRepository tagRepository;
        private final com.bug.bug_reporter.repository.BugVoteRepository bugVoteRepository;
        private final com.bug.bug_reporter.repository.CommentVoteRepository commentVoteRepository;
        private final UserService userService;


        public BugResponse createBug(CreateBugRequest request) {

            User author = userRepository.findById(request.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Set<Tag> tags = request.getTags().stream()
                    .map(name -> getOrCreateTag(name, tagRepository))
                    .collect(Collectors.toSet());

            Bug bug = Bug.builder()
                    .title(request.getTitle())
                    .text(request.getText())
                    .pictureUrl(request.getPictureUrl())
                    .status(request.getStatus())
                    .creationDate(LocalDateTime.now())
                    .author(author)
                    .tags(tags)
                    .build();

            Bug savedBug = bugRepository.save(bug);

            return mapToBugResponse(savedBug, null);
        }

        public List<BugResponse> getAllBugs(Long userId, String search, String tag, Long authorId) {
            return bugRepository.findAll()
                    .stream()
                    .filter(bug -> {
                        if (search != null && !search.isBlank()) {
                            String searchLower = search.toLowerCase();
                            if (!bug.getTitle().toLowerCase().contains(searchLower)) {
                                return false;
                            }
                        }
                        if (tag != null && !tag.isBlank()) {
                            if (bug.getTags() == null || bug.getTags().stream().noneMatch(t -> t.getName().equalsIgnoreCase(tag))) {
                                return false;
                            }
                        }
                        if (authorId != null) {
                            if (bug.getAuthor() == null || !bug.getAuthor().getId().equals(authorId)) {
                                return false;
                            }
                        }
                        return true;
                    })
                    .map(bug -> mapToBugResponse(bug, userId))
                    .toList();
        }

        public List<String> getAllTags() {
            return tagRepository.findAll()
                    .stream()
                    .map(Tag::getName)
                    .toList();
        }

        public BugResponse getBugById(Long id, Long userId) {
            Bug bug = bugRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

            return mapToBugResponse(bug, userId);
        }

        public BugResponse updateBug(Long id, UpdateBugRequest request) {
            Bug bug = bugRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

            if (request.getTitle() != null && !request.getTitle().isBlank()) {
                bug.setTitle(request.getTitle());
            }

            if (request.getText() != null && !request.getText().isBlank()) {
                bug.setText(request.getText());
            }

            if (request.getPictureUrl() != null) {
                bug.setPictureUrl(request.getPictureUrl());
            }

            if (request.getStatus() != null && !request.getStatus().isBlank()) {
                bug.setStatus(request.getStatus());
            }

            Bug updatedBug = bugRepository.save(bug);
            return mapToBugResponse(updatedBug, null);
        }

        public void deleteBug(Long id) {
            Bug bug = bugRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Bug not found with id: " + id));

            bugRepository.delete(bug);
        }

        private BugResponse mapToBugResponse(Bug bug, Long userId) {
            BugResponse response = new BugResponse();
            response.setId(bug.getId());
            response.setTitle(bug.getTitle());
            response.setText(bug.getText());
            response.setCreationDate(bug.getCreationDate());
            response.setPictureUrl(bug.getPictureUrl());
            response.setStatus(bug.getStatus());
            response.setTags(
                    bug.getTags()
                            .stream()
                            .map(Tag::getName)
                            .collect(Collectors.toSet())
            );
            if (bug.getAuthor() != null) {
                response.setAuthorId(bug.getAuthor().getId());
                response.setAuthorUsername(bug.getAuthor().getUsername());
                response.setAuthorScore(userService.calculateUserScore(bug.getAuthor().getId()));
            }

            Integer voteScore = bugVoteRepository.getVoteScoreByBugId(bug.getId());
            response.setVoteScore(voteScore != null ? voteScore : 0);

            if (userId != null) {
                bugVoteRepository.findByUserIdAndBugId(userId, bug.getId())
                        .ifPresent(vote -> response.setUserVote(vote.getVoteType() == 1 ? "UPVOTE" : "DOWNVOTE"));
            }

            if (bug.getComments() != null) {
                List<CommentResponse> comments = bug.getComments()
                        .stream()
                        .map(c -> mapToCommentResponse(c, userId))
                        .toList();
                response.setComments(comments);
            }

            return response;
        }

        private CommentResponse mapToCommentResponse(Comment comment, Long userId) {
            CommentResponse response = new CommentResponse();
            response.setId(comment.getId());
            response.setText(comment.getText());
            response.setPictureUrl(comment.getPictureUrl());
            response.setCreationDate(comment.getCreationDate());

            if (comment.getAuthor() != null) {
                response.setAuthorId(comment.getAuthor().getId());
                response.setAuthorUsername(comment.getAuthor().getUsername());
                Integer bugScore = bugVoteRepository.getAuthorVoteScore(comment.getAuthor().getId());
                Integer commentScore = commentVoteRepository.getAuthorVoteScore(comment.getAuthor().getId());
                double authorScore = (double) ((bugScore != null ? bugScore : 0) + (commentScore != null ? commentScore : 0));
                response.setAuthorScore(authorScore);
            }

            if (comment.getBug() != null) {
                response.setBugId(comment.getBug().getId());
            }

            Integer voteScore = commentVoteRepository.getVoteScoreByCommentId(comment.getId());
            response.setVoteScore(voteScore != null ? voteScore : 0);

            if (userId != null) {
                commentVoteRepository.findByUserIdAndCommentId(userId, comment.getId())
                        .ifPresent(vote -> response.setUserVote(vote.getVoteType() == 1 ? "UPVOTE" : "DOWNVOTE"));
            }

            return response;
        }
    }
