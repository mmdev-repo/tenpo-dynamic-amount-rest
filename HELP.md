# Fintech Percentage Sum Service
A RESTful API for a fintech application that receives two numeric inputs, adds them, and applies a dynamic percentage retrieved from an external service. It features Redis caching, retry mechanisms, and PostgreSQL storage for call history.

## Features
- **Add Two Numbers**: Accepts two input numbers and returns their sum.
- **Dynamic Percentage**: Queries an external service to get a percentage, which is applied to the sum.
- **Caching with Redis**: Caches the percentage value for 30 minutes to avoid repeated external calls.
- **Retry Logic**: Retries up to 3 times if the external service fails.
- **Fallback**: Uses the cached percentage if available; otherwise, returns an appropriate error.
- **Call History**: Stores all API requests and results in a PostgreSQL database.
- **Dockerized Deployment**: Runs fully in Docker using `docker-compose`.
- **API Documentation**: Available via Swagger UI.

## Tech Stack

- Java 21
- Spring Boot
- Redis (cache)
- PostgreSQL (history storage)
- Docker & Docker Compose
- Swagger for API documentation

## Getting Started

### Prerequisites

- Docker and Docker Compose installed

### 1. Clone the Repository
git clone https://github.com/your-username/fintech-sum-service.git

### 2. Clone the Repository