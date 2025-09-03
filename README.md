# RoomieHub - University Student Housing Platform

A trusted platform connecting verified university students for safe housing exchanges and rentals within campus communities.

## Overview

RoomieHub allows verified university students to:
- **Find Housing**: Browse available dorm rooms, apartments, and temporary stays
- **Offer Housing**: List spare space for rent or sharing
- **Connect Safely**: University email verification ensures all users are legitimate students
- **Filter by Needs**: Search by location, price, property type, and availability dates

## Target Users

- University students looking for housing
- Students with extra space to rent/share  
- International students needing temporary housing
- Students doing exchanges/transfers

## Tech Stack

- **Backend**: Spring Boot (Java 17)
- **Frontend**: Swift (iOS app) - *to be implemented*
- **Database**: MySQL with H2 for testing
- **Authentication**: JWT with refresh tokens
- **Email**: Spring Mail for verification

## Features

### Authentication System
- University email verification required
- JWT-based authentication with refresh tokens
- Password hashing with BCrypt
- Automatic university domain validation

### Housing Listings
- Create offers for available housing
- Post requests for needed housing
- Multiple property types (dorm, apartment, studio, house, room shares)
- Flexible pricing (monthly or nightly rates)
- Location and amenity details
- Availability date ranges
- Photo uploads (future enhancement)

### Search and Discovery
- Public browsing of active listings
- Advanced filtering by:
  - Listing type (offer/request)
  - Property type
  - Location (city, university)
  - Price range
  - Availability dates
  - Text search in descriptions

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+ (for production) or H2 (for testing)

### Setup

1. Clone the repository:
```bash
git clone https://github.com/M1ngdaXie/RoomieHub.git
cd RoomieHub
```

2. Configure database in `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/roomiehub?createDatabaseIfNotExist=true
spring.datasource.username=your_username
spring.datasource.password=your_password

# JWT Configuration
jwt.secret=your-secret-key-change-in-production
jwt.access-token.expiration=86400
jwt.refresh-token.expiration=604800

# Email Configuration (for verification)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
app.mail.from=noreply@roomiehub.com
```

3. Build and run:
```bash
./mvnw spring-boot:run
```

4. Access the API at `http://localhost:8080`

### Testing

Run tests with H2 in-memory database:
```bash
./mvnw test
```

## API Documentation

### Authentication Endpoints
- `POST /api/auth/register` - Register new student account
- `POST /api/auth/login` - Login with email/password  
- `GET /api/auth/verify-email?token=...` - Verify email address
- `POST /api/auth/refresh-token` - Refresh access token
- `POST /api/auth/logout` - Logout and invalidate tokens

### Housing Endpoints

#### Public (No Authentication Required)
- `GET /api/housing/listings` - Browse all active listings
- `GET /api/housing/listings/{id}` - View specific listing details

#### Protected (Authentication Required)
- `POST /api/housing/listings` - Create new housing listing
- `GET /api/housing/my-listings` - Get user's listings
- `PUT /api/housing/listings/{id}` - Update listing
- `DELETE /api/housing/listings/{id}` - Delete listing
- `PATCH /api/housing/listings/{id}/activate` - Activate listing
- `PATCH /api/housing/listings/{id}/deactivate` - Deactivate listing

### Search Parameters
- `page` & `size` - Pagination
- `type` - Filter by OFFER or REQUEST  
- `propertyType` - DORM_ROOM, APARTMENT, STUDIO, etc.
- `search` - Text search in title/description/location
- `city` - Filter by city
- `university` - Filter by university domain
- `minPrice` & `maxPrice` - Price range
- `startDate` & `endDate` - Availability dates

## Project Structure

```
src/
├── main/java/com/campusnest/campusnest_platform/
│   ├── controllers/          # REST API controllers
│   │   ├── AuthController.java
│   │   └── HousingController.java
│   ├── models/              # JPA entities
│   │   ├── User.java
│   │   ├── HousingListing.java
│   │   ├── EmailVerification.java
│   │   └── RefreshToken.java
│   ├── services/            # Business logic
│   │   ├── AuthService.java
│   │   ├── HousingListingService.java
│   │   └── EmailVerificationService.java
│   ├── repository/          # Data access layer
│   ├── requests/            # API request DTOs
│   ├── response/            # API response DTOs
│   ├── enums/              # Enumeration types
│   └── security/           # Security configuration
└── test/                   # Unit and integration tests
```

## Database Schema

See [DATABASE_ARCHITECTURE.md](DATABASE_ARCHITECTURE.md) for detailed database design, best practices, and production recommendations.

## Development

### Adding New Features
1. Create feature branch from main
2. Add tests for new functionality  
3. Implement feature with proper validation
4. Update API documentation
5. Submit pull request

### Code Style
- Follow Spring Boot conventions
- Use Lombok for reducing boilerplate
- Comprehensive input validation
- Proper error handling and logging
- RESTful API design principles

## Security

- University email verification required
- JWT authentication with refresh tokens
- Password hashing with BCrypt
- Input validation and sanitization
- SQL injection prevention through JPA
- Rate limiting (to be implemented)

## Future Enhancements

- iOS mobile app
- Photo upload for listings
- In-app messaging system
- Rating and review system
- Payment integration
- Push notifications
- Geographic search with maps
- Machine learning recommendations

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes with tests
4. Submit a pull request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Support

For support, email support@roomiehub.com or create an issue in the GitHub repository.