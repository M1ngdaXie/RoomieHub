# RoomieHub Database Best Practices and Architecture

## Database Design Overview

The RoomieHub platform uses a MySQL database with the following core entities:

### Core Tables

1. **users** - Verified university students
   - University email verification required
   - Domain-based university validation
   - Email verification workflow

2. **housing_listings** - Housing offers and requests
   - Support for both offering and requesting housing
   - Multiple property types (dorm, apartment, studio, etc.)
   - Flexible pricing (monthly/nightly)
   - Location and amenity details
   - Date availability tracking

3. **email_verifications** - Email verification tokens
4. **refresh_tokens** - JWT refresh token management

## Best Practices Implemented

### 1. Data Security
- Password hashing with BCrypt
- JWT-based authentication with refresh tokens
- Email verification required for account activation
- University domain validation

### 2. Database Design
- Foreign key relationships maintained
- Enumerated types for consistent data
- Timestamp tracking (created_at, updated_at)
- Proper indexing on frequently queried fields

### 3. API Design
- RESTful endpoints
- Paginated results for large datasets
- Search functionality with multiple filters
- Public browsing for housing listings
- Protected endpoints for user operations

### 4. Data Validation
- Bean validation annotations
- Business logic validation in services
- Input sanitization and validation

## Recommended Database Architecture for Production

### 1. Connection Pooling
```properties
# MySQL Connection Pool Settings
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=20000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 2. Performance Optimization
```sql
-- Recommended indexes for housing_listings table
CREATE INDEX idx_housing_listings_status ON housing_listings(status);
CREATE INDEX idx_housing_listings_listing_type ON housing_listings(listing_type);
CREATE INDEX idx_housing_listings_property_type ON housing_listings(property_type);
CREATE INDEX idx_housing_listings_city ON housing_listings(city);
CREATE INDEX idx_housing_listings_created_at ON housing_listings(created_at DESC);
CREATE INDEX idx_housing_listings_price_month ON housing_listings(price_per_month);
CREATE INDEX idx_housing_listings_available_from ON housing_listings(available_from);

-- Composite indexes for common queries
CREATE INDEX idx_housing_listings_status_type ON housing_listings(status, listing_type);
CREATE INDEX idx_housing_listings_status_city ON housing_listings(status, city);
```

### 3. Database Configuration
```properties
# Production MySQL Settings
spring.datasource.url=jdbc:mysql://localhost:3306/roomiehub?useSSL=true&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.properties.hibernate.format_sql=false
spring.jpa.properties.hibernate.jdbc.batch_size=20
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true
```

### 4. Backup Strategy
- Daily automated backups
- Point-in-time recovery enabled
- Backup retention for 30 days
- Test restore procedures monthly

### 5. Monitoring and Logging
- Database connection monitoring
- Slow query logging
- Performance metrics tracking
- Application-level logging for data operations

### 6. Scalability Considerations
- Read replicas for geographic distribution
- Database sharding by university domain if needed
- Caching layer for frequently accessed data
- CDN for static content

## Security Best Practices

### 1. Database Security
- Encrypted connections (SSL/TLS)
- Principle of least privilege for database users
- Regular security updates
- Database firewall rules

### 2. Application Security
- SQL injection prevention (using JPA/Hibernate)
- Input validation and sanitization
- Rate limiting for API endpoints
- Audit logging for sensitive operations

### 3. Data Privacy
- GDPR compliance considerations
- User data anonymization options
- Secure data deletion procedures
- Privacy controls for listing visibility

## API Endpoints

### Public Endpoints
- `GET /api/housing/listings` - Browse housing listings
- `GET /api/housing/listings/{id}` - View specific listing

### Authenticated Endpoints
- `POST /api/housing/listings` - Create new listing
- `PUT /api/housing/listings/{id}` - Update listing
- `DELETE /api/housing/listings/{id}` - Delete listing
- `GET /api/housing/my-listings` - User's listings
- `PATCH /api/housing/listings/{id}/activate` - Activate listing
- `PATCH /api/housing/listings/{id}/deactivate` - Deactivate listing

### Search and Filter Parameters
- `type` - Filter by listing type (OFFER/REQUEST)
- `propertyType` - Filter by property type
- `search` - Text search in title/description/location
- `city` - Filter by city
- `university` - Filter by university domain
- `minPrice`/`maxPrice` - Price range filtering
- `startDate`/`endDate` - Availability date filtering

## Deployment Recommendations

1. Use managed database service (AWS RDS, Google Cloud SQL)
2. Enable automated backups and monitoring
3. Configure read replicas for improved performance
4. Implement database connection pooling
5. Set up proper logging and monitoring
6. Regular security audits and updates