# Online Quiz Application

A modern, production-ready Online Quiz Application built with Spring Boot, React, and MySQL on Railway.

## 🚀 Features

- **User Authentication**: Secure registration and login with JWT tokens
- **Role-Based Access Control**: Admin and Participant roles
- **Quiz Management**: Admins can create, edit, and manage quizzes
- **Quiz Taking**: Participants can take quizzes with timers
- **Scoring System**: Automatic scoring and result tracking
- **History & Analytics**: Track quiz attempts and performance
- **Responsive UI**: Modern, animated interface with Tailwind CSS
- **API Documentation**: Interactive Swagger UI for all endpoints

## 📋 Tech Stack

### Backend
- Java 17
- Spring Boot 3.2.0
- Spring Security + JWT
- Spring Data JPA
- MySQL Database
- Maven
- JUnit 5 + Mockito (Testing)
- SpringDoc OpenAPI (Swagger)

### Frontend
- React 18
- Vite
- React Router v6
- Axios
- Tailwind CSS
- Framer Motion (Animations)
- Zustand (State Management)
- React Hot Toast (Notifications)

### Database
- MySQL (Railway)

## 🛠️ Installation & Setup

### Prerequisites
- Java 17+
- Node.js 18+
- Maven 3.9+
- MySQL (Railway account)

### Backend Setup

1. **Clone the repository**
   ```bash
   cd backend
   ```

2. **Configure Database**
   - Update `src/main/resources/application.properties` with your Railway MySQL connection:
   ```properties
   spring.datasource.url=jdbc:mysql://switchyard.proxy.rlwy.net:19594/railway
   spring.datasource.username=root
   spring.datasource.password=your_password
   ```

3. **Update JWT Secret**
   - Replace the `jwt.secret` in `application.properties` with a strong secret key (min 32 chars)

4. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start at `http://localhost:8080`

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd frontend
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Create environment file**
   ```bash
   cp .env.example .env
   ```
   Update `VITE_API_BASE_URL` in `.env` if running on a different backend URL

4. **Start development server**
   ```bash
   npm run dev
   ```

   The frontend will be available at `http://localhost:5173`

## 🔌 API Documentation

Once the backend is running, access the interactive API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### Key API Endpoints

#### Authentication
- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user

#### Admin Endpoints
- `POST /api/admin/quiz` - Create a new quiz
- `PUT /api/admin/quiz/{id}` - Update a quiz
- `DELETE /api/admin/quiz/{id}` - Delete a quiz
- `POST /api/admin/question` - Add a question to a quiz
- `GET /api/admin/quiz/{quizId}/results` - Get quiz results

#### Participant Endpoints
- `GET /api/quizzes` - Get all published quizzes
- `GET /api/quiz/{id}` - Get quiz details with questions
- `POST /api/quiz/submit` - Submit completed quiz
- `GET /api/user/history` - Get user's quiz history
- `GET /api/attempt/{id}` - Get specific quiz attempt details

## 🗄️ Database Schema

### User Table
- id (Primary Key)
- name
- email (Unique)
- password (BCrypt encrypted)
- role (ADMIN/PARTICIPANT)
- is_active
- created_at, updated_at

### Quiz Table
- id (Primary Key)
- title
- description
- time_limit (minutes)
- is_published
- created_by (Foreign Key to User)
- created_at, updated_at

### Question Table
- id (Primary Key)
- quiz_id (Foreign Key)
- question_text
- options (JSON array)
- correct_answer_index
- question_order
- is_active

### QuizAttempt Table
- id (Primary Key)
- user_id (Foreign Key)
- quiz_id (Foreign Key)
- score
- total_questions
- selected_answers (JSON)
- time_spent (seconds)
- submitted_at
- created_at

## 📦 Deployment

### Backend - AWS EC2

1. **Create JAR file**
   ```bash
   mvn clean package
   ```

2. **Upload JAR to EC2**
   ```bash
   scp -i your-key.pem target/quiz-app-1.0.0.jar ec2-user@your-ec2-ip:/home/ec2-user/
   ```

3. **Run on EC2**
   ```bash
   java -Dspring.profiles.active=prod -jar quiz-app-1.0.0.jar
   ```

### Docker Deployment (Alternative)

Build Docker image:
```bash
docker build -t quiz-app .
docker run -p 8080:8080 -e SPRING_DATASOURCE_URL=your_db_url quiz-app
```

### Frontend - Netlify

1. **Build the project**
   ```bash
   npm run build
   ```

2. **Deploy to Netlify**
   - Connect your GitHub repository to Netlify
   - Set build command: `npm run build`
   - Set publish directory: `dist`
   - Add environment variable `VITE_API_BASE_URL` pointing to your backend

   OR use Netlify CLI:
   ```bash
   npm install -g netlify-cli
   netlify deploy
   ```

## 🔐 Security Features

- JWT Token-based Authentication
- BCrypt password encryption
- Role-Based Access Control (RBAC)
- CORS configuration
- Stateless session management
- Input validation (Spring Validation)
- Exception handling and error middleware

## 🧪 Testing

Run backend tests:
```bash
mvn test
```

Test files are located in `src/test/java/com/online_quiz/`

### Test Coverage
- AuthServiceTest - User registration and login
- QuizServiceTest - Quiz CRUD operations

## 📱 Frontend Features

### Pages
- **Landing Page**: Welcome and feature showcase
- **Login Page**: User authentication
- **Register Page**: New user registration
- **Admin Dashboard**: Quiz management
- **Create Quiz Page**: New quiz creation
- **Quiz Browsing**: List of available quizzes
- **Quiz Taking**: Interactive quiz interface with timer
- **Results Page**: Quiz score and performance
- **History Page**: Past quiz attempts
- **404 Page**: Not found error page

### UI Components
- Responsive navigation header
- Interactive quiz cards
- Animated page transitions
- Toast notifications
- Loading spinners
- Progress indicators
- Modal dialogs

## 🎨 Styling

The frontend uses:
- **Tailwind CSS**: Utility-first CSS framework
- **Framer Motion**: Smooth animations
- **Custom CSS**: Global styles and animations
- **Dark Mode Support**: Built-in dark theme

## 📝 Default Test Credentials

After initial setup, you can create test users through the registration page:

**Admin User:**
- Role: ADMIN
- Can create and manage quizzes

**Participant User:**
- Role: PARTICIPANT
- Can take quizzes and view results

## 🐛 Troubleshooting

### Database Connection Issues
- Verify Railway MySQL credentials
- Check network connectivity to Railway proxy
- Ensure database exists

### CORS Errors
- Update `app.cors.allowed-origins` in `application.properties`
- Include your frontend URL (e.g., `https://yourdomain.netlify.app`)

### JWT Token Expired
- Token expiration is set to 24 hours by default (configurable in `application.properties`)
- User will be redirected to login page on token expiration

### Frontend Build Issues
- Clear `node_modules`: `rm -rf node_modules && npm install`
- Clear Vite cache: `rm -rf .vite`
- Check Node version: `node --version` (should be 18+)

## 📚 Project Structure

```
online-quiz-app/
├── backend/
│   ├── src/
│   │   ├── main/java/com/online_quiz/
│   │   │   ├── config/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── entity/
│   │   │   ├── exception/
│   │   │   ├── repository/
│   │   │   ├── security/
│   │   │   └── service/
│   │   ├── test/java/com/online_quiz/
│   │   └── resources/
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   │   ├── admin/
│   │   │   ├── participant/
│   │   │   ├── pages/
│   │   │   └── layout/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── services/
│   │   ├── styles/
│   │   ├── App.jsx
│   │   └── main.jsx
│   ├── index.html
│   ├── vite.config.js
│   ├── tailwind.config.js
│   ├── package.json
│   └── netlify.toml
└── README.md
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For issues and questions, please create an issue in the repository or contact the development team.

## 🎯 Future Enhancements

- Email notifications for quiz completion
- Advanced analytics dashboard
- Quiz categories and tags
- Leaderboard system
- Attempt review with detailed explanations
- Mobile app (React Native)
- WebSocket for real-time notifications
- Multi-language support

---

**Built with ❤️ as a production-ready application**
