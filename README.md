# 🏠 CampusNest Platform

A comprehensive university student housing platform connecting students with safe, affordable housing options. Built with a robust Spring Boot backend and planned iOS mobile app frontend for seamless campus housing experiences.

## 🚀 Current Features (Backend API)

### 🔐 Authentication & Security
- **JWT-based Authentication** with refresh token management
- **Email Verification System** for secure account activation
- **Advanced Password Reset** with one-time tokens and rate limiting
- **Role-based Access Control** (STUDENT/ADMIN permissions)
- **Spring Security Integration** with comprehensive UserDetails implementation

### 👥 User Management System
- **Complete User Lifecycle** management with account status tracking
- **Admin Dashboard** for user oversight and system analytics
- **Privacy-First Design** with email masking and audit logging
- **Account Security Features** (locking, expiration, credential management)

### 🏡 Housing Listing Platform
- **Full CRUD Operations** for property listings
- **Detailed Property Information** (bedrooms, bathrooms, pricing, location)
- **Smart Ownership Verification** with method-level security
- **Automatic Listing Expiration** with maintenance workflows
- **User Favorites System** for personalized housing discovery

### 📸 Advanced Image Management
- **AWS S3 Integration** with private bucket security
- **Pre-signed URL Generation** for secure direct uploads
- **Multi-image Support** with primary designation and ordering
- **Time-limited Access URLs** (24-hour expiration for security)
- **Scalable Cloud Storage** handling all image operations

### 📧 Communication Infrastructure
- **SMTP Email System** for verification and notifications
- **Template-based Messaging** with consistent branding
- **Rate Limiting Protection** against abuse and spam

### 🗄️ Database & Performance
- **MySQL Integration** with optimized JPA/Hibernate configuration
- **Strategic Indexing** for high-performance queries
- **Transactional Data Operations** ensuring consistency
- **Comprehensive Audit Trails** for security and compliance

## 🛠️ Technology Stack

**Backend API:**
- **Framework**: Spring Boot 3.5.5 with Java 17
- **Security**: Spring Security + JWT tokens
- **Database**: MySQL with JPA/Hibernate ORM
- **Cloud Storage**: AWS S3 for secure image management
- **Email**: Gmail SMTP integration
- **Build**: Maven with comprehensive testing suite (28+ tests)

**Planned Frontend:**
- **iOS Mobile App**: Swift with UIKit/SwiftUI
- **Real-time Communication**: WebSocket integration
- **Local Storage**: Core Data for offline capabilities

## 📱 API Architecture

### Current REST Endpoints

#### Authentication (`/api/auth`)
```
POST /register          # User registration with email verification
POST /login            # JWT authentication
POST /refresh          # Token refresh
POST /logout           # Secure session termination
POST /forgot-password  # Password reset initiation
POST /reset-password   # Secure password reset
```

#### Housing Management (`/api/housing`)
```
GET  /                 # Browse active listings (public)
POST /                 # Create new listing (authenticated)
GET  /{id}            # View detailed listing
PUT  /{id}            # Update listing (owner/admin)
DELETE /{id}          # Remove listing (owner/admin)
```

#### Media Management (`/api/images`)
```
POST /upload-url       # Generate secure S3 upload URL
GET  /url/{s3Key}     # Retrieve signed viewing URL
```

#### Administration (`/api/admin`)
```
GET  /users           # User management with pagination
PUT  /users/{id}/role # Role assignment (STUDENT/ADMIN)
PUT  /users/{id}/status # Account status management
GET  /stats           # System analytics and metrics
```

## 🔮 Upcoming Features & Roadmap

### 📱 iOS Mobile Application (Q1 2025)
- **Native Swift Development** with modern UI/UX design
- **Intuitive Property Browsing** with swipe gestures and filters
- **Interactive Map Integration** for location-based search
- **Push Notifications** for new listings and updates
- **Offline Capability** with Core Data synchronization
- **Camera Integration** for direct property photo uploads

### 📅 Booking & Reservation System (Q2 2025)
- **Smart Booking Calendar** with availability management
- **Instant Booking** vs. request-to-book options
- **Booking Conflict Prevention** with real-time availability
- **Automated Confirmation** emails and SMS notifications
- **Booking History** and management dashboard
- **Cancellation Policies** with flexible terms

### 💰 Deposit Management System (Q2 2025)
- **Secure Deposit Collection** with payment gateway integration
- **Escrow Service** for deposit protection
- **Automated Refund Processing** based on inspection reports
- **Damage Assessment Tools** with photo documentation
- **Dispute Resolution** workflow with admin mediation
- **Financial Reporting** for landlords and tenants

### ⭐ Rating & Review System (Q3 2025)
- **Dual Rating System** (properties and users)
- **Verified Reviews** only from confirmed bookings
- **Photo Reviews** with image verification
- **Response System** for landlord feedback
- **Trust Scores** based on booking history and reviews
- **Spam Detection** and content moderation

### 🔄 Real-Time Messaging Infrastructure (Q3 2025)
- **WebSocket Integration** for instant communication
- **Apache Kafka** for scalable message streaming
- **In-App Chat** between students and landlords
- **Message Threading** for organized conversations
- **File Sharing** capabilities (documents, photos)
- **Message Encryption** for privacy protection
- **Typing Indicators** and read receipts
- **Push Notification** integration for offline users

### 🚀 Advanced Platform Features (Q4 2025)
- **AI-Powered Recommendations** based on user preferences and behavior
- **Smart Pricing Suggestions** using market data analysis
- **Virtual Property Tours** with 360° photo integration
- **Roommate Matching** algorithm with compatibility scoring
- **Integration with University Systems** for student verification
- **Multi-Language Support** for international students

## 🏗️ Development Roadmap

### Phase 1: Mobile Foundation (Q1 2025)
- iOS app development with core browsing features
- API optimization for mobile consumption
- Push notification infrastructure
- Offline synchronization capabilities

### Phase 2: Booking Platform (Q2 2025)
- Complete booking system implementation
- Payment gateway integration (Stripe/Square)
- Deposit management with escrow services
- Calendar and availability management

### Phase 3: Community Features (Q3 2025)
- Rating and review system
- Real-time messaging with WebSocket + Kafka
- Enhanced user profiles and verification
- In-app communication tools

### Phase 4: Intelligence & Scale (Q4 2025)
- AI recommendation engine
- Advanced analytics and insights
- Multi-university expansion
- Enterprise features for property managers

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+
- AWS Account for S3 storage

### Quick Setup
```bash
# Clone the repository
git clone https://github.com/yourusername/campusnest-platform.git

# Navigate to project
cd campusnest-platform

# Run the application
./mvnw spring-boot:run
```

### Database Configuration
```properties
# Create MySQL database: campusNest
# Update application.properties with your credentials
# Schema auto-creates on first run
```

### AWS S3 Setup
```bash
export AWS_ACCESS_KEY_ID=your_access_key
export AWS_SECRET_ACCESS_KEY=your_secret_key
```

## 🧪 Testing & Quality

- **28+ Comprehensive Tests** covering security, integration, and business logic
- **Security Testing** for authentication and authorization flows
- **API Contract Testing** ensuring endpoint reliability
- **Performance Testing** for scalability validation

```bash
# Run complete test suite
./mvnw test

# Run security tests
./mvnw test -Dtest="*SecurityTest"
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

## 📄 License

This project is licensed under the MIT License - see [LICENSE](LICENSE) for details.

## 🌟 Connect With Us

- **Project Repository**: [GitHub](https://github.com/yourusername/campusnest-platform)
- **Issue Tracking**: [GitHub Issues](https://github.com/yourusername/campusnest-platform/issues)
- **Documentation**: [Wiki](https://github.com/yourusername/campusnest-platform/wiki)

---

⭐ **Star this repository if you're excited about the future of student housing!**

*Revolutionizing campus living through technology - connecting students with their perfect home away from home.*