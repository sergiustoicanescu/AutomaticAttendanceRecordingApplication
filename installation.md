# 📌 Installation & Setup

## 1️⃣ Clone the Repository
```sh
 git clone https://github.com/yourusername/AutomaticAttendance.git
 cd AutomaticAttendance
```

## 2️⃣ Backend Setup
Ensure you have **JDK 19** and MySQL installed. Configure the environment variables for the backend:

```sh
 export GOOGLE_AUTH_ID=your_google_auth_id
 export GOOGLE_AUTH_SECRET=your_google_auth_secret
 export FRONTEND_URL=your_frontend_url
 export DOMAIN_URL=your_domain_url
 export SPRING_DATASOURCE_USERNAME=your_db_username
 export SPRING_DATASOURCE_PASSWORD=your_db_password
```

Then, build and run the backend:
```sh
 mvn clean install
 java -jar target/AutomaticAttendance.jar
```

## 3️⃣ Frontend Setup
Navigate to the frontend directory and configure the required environment variables:

```sh
 export REACT_APP_GOOGLE_MAPS_API_KEY=your_google_maps_api_key
 export REACT_APP_SERVER_URL=your_server_url
 export REACT_APP_DOMAIN=your_domain
```

Then, install dependencies and run the app:
```sh
 npm install
 npm start
```

---


## 🎯 You're all set! Happy coding! 🚀

