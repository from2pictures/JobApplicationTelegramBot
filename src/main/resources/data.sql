DELETE FROM vacancy_skills_needed;
DELETE FROM vacancies;
DELETE FROM companies;
DELETE FROM cities;
DELETE FROM users;

INSERT INTO users (chat_id, first_name, last_name, username, language) VALUES
                                                                      (111222333, 'Иван', 'Иванов', 'ivan_dev', 'ru'),
                                                                      (444555666, 'John', 'Doe', 'johndoe', 'en'),
                                                                      (777888999, 'Анна', 'Петрова', 'anna_recruiter', 'ru'),
                                                                      (555444333, 'Сергей', 'Сидоров', 'sergey_qa', 'ru'),
                                                                      (222333444, 'Elena', 'Smirnova', 'elena_pm', 'en');

INSERT INTO cities (id, name) VALUES
                                  (1, 'Москва'),
                                  (2, 'Санкт-Петербург'),
                                  (3, 'Новосибирск'),
                                  (4, 'Екатеринбург'),
                                  (5, 'Казань'),
                                  (6, 'Нижний Новгород'),
                                  (7, 'Удаленная работа');

INSERT INTO companies (id, name) VALUES
                                     (10, 'Яндекс'),
                                     (20, 'Сбер'),
                                     (30, 'Т-Банк'),
                                     (40, 'VK'),
                                     (50, 'Озон'),
                                     (60, 'Альфа-Банк'),
                                     (70, 'Авито');

INSERT INTO vacancies (id, title, company_id, city_id, salary, description, publish_date, link_to_original) VALUES
(101, 'Junior Java Developer', 10, 1, 100000.00, 'Ищем начинающего разработчика в команду бэкенда. Готовы обучать.', '2026-05-20', 'https://ya.ru'),
(102, 'Middle Java Developer', 30, 1, 260000.00, 'Разработка новых фичей для мобильного банка. Опыт от 2-х лет.', '2026-05-22', 'https://tbank.ru'),
(103, 'Senior Java Engineer', 70, 1, 450000.00, 'Проектирование архитектуры высоконагруженных сервисов доставки сообщений.', '2026-05-24', 'https://avito.ru'),

(104, 'QA Automation (Java)', 40, 2, 190000.00, 'Автоматизация тестирования высоконагруженной социальной платформы.', '2026-05-25', 'https://vk.company'),
(105, 'Middle Spring Developer', 60, 2, 240000.00, 'Разработка кредитного конвейера. Работа со Spring Boot и Kafka.', '2026-05-25', 'https://alfa.ru'),

(106, 'Java Backend Team Lead', 50, 5, 550000.00, 'Управление командой разработки логистической платформы маркетплейса.', '2026-05-26', 'https://ozon.ru'),

(107, 'Middle Kotlin/Java Developer', 10, 7, 280000.00, 'Разработка микросервисов в облачной инфраструктуре. Полная удаленка.', '2026-05-26', 'https://ya.ru'),
(108, 'Senior Spring Boot Architect', 20, 7, 500000.00, 'Проектирование целевой архитектуры распределенных систем ядра банка.', '2026-05-27', 'https://sber.ru'),
(109, 'Intern Java Developer', 30, 7, 50000.00, 'Стажировка для студентов старших курсов. Шанс попасть в штат.', '2026-05-27', 'https://tbank.ru');

INSERT INTO vacancy_skills_needed (vacancy_id, skills_needed) VALUES
(101, 'Java 17'),
(101, 'SQL'),
(101, 'Git'),

(102, 'Java 21'),
(102, 'Spring Boot'),
(102, 'PostgreSQL'),
(102, 'Docker'),

(103, 'Java 21'),
(103, 'Spring Cloud'),
(103, 'Kafka'),
(103, 'Kubernetes'),
(103, 'Redis'),

(104, 'Java'),
(104, 'Selenium'),
(104, 'JUnit 5'),
(104, 'Allure'),

(105, 'Java'),
(105, 'Spring Boot'),
(105, 'Kafka'),
(105, 'Liquibase'),

(106, 'Java'),
(106, 'Management'),
(106, 'System Design'),
(106, 'PostgreSQL'),

(107, 'Java'),
(107, 'Kotlin'),
(107, 'Spring Boot'),
(107, 'gRPC'),

(108, 'Java 21'),
(108, 'Spring Framework'),
(108, 'Microservices'),
(108, 'Apache Kafka'),
(108, 'Kubernetes'),

(109, 'Java Core'),
(109, 'Algorithms');
