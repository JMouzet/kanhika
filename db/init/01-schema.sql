CREATE TABLE "kanjis" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "freq_mainichi_shinbun" smallint(6) DEFAULT 0,
  "grade" tinyint(4) DEFAULT 0,
  "heisig_en" varchar(255) DEFAULT '0',
  "jlpt" tinyint(4) DEFAULT 0,
  "kanji" varchar(5) UNIQUE DEFAULT null,
  "stroke_count" tinyint(4) DEFAULT 0,
  "unicode" varchar(10) DEFAULT ''
);

CREATE TABLE "kanjis_meanings" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "kanji" mediumint(9) NOT NULL,
  "meaning" int(11) UNIQUE NOT NULL
);

CREATE TABLE "kanjis_readings" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "kanji" mediumint(9) NOT NULL,
  "reading" int(11) NOT NULL DEFAULT 0,
  "is_kun" bit(1) NOT NULL,
  "is_name" bit(1) NOT NULL,
  "is_on" bit(1) NOT NULL
);

CREATE TABLE "meanings" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "meaning" varchar(255) UNIQUE NOT NULL
);

CREATE TABLE "readings" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "reading" varchar(50) NOT NULL
);

CREATE TABLE "users" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "username" varchar(50) UNIQUE NOT NULL,
  "bio" varchar(200),
  "email" varchar(255) UNIQUE NOT NULL,
  "password" varchar(255) NOT NULL,
  "exp" int(11) NOT NULL DEFAULT 0,
  "flame" int(11) NOT NULL DEFAULT 0,
  "role" tinyint(4) NOT NULL DEFAULT 0,
  "disabled" bit(1) NOT NULL DEFAULT 0,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
  "updated_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "follows" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "user_id" int(11) NOT NULL,
  "follow_id" int(11) NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "blocks" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "user_id" int(11) NOT NULL,
  "block_id" int(11) NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "scores" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "user_id" int(11) NOT NULL,
  "kanji_id" mediumint(9) NOT NULL,
  "scored" tinyint(4) NOT NULL,
  "out_of" tinyint(4) NOT NULL,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
  "updated_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "kanji_comments" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "user_id" int(11) NOT NULL,
  "kanji_id" mediumint(9) NOT NULL,
  "message" varchar(2000) NOT NULL,
  "deleted" bit(1) NOT NULL DEFAULT 0,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
  "updated_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "direct_messages" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "sender_id" int(11) NOT NULL,
  "receiver_id" int(11) NOT NULL,
  "message" varchar(2000) NOT NULL,
  "read" bit(1) DEFAULT 0,
  "deleted" bit(1) NOT NULL DEFAULT 0,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP()),
  "updated_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "votes" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "comment_id" bigint(20) NOT NULL,
  "user_id" int(11) NOT NULL,
  "vote" tinyint(4) NOT NULL
);

CREATE TABLE "quizzes" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "user_id" int(11) NOT NULL,
  "done" bit(1) NOT NULL DEFAULT 0,
  "created_at" timestamp NOT NULL DEFAULT (CURRENT_TIMESTAMP())
);

CREATE TABLE "questions" (
  "id" SERIAL PRIMARY KEY NOT NULL,
  "quiz_id" bigint(20) NOT NULL,
  "kanji_id" mediumint(9) NOT NULL,
  "question_type" tinyint(4) NOT NULL,
  "correct_answer" varchar(255) NOT NULL,
  "wrong_answer_1" varchar(255),
  "wrong_answer_2" varchar(255),
  "wrong_answer_3" varchar(255),
  "given_answer" varchar(255)
);

CREATE INDEX "FK-m_kanji" ON "kanjis_meanings" ("kanji");

CREATE INDEX "FK-m_meaning" ON "kanjis_meanings" ("meaning");

CREATE INDEX "FK-r_kanji" ON "kanjis_readings" ("kanji");

CREATE INDEX "FK-r_reading" ON "kanjis_readings" ("reading");

CREATE INDEX "FK-f_user" ON "follows" ("user_id");

CREATE INDEX "FK-f_follow" ON "follows" ("follow_id");

CREATE INDEX "FK-b_user" ON "blocks" ("user_id");

CREATE INDEX "FK-b_block" ON "blocks" ("block_id");

CREATE INDEX "FK-s_user" ON "scores" ("user_id");

CREATE INDEX "FK-s_kanji" ON "scores" ("kanji_id");

CREATE INDEX "FK-kc_user" ON "kanji_comments" ("user_id");

CREATE INDEX "FK-kc_kanji" ON "kanji_comments" ("kanji_id");

CREATE INDEX "FK-dm_sender" ON "direct_messages" ("sender_id");

CREATE INDEX "FK-dm_receiver" ON "direct_messages" ("receiver_id");

CREATE INDEX "FK-v_comment" ON "votes" ("comment_id");

CREATE INDEX "FK-v_user" ON "votes" ("user_id");

ALTER TABLE "kanjis_meanings" ADD CONSTRAINT "FK-m_kanji" FOREIGN KEY ("kanji") REFERENCES "kanjis" ("id");

ALTER TABLE "kanjis_meanings" ADD CONSTRAINT "FK-m_meaning" FOREIGN KEY ("meaning") REFERENCES "meanings" ("id");

ALTER TABLE "kanjis_readings" ADD CONSTRAINT "FK-r_kanji" FOREIGN KEY ("kanji") REFERENCES "kanjis" ("id");

ALTER TABLE "kanjis_readings" ADD CONSTRAINT "FK-r_reading" FOREIGN KEY ("reading") REFERENCES "readings" ("id");

ALTER TABLE "scores" ADD CONSTRAINT "FK-s_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "scores" ADD CONSTRAINT "FK-s_kanji" FOREIGN KEY ("kanji_id") REFERENCES "kanjis" ("id");

ALTER TABLE "follows" ADD CONSTRAINT "FK-f_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "follows" ADD CONSTRAINT "FK-f_follow" FOREIGN KEY ("follow_id") REFERENCES "users" ("id");

ALTER TABLE "blocks" ADD CONSTRAINT "FK-b_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "blocks" ADD CONSTRAINT "FK-b_block" FOREIGN KEY ("block_id") REFERENCES "users" ("id");

ALTER TABLE "kanji_comments" ADD CONSTRAINT "FK-kc_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "kanji_comments" ADD CONSTRAINT "FK-kc_kanji" FOREIGN KEY ("kanji_id") REFERENCES "kanjis" ("id");

ALTER TABLE "direct_messages" ADD CONSTRAINT "FK-dm_sender" FOREIGN KEY ("sender_id") REFERENCES "users" ("id");

ALTER TABLE "direct_messages" ADD CONSTRAINT "FK-dm_receiver" FOREIGN KEY ("receiver_id") REFERENCES "users" ("id");

ALTER TABLE "votes" ADD CONSTRAINT "FK-v_comment" FOREIGN KEY ("comment_id") REFERENCES "kanji_comments" ("id");

ALTER TABLE "votes" ADD CONSTRAINT "FK-v_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "quizzes" ADD CONSTRAINT "FK-qz_user" FOREIGN KEY ("user_id") REFERENCES "users" ("id");

ALTER TABLE "questions" ADD CONSTRAINT "FK-qt_quiz" FOREIGN KEY ("quiz_id") REFERENCES "quizzes" ("id");

ALTER TABLE "questions" ADD CONSTRAINT "FK-qt_kanji" FOREIGN KEY ("kanji_id") REFERENCES "kanjis" ("id");
