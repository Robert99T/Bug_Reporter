describe("Bug List & Details", () => {
  const login = () => {
    cy.intercept("POST", "http://localhost:8080/auth/login", {
      statusCode: 200,
      body: { id: 1, username: "testuser", role: "USER" },
    }).as("login");
    cy.visit("/login");
    cy.get(".login-input[type=text]").type("testuser");
    cy.get(".login-input[type=password]").type("password123");
    cy.get(".login-submit").click();
    cy.wait("@login");
    cy.url().should("eq", Cypress.config().baseUrl + "/");
  };

  beforeEach(() => {
    cy.intercept("GET", "http://localhost:8080/api/tags", { body: [] });
    cy.intercept("GET", "http://localhost:8080/api/users", { body: [] });
  });

  it("shows bug list after login", () => {
    cy.intercept("GET", "http://localhost:8080/bugs*", {
      statusCode: 200,
      body: [
        { id: 1, title: "Login button broken", text: "Clicking login does nothing", creationDate: "2025-01-15T10:00:00Z", status: "OPEN", authorId: 1, authorUsername: "alice", voteScore: 5, userVote: null, tags: ["ui"], comments: [] },
        { id: 2, title: "API timeout", text: "GET /bugs takes >30s", creationDate: "2025-01-16T08:00:00Z", status: "IN_PROGRESS", authorId: 2, authorUsername: "bob", voteScore: 3, userVote: "UPVOTE", tags: ["performance"], comments: [] },
      ],
    }).as("getBugs");

    login();
    cy.wait("@getBugs");
    cy.get(".bug-card").should("have.length", 2);
    cy.get(".bug-title").first().should("have.text", "Login button broken");
  });

  it("shows empty state when no bugs", () => {
    cy.intercept("GET", "http://localhost:8080/bugs*", {
      statusCode: 200,
      body: [],
    }).as("getBugsEmpty");

    login();
    cy.wait("@getBugsEmpty");
    cy.contains("No bugs found.");
  });

  it("navigates to bug details via card click", () => {
    cy.intercept("GET", "http://localhost:8080/bugs*", {
      statusCode: 200,
      body: [
        { id: 1, title: "Login button broken", text: "Text", creationDate: "2025-01-15T10:00:00Z", status: "OPEN", authorId: 1, authorUsername: "alice", voteScore: 5, userVote: null, tags: ["ui"], comments: [] },
      ],
    }).as("getBugs");
    cy.intercept("GET", "http://localhost:8080/bugs/1*", {
      statusCode: 200,
      body: { id: 1, title: "Login button broken", text: "Text", creationDate: "2025-01-15T10:00:00Z", status: "OPEN", authorId: 1, authorUsername: "alice", authorScore: 10, voteScore: 5, userVote: null, tags: ["ui"] },
    }).as("getBug");
    cy.intercept("GET", "http://localhost:8080/bugs/1/comments*", {
      statusCode: 200,
      body: [],
    }).as("getComments");

    login();
    cy.wait("@getBugs");
    cy.get(".bug-card").first().click();
    cy.wait("@getBug");
    cy.wait("@getComments");
    cy.url().should("include", "/bugs/1");
    cy.contains("Login button broken");
  });
});
