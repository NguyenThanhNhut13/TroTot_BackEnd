# TroTot - Boarding House Management & Search Platform

## 📋 Table of Contents

- General Introduction
- Architecture
- Microservice Components
- Key Features
- Technical Highlights
- System Requirements & Installation
- API Usage & Testing
- Deployment
- Development

## 🌐 General Introduction

TroTot is a comprehensive microservices-based platform designed to connect landlords and tenants through an intelligent room rental system. The platform features AI-powered room recommendations, natural language search capabilities, and a robust user management system. Its goal is to connect tenants with landlords and automate many operations for optimal experience.

## 🏛 Architecture

The platform follows a microservices architecture with the following components:

- **API Gateway**: Fronts all client requests and routes them to appropriate services
- **Service Discovery**: Dynamic registration and lookup of services using Eureka
- **Config Server**: Centralized configuration management
- **Message Broker**: Kafka for asynchronous communication
- **Dedicated Databases**: Isolated databases for each service for scalability

> ![System Architecture](https://github.com/NguyenThanhNhut13/TroTot_BackEnd/blob/main/Microservice-Architecture.svg)  
_(You should add an architecture diagram to your repo and update this link)_

## 🔧 Microservice Components

| Service | Description | Port |
|---------|-------------|------|
| **api-gateway** | Entry point for all requests, handles routing, security, rate-limiting, and logging | 8222 |
| **auth-service** | Handles user authentication, JWT, refresh tokens, registration/login | 8050 |
| **user-service** | Manages user profiles, roles, and information | 8090 |
| **room-service** | Manages room information and listings | 8070 |
| **address-service** | Manages address data, links with room-service | 8060 |
| **media-service** | Handles media upload/storage (room photos/videos) | 8000 |
| **notification-service** | Manages notifications (email, push) | 8020 |
| **payment-service** | Payment processing, integrates with e-wallets/banks | 8040 |
| **chatbot-service** | AI-powered user assistant, helps find rooms and answers FAQs | 5002 |
| **recommendation-service** | Suggests suitable room listings to users (AI/ML) | 5000 |
| **review-service** | Manages reviews and comments for rooms and users | 8030 |
| **report-service** | Handles reporting of inappropriate listings or users | 8010 |
| **config-server** | Centralized configuration service | 8888 |
| **discovery** | Service registry/discovery (Eureka) | 8761 |

### Service Details

#### 1. **api-gateway**
- **Role:** Entry point for all requests, handles routing, security, rate-limiting, and logging.
- **Tech stack:** Java (Spring Cloud Gateway), Docker.
- **Main APIs:** `/api/v1/...`  
- **Depends on:** All backend microservices.

#### 2. **auth-service**
- **Role:** Handles user authentication, JWT, refresh tokens, registration/login.
- **Tech stack:** Java (Spring Boot/Spring Security), Docker, Database (MySQL/PostgreSQL).
- **APIs:** `/auth/login`, `/auth/register`, ...
- **Depends on:** user-service, config-server.

#### 3. **user-service**
- **Role:** Manages user profiles, roles, and information.
- **Tech stack:** Java (Spring Boot), Docker, Database.
- **APIs:** users, `/users/{id}`
- **Depends on:** auth-service.

#### 4. **room-service**
- **Role:** Manages room information and listings.
- **Tech stack:** Java (Spring Boot), Docker, Database.
- **APIs:** `/rooms`, `/rooms/{id}`

#### 5. **address-service**
- **Role:** Manages address data, links with room-service.
- **Tech stack:** Java, Database.
- **APIs:** `/addresses`, `/addresses/{id}`

#### 6. **media-service**
- **Role:** Handles media upload/storage (room photos/videos).
- **Tech stack:** Java/Python, S3 or local storage.
- **APIs:** `/media/upload`, `/media/{id}`

#### 7. **notification-service**
- **Role:** Manages notifications (email, push).
- **Tech stack:** Java/Python, SMTP/third-party.
- **APIs:** `/notifications`

#### 8. **payment-service**
- **Role:** Payment processing, integrates with e-wallets/banks.
- **Tech stack:** Java/Python, third-party APIs.
- **APIs:** `/payments`

#### 9. **chatbot-service**
- **Role:** AI-powered user assistant, helps find rooms and answers FAQs.
- **Tech stack:** Python (AI/ML), Java, NLP, Google Gemini.
- **APIs:** `/chat`

#### 10. **recommendation-service**
- **Role:** Suggests suitable room listings to users (AI/ML).
- **Tech stack:** Python, Java, scikit-learn.
- **APIs:** `/recommendations`

#### 11. **review-service**
- **Role:** Manages reviews and comments for rooms and users.
- **Tech stack:** Java, Database.
- **APIs:** `/reviews`

#### 12. **report-service**
- **Role:** Handles reporting of inappropriate listings or users.
- **Tech stack:** Java, Database.
- **APIs:** `/reports`

#### 13. **config-server**
- **Role:** Centralized configuration service.
- **Tech stack:** Spring Cloud Config, Docker.
- **APIs:** `/config`

#### 14. **discovery**
- **Role:** Service registry/discovery (Eureka).
- **Tech stack:** Spring Cloud Netflix Eureka.
- **APIs:** `/eureka`

## 🌟 Key Features

### For Tenants
- Personalized room recommendations
- Natural language search via AI chatbot
- Advanced filtering by location, price, and amenities
- Detailed room information with photos and video reviews
- Save favorite rooms to wishlist
- Review and rating system
- Secure authentication and profile management
### For Landlords
- Easy room listing management
- Post new rooms quickly and efficiently
- Target audience specification
- Add video reviews to listings after room creation
- View analytics and reports
- Receive notifications for inquiries
- Manage payments and transactions securely
- Purchase and manage post slots for featured listings

### Administrative Features
- User and content moderation
- Report management
- System monitoring and analytics

## 💻 Technical Highlights

### Backend Technologies
- **Java/Spring Boot**: Core microservices implementation
- **Python/FastAPI**: AI and ML services
- **Redis**: Caching and session management
- **JPA/Hibernate**: Database ORM
- **Docker**: Containerization for deployment

### AI and Machine Learning
- **Recommendation System**:
  - Content-based filtering: Suggests rooms similar to those the user has viewed
  - Collaborative filtering: Suggests rooms based on similar users' preferences
- **Natural Language Search**: Google Gemini-powered chatbot for conversational room search

### System Integration
- **Feign Client**: Service-to-service communication
- **Spring Cloud**: Microservices infrastructure
- **Resilience4j**: Circuit breaking, rate limiter, retry, and fault tolerance
- **Kafka**: Event-driven communication

### Security
- **JWT Authentication**: Secure token-based authentication
- **OTP Verification**: Multi-factor authentication via email/SMS
- **Role-Based Access Control**: Granular permission management

## 🚀 System Requirements & Installation

### Prerequisites

- Java JDK 17+
- Docker & Docker Compose
- Maven/Gradle
- Python 3.8+ (for Python services)
- Git

### Environment Configuration

- Create a `.env` file at the project root and set required environment variables (DB_URL, JWT_SECRET, MAIL_CONFIG, etc).
- See `.env.example` if provided (consider adding this file for reference).

### Installation Steps

1. **Clone the repository**
   ```bash
   git clone https://github.com/NguyenThanhNhut13/TroTot_BackEnd.git
   cd TroTot_BackEnd
   ```

2. **Build each service (if required)**
   ```bash
   cd <service-name>
   mvn clean install      # For Java services
   # or
   pip install -r requirements.txt  # For Python services
   ```

3. **Run the entire system with Docker Compose**
   ```bash
   docker-compose up --build
   ```

4. **Access services**
   - API Gateway: http://localhost:8222
   - Eureka Dashboard: http://localhost:8761
   - Swagger UI (if available): http://localhost:8222/swagger-ui.html

### Manual Setup for Development

For developing individual services:

1. Start infrastructure services first
   ```bash
   docker-compose up -d config-server discovery
   ```

2. Run any specific service
   ```bash
   cd service-name
   ./mvnw spring-boot:run
   ```

   For Python services:
   ```bash
   cd service-name
   python -m venv venv
   source venv/bin/activate  # On Windows: venv\Scripts\activate
   pip install -r requirements.txt
   python -m src.main
   ```

## 📚 API Usage & Testing

### Example API Calls

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin@gmail.com",
  "password": "admin"
}
```

```http
GET /api/v1/rooms
```
- No authentication required.
- Returns a list of available rooms.
- Optionally, you can include user info in the request (e.g., via query params or headers) to receive personalized recommendations.
- Example response:
```json
[
    {
        "id": 70,
        "title": "Nhà Trọ Hẻm 177A/27 Nguyễn Văn Luông, Phường 11, Quận 6, Thành phố Hồ Chí Minh",
        "price": 1500000,
        "area": 17,
        "roomType": "BOARDING_HOUSE",
        "imageUrls": [
        "https://res.cloudinary.com/dkp293f4i/image/upload/v1748882275/vbqwwplqzv6dbv2szdf4.jpg"
        ],
        "district": "Quận 6",
        "province": "Hồ Chí Minh"
    },
    ...
]
```

### API Documentation

- Swagger UI for each service is available at `http://localhost:<service-port>/swagger-ui.html`
- API Gateway provides access to all services at `http://localhost:8222/api/v1/<service>`

### Testing

- Run unit tests for each service:
  ```bash
  mvn test         # For Java
  pytest           # For Python
  ```

## 🌩️ Deployment

The system is containerized for easy deployment:

### Using Docker Compose (Development)
```bash
docker-compose up -d
```

### CI/CD with Jenkins (Production)
The project includes a Jenkins pipeline for automated deployment:

- Pipeline defined in Jenkinsfile
- Detects changed services using detect-changes.ps1
- Builds Docker images for modified services
- Pushes images to DockerHub repository
- Deploys to Render.com cloud platform

### Configuration Management
- SSL certificate management using convert-certs.sh
- Environment-specific configuration through Spring Cloud Config

## 🧪 Development

### Adding a new feature

1. Identify the service to modify
2. Create a new branch from `main`
3. Implement the feature
4. Write tests
5. Create a pull request

### Running tests

```bash
# For Java services
cd service-name
./mvnw test

# For Python services
cd service-name
pytest
```

**Repository:** https://github.com/NguyenThanhNhut13/TroTot_BackEnd