DROP DATABASE IF EXISTS `experimentation_system_db`;
CREATE DATABASE `experimentation_system_db`;
USE `experimentation_system_db`;

CREATE TABLE `management_users`
(
    `bgu_username` varchar(10)  NOT NULL,
    `bgu_password` varchar(10)  NOT NULL,
    `user_email`   varchar(255) NOT NULL,
    PRIMARY KEY (`bgu_username`)
);

CREATE TABLE `permissions`
(
    `permission_id`   int          NOT NULL AUTO_INCREMENT,
    `permission_name` varchar(255) NOT NULL,
    PRIMARY KEY (`permission_id`)
);

CREATE TABLE `management_users_permissions`
(
    `bgu_username`  varchar(10) NOT NULL,
    `permission_id` int         NOT NULL,
    FOREIGN KEY (`bgu_username`) REFERENCES management_users (`bgu_username`) ON DELETE CASCADE,
    FOREIGN KEY (`permission_id`) REFERENCES permissions (`permission_id`) ON DELETE CASCADE,
    PRIMARY KEY (`bgu_username`, `permission_id`)
);

CREATE TABLE `experiments`
(
    `experiment_id`   int          NOT NULL AUTO_INCREMENT,
    `experiment_name` varchar(100) NOT NULL,
    `published`       boolean      Not NULL,
    PRIMARY KEY (`experiment_id`)
);

CREATE TABLE `management_users_to_experiments`
(
    `bgu_username`  varchar(10)  NOT NULL,
    `experiment_id` int          NOT NULL,
    `role`          varchar(100) NOT NULL,
    FOREIGN KEY (`bgu_username`) REFERENCES management_users (`bgu_username`) ON DELETE CASCADE,
    FOREIGN KEY (`experiment_id`) REFERENCES experiments (`experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`bgu_username`, `experiment_id`)
);

CREATE TABLE `participants`
(
    `participant_id` int NOT NULL AUTO_INCREMENT,
    `experiment_id`  int NOT NULL,
    `curr_stage`     int NOT NULL,
    `is_done`        boolean,
    FOREIGN KEY (`experiment_id`) REFERENCES experiments (`experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`participant_id`)
);

CREATE TABLE `experimentees`
(
    `access_code`        varchar(255) NOT NULL,
    `experimentee_email` varchar(255) NOT NULL,
    `participant_id`     int          NOT NULL,
    FOREIGN KEY (`participant_id`) REFERENCES participants (`participant_id`) ON DELETE CASCADE,
    PRIMARY KEY (`access_code`)
);

CREATE TABLE `graders`
(
    `grader_email`   varchar(255) NOT NULL,
    `participant_id` int          NOT NULL,
    FOREIGN KEY (`participant_id`) REFERENCES participants (`participant_id`) ON DELETE CASCADE,
    PRIMARY KEY (`grader_email`)
);

CREATE TABLE `stages`
(
    `stage_index`   int NOT NULL,
    `experiment_id` int NOT NULL,
    FOREIGN KEY (`experiment_id`) REFERENCES experiments (`experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`stage_index`, `experiment_id`)
);

CREATE TABLE `grading_tasks`
(
    `grading_task_id`    int NOT NULL AUTO_INCREMENT,
    `grading_task_name`  varchar(100) NOT NULL,
    `grading_experiment` int NOT NULL,
    `base_experiment`    int NOT NULL,
    `general_experiment` int,
    FOREIGN KEY (`grading_experiment`) REFERENCES experiments (`experiment_id`) ON DELETE CASCADE,
    FOREIGN KEY (`base_experiment`) REFERENCES experiments (`experiment_id`) ON DELETE CASCADE,
    FOREIGN KEY (`general_experiment`) REFERENCES experiments (`experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`grading_task_id`)
);

CREATE TABLE `stages_of_grading_task`
(
    `grading_task_id` int NOT NULL REFERENCES grading_tasks (`grading_task_id`) ON DELETE CASCADE,
    `stage_index`   int  NOT NULL REFERENCES stages (`stage_index`) ON DELETE CASCADE,
    `experiment_id` int  NOT NULL REFERENCES stages (`experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`grading_task_id`, `stage_index`, `experiment_id`)
);

CREATE TABLE `graders_to_grading_tasks`
(
    `grading_task_id`    int          NOT NULL,
    `grader_email`       varchar(255) NOT NULL,
    `grader_access_code` varchar(255) NOT NULL,
    FOREIGN KEY (`grading_task_id`) REFERENCES grading_tasks (`grading_task_id`) ON DELETE CASCADE,
    FOREIGN KEY (`grader_email`) REFERENCES graders (`grader_email`) ON DELETE CASCADE,
    PRIMARY KEY (`grading_task_id`, `grader_email`)
);

CREATE TABLE `graders_grading_tasks_to_participants`
(
    `grading_task_id` int          NOT NULL,
    `grader_email`    varchar(255) NOT NULL,
    `participant_id`  int          NOT NULL,
    `grading_state`   boolean      NOT NULL,
    FOREIGN KEY (`grading_task_id`, `grader_email`) REFERENCES graders_to_grading_tasks (`grading_task_id`, `grader_email`) ON DELETE CASCADE,
    FOREIGN KEY (`participant_id`) REFERENCES participants (`participant_id`) ON DELETE CASCADE,
    PRIMARY KEY (`grading_task_id`, `grader_email`, `participant_id`)
);

CREATE TABLE `info_stages`
(
    `stage_index`   int  NOT NULL REFERENCES stages (`stage_index`) ON DELETE CASCADE,
    `experiment_id` int  NOT NULL REFERENCES stages (`experiment_id`) ON DELETE CASCADE,
    `info`          TEXT NOT NULL,
    PRIMARY KEY (`stage_index`, `experiment_id`)
);

CREATE TABLE `questionnaire_stages`
(
    `stage_index`   int NOT NULL REFERENCES stages (`stage_index`) ON DELETE CASCADE,
    `experiment_id` int NOT NULL REFERENCES stages (`experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`stage_index`, `experiment_id`)
);

CREATE TABLE `questions`
(
    `question_index` int  NOT NULL,
    `question_json`  JSON NOT NULL,
    `stage_index`    int  NOT NULL,
    `experiment_id`  int  NOT NULL,
    FOREIGN KEY (`stage_index`, `experiment_id`) REFERENCES questionnaire_stages (`stage_index`, `experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`question_index`, `stage_index`, `experiment_id`)
);

CREATE TABLE `code_stages`
(
    `stage_index`   int  NOT NULL REFERENCES stages (`stage_index`) ON DELETE CASCADE,
    `experiment_id` int  NOT NULL REFERENCES stages (`experiment_id`) ON DELETE CASCADE,
    `description`   TEXT NOT NULL,
    `template`      TEXT NOT NULL,
    PRIMARY KEY (`stage_index`, `experiment_id`)
);

CREATE TABLE `requirements`
(
    `requirement_index` int  NOT NULL,
    `text`              TEXT NOT NULL,
    `stage_index`       int  NOT NULL,
    `experiment_id`     int  NOT NULL,
    FOREIGN KEY (`stage_index`, `experiment_id`) REFERENCES code_stages (`stage_index`, `experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`requirement_index`, `stage_index`, `experiment_id`)
);

CREATE TABLE `tagging_stages`
(
    `stage_index`                    int NOT NULL REFERENCES stages (`stage_index`) ON DELETE CASCADE,
    `experiment_id`                  int NOT NULL REFERENCES stages (`experiment_id`) ON DELETE CASCADE,
    `appropriate_coding_stage_index` int,
    FOREIGN KEY (`appropriate_coding_stage_index`, `experiment_id`) REFERENCES code_stages (`stage_index`, `experiment_id`) ON DELETE CASCADE,
    PRIMARY KEY (`stage_index`, `experiment_id`)
);

CREATE TABLE `requirement_tags`
(
    `start_char_loc` int NOT NULL,
    `length`         int NOT NULL,
    `participant_id` int NOT NULL,
    FOREIGN KEY (`participant_id`) REFERENCES participants (`participant_id`) ON DELETE CASCADE,
    PRIMARY KEY (`participant_id`, `start_char_loc`)
);

CREATE TABLE `requirements_to_requirement_tags`
(
    `requirement_index` int NOT NULL,
    `experiment_id`     int NOT NULL,
    `stage_index`       int NOT NULL,
    `start_char_loc`    int NOT NULL,
    `participant_id`    int NOT NULL,
    FOREIGN KEY (`requirement_index`, `stage_index`, `experiment_id`) REFERENCES requirements (`requirement_index`, `stage_index`, `experiment_id`) ON DELETE CASCADE,
    FOREIGN KEY (`participant_id`, `start_char_loc`) REFERENCES requirement_tags (`participant_id`, `start_char_loc`) ON DELETE CASCADE,
    PRIMARY KEY (`requirement_index`, `stage_index`, `experiment_id`, `participant_id`, `start_char_loc`)
);

CREATE TABLE `code_results`
(
    `user_code`      TEXT NOT NULL,
    `stage_index`    int  NOT NULL,
    `experiment_id`  int  NOT NULL,
    `participant_id` int  NOT NULL,
    FOREIGN KEY (`stage_index`, `experiment_id`) REFERENCES code_stages (`stage_index`, `experiment_id`) ON DELETE CASCADE,
    FOREIGN KEY (`participant_id`) REFERENCES participants (`participant_id`) ON DELETE CASCADE,
    PRIMARY KEY (`participant_id`, `stage_index`, `experiment_id`)
);

CREATE TABLE `answers`
(
    `textual_answer` TEXT,
    `numeral_answer` int,
    `question_index` int NOT NULL,
    `stage_index`    int NOT NULL,
    `experiment_id`  int NOT NULL,
    `participant_id` int NOT NULL,
    FOREIGN KEY (`question_index`, `stage_index`, `experiment_id`) REFERENCES questions (`question_index`, `stage_index`, `experiment_id`) ON DELETE CASCADE,
    FOREIGN KEY (`participant_id`) REFERENCES participants (`participant_id`) ON DELETE CASCADE,
    PRIMARY KEY (`participant_id`, `question_index`, `stage_index`, `experiment_id`)
);

