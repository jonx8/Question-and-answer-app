CREATE DATABASE questions;
CREATE DATABASE notifications;

CREATE USER questions_user WITH PASSWORD 'password';
CREATE USER notifications_user WITH PASSWORD 'password';

GRANT ALL PRIVILEGES ON DATABASE questions TO questions_user;
GRANT ALL PRIVILEGES ON DATABASE notifications TO notifications_user;

\c questions;
GRANT ALL ON SCHEMA public TO questions_user;


\c notifications;
GRANT ALL ON SCHEMA public TO notifications_user;
