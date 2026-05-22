describe("Login Page", () => {
  beforeEach(() => {
    cy.visit("/login");
  });

  it("renders login form", () => {
    cy.get(".login-card").should("be.visible");
    cy.get(".login-heading").should("have.text", "Login");
    cy.get(".login-input[type=text]").should("have.attr", "required");
    cy.get(".login-input[type=password]").should("have.attr", "required");
    cy.get(".login-submit").should("have.text", "Login");
  });

  it("toggles to register mode", () => {
    cy.get(".login-toggle-button").contains("Create account").click();
    cy.get(".login-heading").should("have.text", "Creează cont");
    cy.get(".login-input[type=email]").should("be.visible");
    cy.get(".login-submit").should("have.text", "Create account");
  });

  it("shows error on failed login", () => {
    cy.intercept("POST", "http://localhost:8080/auth/login", {
      statusCode: 401,
      body: {},
    }).as("loginFail");

    cy.get(".login-input[type=text]").type("wronguser");
    cy.get(".login-input[type=password]").type("wrongpass");
    cy.get(".login-submit").click();

    cy.wait("@loginFail");
    cy.get(".login-error").should("be.visible");
  });

  it("redirects to / on successful login", () => {
    cy.intercept("POST", "http://localhost:8080/auth/login", {
      statusCode: 200,
      body: { id: 1, username: "testuser", role: "USER" },
    }).as("loginSuccess");

    cy.get(".login-input[type=text]").type("testuser");
    cy.get(".login-input[type=password]").type("password123");
    cy.get(".login-submit").click();

    cy.wait("@loginSuccess");
    cy.url().should("eq", Cypress.config().baseUrl + "/");
  });
});
