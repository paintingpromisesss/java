CREATE TABLE IF NOT EXISTS users (
    name VARCHAR(255) PRIMARY KEY,
    exp INT NOT NULL
);

CREATE TABLE IF NOT EXISTS user_skills (
    user_name VARCHAR(255) NOT NULL,
    skill VARCHAR(255) NOT NULL,
    PRIMARY KEY (user_name, skill),
    CONSTRAINT fk_user_skills_user
        FOREIGN KEY (user_name)
        REFERENCES users (name)
        ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS jobs (
    title VARCHAR(255) PRIMARY KEY,
    company VARCHAR(255) NOT NULL,
    exp INT NOT NULL
);

CREATE TABLE IF NOT EXISTS job_tags (
    job_title VARCHAR(255) NOT NULL,
    tag VARCHAR(255) NOT NULL,
    PRIMARY KEY (job_title, tag),
    CONSTRAINT fk_job_tags_job
        FOREIGN KEY (job_title)
        REFERENCES jobs (title)
        ON DELETE CASCADE
);
