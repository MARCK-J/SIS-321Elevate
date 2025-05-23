-- Reingeniería: Roles dinámicos y referencias directas a Users

-- Table: Roles
CREATE TABLE Roles (
    role_id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    creation_date DATE NOT NULL DEFAULT CURRENT_DATE
);

-- Table: Instituciones
CREATE TABLE Instituciones (
    institucion_id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    dominio_institucional VARCHAR(100) NOT NULL
);

-- Table: Category_course
CREATE TABLE Category_course (
    id SERIAL PRIMARY KEY,
    name_category varchar(50) NOT NULL
);

-- Table: Users
CREATE TABLE Users (
    user_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    role_id INT NOT NULL,
    institucion_id INT,
    verification BOOLEAN NOT NULL,
    activation BOOLEAN NOT NULL,
    date_join DATE NOT NULL,
    CONSTRAINT fk_institucion FOREIGN KEY (institucion_id) REFERENCES Instituciones(institucion_id),
    CONSTRAINT fk_role FOREIGN KEY (role_id) REFERENCES Roles(role_id)
);

-- Table: Courses
CREATE TABLE Courses (
    course_id SERIAL PRIMARY KEY,
    title varchar(255)  NOT NULL,
    image text  NOT NULL,
    description text  NOT NULL,
    duration varchar(25)  NOT NULL,
    rating DECIMAL(2, 1) DEFAULT 0,
    abilities text NOT NULL,
    available boolean  NOT NULL,
    teacher_user_id INT NOT NULL,
    category_course_id INT NOT NULL,
    CONSTRAINT fk_courses_teacher FOREIGN KEY (teacher_user_id) REFERENCES Users(user_id),
    CONSTRAINT fk_courses_category FOREIGN KEY (category_course_id) REFERENCES Category_course(id)
);

-- Table: Enrollments
CREATE TABLE Enrollments (
    enrollment_id SERIAL PRIMARY KEY,
    enrollment_date timestamp  NOT NULL,
    student_user_id INT NOT NULL,
    courses_course_id INT NOT NULL,
    completed boolean  NOT NULL,
    CONSTRAINT fk_enrollments_student FOREIGN KEY (student_user_id) REFERENCES Users(user_id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (courses_course_id) REFERENCES Courses(course_id)
);

-- Table: Lessons
CREATE TABLE Lessons (
    lessons_id SERIAL  NOT NULL,
    course_id int  NOT NULL,
    title varchar(50)  NOT NULL,
    description varchar(255)  NOT NULL,
    duration varchar(25)  NOT NULL,
    content text  NOT NULL,
    video text  NOT NULL,
    "order" int  NOT NULL,
    complete boolean  NOT NULL,
    CONSTRAINT Lessons_pk PRIMARY KEY (lessons_id),
    CONSTRAINT fk_lessons_course FOREIGN KEY (course_id) REFERENCES Courses(course_id)
);

-- Table: Messages
CREATE TABLE Messages (
    message_id SERIAL PRIMARY KEY,
    content text  NOT NULL,
    time timestamp  NOT NULL
);

-- Table: Logs de seguridad
CREATE TABLE Logs_seguridad (
    log_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    action VARCHAR(50) NOT NULL,
    details TEXT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_log_user FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Table: Quizzes
CREATE TABLE Quizzes (
    quiz_id SERIAL PRIMARY KEY,
    title varchar(255) NOT NULL,
    description text NOT NULL,
    due_date date NOT NULL,
    lessons_id int NOT NULL,
    course_id int NOT NULL,
    CONSTRAINT Quizzes_Lessons_fk FOREIGN KEY (lessons_id) REFERENCES Lessons (lessons_id),
    CONSTRAINT Quizzes_Courses_fk FOREIGN KEY (course_id) REFERENCES Courses (course_id)
);

-- Table: Questions
CREATE TABLE Questions (
    question_id SERIAL PRIMARY KEY,
    quiz_id int NOT NULL,
    content text NOT NULL,
    question_type varchar(50) NOT NULL,
    CONSTRAINT Questions_Quizzes_fk FOREIGN KEY (quiz_id) REFERENCES Quizzes (quiz_id)
);

-- Table: Options
CREATE TABLE Options (
    option_id SERIAL PRIMARY KEY,
    question_id int NOT NULL,
    content text NOT NULL,
    is_correct boolean NOT NULL,
    CONSTRAINT Options_Questions_fk FOREIGN KEY (question_id) REFERENCES Questions (question_id)
);

-- Table: Results
CREATE TABLE Results (
    result_id SERIAL PRIMARY KEY,
    score int NOT NULL,
    submission_date date NOT NULL,
    quiz_id int NOT NULL,
    student_user_id int NOT NULL,
    CONSTRAINT Results_Quizzes_fk FOREIGN KEY (quiz_id) REFERENCES Quizzes (quiz_id),
    CONSTRAINT Results_Student_fk FOREIGN KEY (student_user_id) REFERENCES Users (user_id)
);

-- Table: Certification
CREATE TABLE Certification (
    certification_id SERIAL PRIMARY KEY,
    student_user_id INT NOT NULL,
    course_id INT NOT NULL,
    issue_date TIMESTAMP NOT NULL,
    CONSTRAINT Certification_Student_fk FOREIGN KEY (student_user_id) REFERENCES Users (user_id),
    CONSTRAINT Certification_Courses_fk FOREIGN KEY (course_id) REFERENCES Courses (course_id)
);

-- Table: favorites
CREATE TABLE favorites (
    favorite_id SERIAL PRIMARY KEY,
    student_user_id BIGINT NOT NULL,
    course_id BIGINT NOT NULL,
    FOREIGN KEY (student_user_id) REFERENCES Users(user_id),
    FOREIGN KEY (course_id) REFERENCES Courses(course_id)
);

-- Table: Notifications (tokens de verificación)
CREATE TABLE IF NOT EXISTS verification_token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id INT NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    CONSTRAINT fk_verification_user FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- Table: Assessments
CREATE TABLE Assessments (
    assessments_id SERIAL PRIMARY KEY,
    title varchar(255)  NOT NULL,
    description text  NOT NULL,
    grade int  NOT NULL,
    due_date date  NOT NULL
);

-- FIN DEL SCRIPT