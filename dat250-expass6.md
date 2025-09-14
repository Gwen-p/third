# DAT250: Software Technology Experiment Assignment 3 - Report

## Project Overview

For this assignment, I refactored the domain model and test implementation based on a better understanding of the class relationships from the UML diagram provided in the first task. The GitHub examples from the course lectures were particularly helpful in guiding these improvements.

**GitHub Repository for the previous project**: [https://github.com/Gwen-p/DAT250_expass2](https://github.com/Gwen-p/DAT250_expass2)  
**GitHub Repository for the current project**: [https://github.com/Gwen-p/third](https://github.com/Gwen-p/third)

## Technical Implementation

### Domain Model
The application implements the following domain model:

- **User**: Represents users with a username (used as ID) and email. Users can create polls and vote.
- **Poll**: Represents voting polls containing questions, a validity period, and voting options.
- **VoteOption**: Individual options within a poll, each with a caption and presentation order.
- **Vote**: Records user votes with timestamps and associations to both users and vote options.

### Code Structure
The project is organized with clear separation between entities, services, and REST endpoints. JPA annotations define the relationships between entities (e.g., `@OneToMany`, `@ManyToOne`), ensuring proper persistence and retrieval.

### Key Features
- **RESTful API**: Endpoints for creating polls, adding vote options, submitting votes, and retrieving poll results.
- **Persistence**: Using JPA with Hibernate as the ORM provider. Database configuration is handled via `persistence.xml`.
- **Testing**: JUnit tests verify the correctness of the domain model and API endpoints.

## Technical Problems Encountered

### 1. Deciding the Logic for User Login
**Problem**: Initially, I was uncertain about how to handle user authentication and session management. The assignment did not specify whether to implement a full login system or use a simpler approach.

**Solution**: After reviewing the requirements, I realized that a lightweight approach would suffice. I used the username as a unique identifier and did not implement password-based authentication. Each vote is associated with a user via their username, ensuring that users can only vote once per poll (as required).

### 2. Mapping Relationships in JPA
**Problem**: Correctly defining the relationships between `User`, `Poll`, `VoteOption`, and `Vote` was challenging. I initially struggled with bidirectional mappings and cascade operations.

**Solution**: I studied the lecture examples and adjusted the mappings accordingly. For instance:
- `Poll` has a `@OneToMany` relationship with `VoteOption`.
- `Vote` has `@ManyToOne` relationships with both `User` and `VoteOption`.
- Cascade settings were configured to propagate operations where necessary (e.g., persisting `VoteOption` when a `Poll` is persisted).

## Pending Issues
- Improve the frontend appearance: The current UI is functional but basic. Enhancing the visual design would improve user experience.
- Add input validation: Additional checks for user inputs (e.g., duplicate usernames, invalid dates) would make the application more robust.
- Implement pagination: For retrieving polls or votes, pagination would be useful when the data grows.

## Conclusion
The lectures and GitHub examples provided valuable guidance for understanding JPA relationships and REST implementation. The refactored domain model now accurately reflects the intended design, and the application meets the core requirements. Future work will focus on enhancing the UI and adding validation features.