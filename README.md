# Ecommerce Backend API

A Spring Boot REST API for an ecommerce platform, featuring JWT authentication, role-based access control, and relational order management with real-time inventory tracking.

Built to apply backend engineering concepts in a domain I already understood from working in L3 technical support for a production ecommerce system.

## Features

- **User authentication** — registration and login with BCrypt password hashing and JWT tokens
- **Role-based access control** — admin-only actions (e.g., product deletion) enforced via Spring Security
- **Product & Category management** — full CRUD with input validation
- **Order placement** — validates stock, calculates totals server-side, snapshots price at time of purchase, and atomically deducts inventory
- **User-scoped data access** — users can only view their own order history
- **Clean API responses** — DTOs used to prevent leaking sensitive fields (e.g., password hashes) or internal data (e.g., stock counts) in API responses

## Tech Stack

- Java 17, Spring Boot 3
- Spring Data JPA (Hibernate)
- Spring Security + JWT (jjwt)
- MySQL
- Maven
- Lombok

## Architecture
Controller → Repository → MySQL


Entities: `User`, `Product`, `Category`, `Order`, `OrderItem`

- `Product` ↔ `Category`: Many-to-One
- `Order` ↔ `User`: Many-to-One
- `Order` ↔ `OrderItem`: One-to-Many (cascade)
- `OrderItem` ↔ `Product`: Many-to-One

## API Endpoints

| Method | Endpoint | Auth Required | Description |
|--------|----------|----------------|--------------|
| POST | `/auth/register` | No | Register a new user |
| POST | `/auth/login` | No | Login, returns JWT |
| GET | `/products` | Yes | List all products |
| POST | `/products` | Yes | Create a product |
| PUT | `/products/{id}` | Yes | Update a product |
| DELETE | `/products/{id}` | Admin only | Delete a product |
| GET | `/categories` | Yes | List all categories |
| POST | `/categories` | Admin only | Create a category |
| POST | `/orders` | Yes | Place an order |
| GET | `/orders` | Yes | View your own order history |

## Running Locally

1. Clone the repo
2. Create a MySQL database named `ecommerce_db`
3. Update `src/main/resources/application.properties` with your MySQL credentials
4. Run the app: mvn spring-boot:run

5. App runs on `http://localhost:8080`

## Known Limitations / Next Steps

- No dedicated Service layer yet — business logic currently lives in Controllers; a refactor to extract Services would improve testability and separation of concerns
- Stock deduction is not yet protected against race conditions under concurrent load — would require `@Transactional` with an atomic conditional update (`UPDATE ... WHERE stock >= ?`) to prevent overselling
- No pagination on list endpoints yet — would add `Pageable` support for scalability with large datasets

## What I Learned

Building this project deepened my understanding of relational data modeling, stateless authentication with JWT, and the kinds of concurrency and data-exposure issues that come up in real production systems — several of which (like the password-leak fix and the update-not-copying-all-fields bug) I found and fixed myself while testing.