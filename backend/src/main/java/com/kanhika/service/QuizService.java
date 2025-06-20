package com.kanhika.service;

import com.kanhika.dto.quiz.*;
import com.kanhika.exception.InvalidFormatException;
import com.kanhika.exception.ResourceNotFoundException;
import com.kanhika.model.*;
import com.kanhika.repository.*;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.util.*;


@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final KanjiRepository kanjiRepository;
    private final MeaningRepository meaningRepository;
    private final ReadingRepository readingRepository;
    private final UserRepository userRepository;
    private final ScoreRepository scoreRepository;

    public QuizService(QuizRepository quizRepository,
                       QuestionRepository questionRepository,
                       KanjiRepository kanjiRepository,
                       MeaningRepository meaningRepository,
                       ReadingRepository readingRepository,
                       UserRepository userRepository,
                       ScoreRepository scoreRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.kanjiRepository = kanjiRepository;
        this.meaningRepository = meaningRepository;
        this.readingRepository = readingRepository;
        this.userRepository = userRepository;
        this.scoreRepository = scoreRepository;
    }

    public QuizOutDTO getQuiz(String username) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        // Get all uncleared quizzes
        List<Quiz> quizzes = quizRepository.findAllByUserAndDoneFalseOrderByCreatedAtDesc(user);
        // If no quizzes, return -1
        if (quizzes.isEmpty()) {
            return new QuizOutDTO(-1);
        }

        // Return the last quiz id
        return new QuizOutDTO(quizzes.getFirst().getId());
    }

    public QuizOutDTO makeQuiz(String username, QuizInDTO request) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        // Check request validity
        String[] questionsType = {"meaning", "reading", "mix"};
        if (!Arrays.asList(questionsType).contains(request.questions_type())) {
            throw new InvalidFormatException("Questions type must be one of the following: " + Arrays.toString(questionsType));
        }

        String[] scope = {"grade", "jlpt", "all"};
        if (!Arrays.asList(scope).contains(request.difficulty_type())) {
            throw new InvalidFormatException("Difficulty type must be one of the following: " + Arrays.toString(scope));
        }
        switch (request.difficulty_type()) {
            case "grade":
                // Grade level must be between 1 and 6 or 8
                if (request.difficulty_number() < 1 || request.difficulty_number() > 8 || request.difficulty_number() == 7) {
                    throw new InvalidFormatException("Difficulty number for type grade must be between 1 and 6 or 8");
                }
                break;
            case "jlpt":
                // JLPT level must be between 5 and 1
                if (request.difficulty_number() < 1 || request.difficulty_number() > 5) {
                    throw new InvalidFormatException("Difficulty number for type jlpt must be between 5 and 1");
                }
        }

        // Close uncleared quizzes
        List<Quiz> uncleared = quizRepository.findAllByUserAndDoneFalseOrderByCreatedAtDesc(user);
        uncleared.forEach(q -> {
            q.setDone(true);
            quizRepository.save(q);
        });

        // Create the quiz object
        Quiz quiz = new Quiz();

        // Set the quiz properties
        quiz.setUser(user);
        quiz.setQuestionsNumber(request.questions_number());
        quiz.setQuestionsType(request.questions_type());
        quiz.setQuiz_scope(request.difficulty_type());
        quiz.setQuiz_level(request.difficulty_number());

        // Save the quiz
        quizRepository.save(quiz);

        return new QuizOutDTO(quiz.getId());
    }

    public QuestionDTO getQuestion(String username, int quiz_id) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        Quiz quiz = quizRepository.findByUserAndIdAndDoneFalse(user, quiz_id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found."));

        Question question = questionRepository.findByQuizAndGivenAnswerNull(quiz)
                .orElse(new Question());

        if (question.getCorrectAnswer() == null) {
            // Get previous questions
            List<Question> oldQuestions = questionRepository.findByQuizAndGivenAnswerNotNull(quiz);

            // Roll a random kanji
            // Get grade or jlpt level
            int grade = -1;
            int jlpt = -1;
            if (quiz.getQuiz_scope().equals("grade"))
                grade = quiz.getQuiz_level();
            if (quiz.getQuiz_scope().equals("jlpt"))
                jlpt = quiz.getQuiz_level();

            // Get all possible ids
            List<Integer> kanjiIds = kanjiRepository.findAllIdByGradeAndJlpt(
                    grade == -1 ? null : grade,
                    jlpt == -1 ? null : jlpt
            );

            // Get already answered ids
            List<Integer> excludeIds = oldQuestions.stream()
                    .map(q -> q.getKanji().getId())
                    .toList();

            // Filter the possible ids
            List<Integer> kanjiPoll = kanjiIds.stream().filter(id -> !excludeIds.contains(id)).toList();

            // Get the question kanji
            Kanji kanji = kanjiRepository.findById(kanjiPoll.get(new Random().nextInt(kanjiPoll.size())));


            // Prepare the question
            // Set the question type
            // Meaning can have 0 or 1, reading 2 or 3, mix can have all 4
            int maxType = 2;
            int offsetType = 0;
            if (quiz.getQuestionsType().equals("mix"))
                maxType = 4;
            if (quiz.getQuestionsType().equals("reading"))
                offsetType = 2;

            // Roll the question type
            int questionType = new Random().nextInt(maxType) + offsetType;

            // Init variables
            String correctAnswer = "";
            Set<String> wrongAnswers = new HashSet<>();

            System.out.println(questionType + " " + kanji.getKanji());

            if (questionType == 0) {
                // Find the meaning of a given kanji
                // Get one of the kanji meanings if multiple
                List<Meaning> correctAnswers = meaningRepository.findAllByKanji(kanji.getKanji());
                correctAnswer = correctAnswers.get(new Random().nextInt(correctAnswers.size())).getMeaning();

                // Get all possible meanings for this grade/jlpt
                List<Meaning> allAnswers = meaningRepository.findAllByGradeAndJlpt(
                        grade == -1 ? null : grade,
                        jlpt == -1 ? null : jlpt
                );

                // Remove meanings matching the correct answer meanings
                List<Meaning> wrongAnswersPoll =
                        allAnswers.stream()
                                .filter(meaning -> !correctAnswers.contains(meaning))
                                .toList();

                // Choose 3 random meanings
                while (wrongAnswers.size() < 3) {
                    wrongAnswers.add(wrongAnswersPoll.get(new Random().nextInt(wrongAnswersPoll.size())).getMeaning());
                }
            }
            else if (questionType == 1) {
                // Find the kanji from a given meaning
                // Set the correct kanji
                correctAnswer = kanji.getKanji();

                // Get all meanings from correct kanji
                List<Meaning> excludeMeanings = meaningRepository.findAllByKanji(correctAnswer);

                // Get all possible meanings for this grade/jlpt
                List<Meaning> allAnswers = meaningRepository.findAllByGradeAndJlpt(
                        grade == -1 ? null : grade,
                        jlpt == -1 ? null : jlpt
                );

                // Remove meanings matching the correct answer meanings
                List<Meaning> wrongAnswersPoll =
                        allAnswers.stream()
                                .filter(meaning -> !excludeMeanings.contains(meaning))
                                .toList();
                System.out.println(wrongAnswersPoll.get(new Random().nextInt(wrongAnswersPoll.size())).getMeaning());
                // Choose 3 random kanjis that doesn't match with the correct kanji meanings
                while (wrongAnswers.size() < 3) {
                    List<Kanji> wrongKanjis = meaningRepository.findAllByMeaningAndGradeAndJlpt(
                            wrongAnswersPoll.get(new Random().nextInt(wrongAnswersPoll.size())).getMeaning(),
                            grade == -1 ? null : grade,
                            jlpt == -1 ? null : jlpt);
                    wrongAnswers.add(wrongKanjis.get(new Random().nextInt(wrongKanjis.size())).getKanji());
                }
            }
            else if (questionType == 2) {
                // Find the reading of a given kanji
                // Get one of the kanji readings if multiple
                List<Reading> correctAnswers = readingRepository.findAllByKanji(kanji.getKanji());
                correctAnswer = correctAnswers.get(new Random().nextInt(correctAnswers.size())).getReading();

                // Get all possible meanings for this grade/jlpt
                List<Reading> allAnswers = readingRepository.findAllByGradeAndJlpt(
                        grade == -1 ? null : grade,
                        jlpt == -1 ? null : jlpt
                );

                // Remove meanings matching the correct answer meanings
                List<Reading> wrongAnswersPoll =
                        allAnswers.stream()
                                .filter(reading -> !correctAnswers.contains(reading))
                                .toList();

                // Choose 3 random meanings
                while (wrongAnswers.size() < 3) {
                    wrongAnswers.add(wrongAnswersPoll.get(new Random().nextInt(wrongAnswersPoll.size())).getReading());
                }
            }
            else if (questionType == 3) {
                // Find the kanji from a given reading
                // Set the correct kanji
                correctAnswer = kanji.getKanji();

                // Get all readings from correct kanji
                List<Reading> excludeReadings = readingRepository.findAllByKanji(correctAnswer);

                // Get all possible readings for this grade/jlpt
                List<Reading> allAnswers = readingRepository.findAllByGradeAndJlpt(
                        grade == -1 ? null : grade,
                        jlpt == -1 ? null : jlpt
                );

                // Remove readings matching the correct answer readings
                List<Reading> wrongAnswersPoll =
                        allAnswers.stream()
                                .filter(reading -> !excludeReadings.contains(reading))
                                .toList();
                System.out.println("fdp répond:" + wrongAnswersPoll.get(new Random().nextInt(wrongAnswersPoll.size())).getReading());

                // Choose 3 random kanjis that doesn't match with the correct kanji readings
                while (wrongAnswers.size() < 3) {
                    List<Kanji> wrongKanjis = readingRepository.findAllByReadingAndGradeAndJlpt(
                            wrongAnswersPoll.get(new Random().nextInt(wrongAnswersPoll.size())).getReading(),
                            grade == -1 ? null : grade,
                            jlpt == -1 ? null : jlpt);

                    wrongAnswers.add(wrongKanjis.get(new Random().nextInt(wrongKanjis.size())).getKanji());
                }
            }
            else {
                throw new RuntimeException("Internal server error.");
            }

            // Convert to list
            List<String> wrongAnswersList = wrongAnswers.stream().toList();


            // Final assembly
            question.setQuiz(quiz);
            question.setKanji(kanji);
            question.setQuestionType(questionType);
            question.setCorrectAnswer(correctAnswer);
            question.setWrongAnswer1(wrongAnswersList.get(0));
            question.setWrongAnswer2(wrongAnswersList.get(1));
            question.setWrongAnswer3(wrongAnswersList.get(2));

            questionRepository.save(question);
        }

        // Get the random variable seeded by the question
        Random ran = getRandom(question);

        // Generate the question message
        String message = "";
        if (question.getQuestionType() == 0) {
            // Find the meaning of a given kanji
            message = "What's the meaning of the following kanji? [" + question.getKanji().getKanji() + "]";
        }
        else if (question.getQuestionType() == 1) {
            // Find the kanji from a given meaning
            List<Meaning> meanings = meaningRepository.findAllByKanji(question.getKanji().getKanji());
            Collections.shuffle(meanings, ran);
            message = "Which kanji match the following meaning? [" + meanings.getFirst().getMeaning() + "]";
        }
        else if (question.getQuestionType() == 2) {
            // Find the meaning of a given kanji
            message = "How to read the following kanji? [" + question.getKanji().getKanji() + "]";
        }
        else if (question.getQuestionType() == 3) {
            // Find the kanji from a given meaning
            List<Reading> readings = readingRepository.findAllByKanji(question.getKanji().getKanji());
            Collections.shuffle(readings, ran);
            message = "Which kanji reads like the following? [" + readings.getFirst().getReading() + "]";
        }
        else {
            throw new RuntimeException("Internal server error.");
        }

        // Shuffle the answers using creation date as seed
        List<String> answers = shuffleAnswers(question);

        return new QuestionDTO(
                question.getId(),
                message,
                answers.get(0),
                answers.get(1),
                answers.get(2),
                answers.get(3)
        );
    }

    public ResultDTO sendAnswer(String username, int quiz_id, int question_id, int answerNo) {
        User user = userRepository.findByUsernameIgnoreCaseAndDisabledFalse(username)
                .orElseThrow(() -> new UsernameNotFoundException("Unexpected error."));

        Quiz quiz = quizRepository.findByUserAndIdAndDoneFalse(user, quiz_id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found."));

        Question question = questionRepository.findByIdAndGivenAnswerNull(question_id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found."));

        Score score = scoreRepository.findByUserAndKanji(user, question.getKanji())
                .orElseGet(() -> {
                    Score newScore = new Score();
                    newScore.setUser(user);
                    newScore.setKanji(question.getKanji());
                    newScore.setScored(0);
                    newScore.setOutOf(0);
                    return newScore;
                });

        // Retrieve the answers, shuffled the same way as before
        List<String> answers = shuffleAnswers(question);

        // Send the answer
        question.setGivenAnswer(answers.get(answerNo));
        questionRepository.save(question);

        // Increase the OutOf number of score
        score.setOutOf(score.getOutOf() + 1);

        // Check if given answer match the correct answer
        boolean answerIs = false;
        if (questionRepository.isAnswerCorrect(question)) {
            answerIs = true;

            // Increase the Scored number of score
            score.setScored(score.getScored() + 1);
        }

        // Save score
        scoreRepository.save(score);

        // Get the total of question answered during the quiz
        int quizTotal = questionRepository.countByQuizAndGivenAnswerNotNull(quiz);

        // Get the total of correct answers during the quiz
        int quizScore = questionRepository.countCorrectAnswer(quiz);

        // Check if it was the last question of the quiz session
        boolean end = false;
        if (quizTotal == quiz.getQuestionsNumber()) {
            end = true;

            // End the quiz
            quiz.setDone(true);
            quizRepository.save(quiz);
        }

        return new ResultDTO(
                answerIs,
                answers.get(answerNo),
                quizScore,
                quizTotal,
                end
        );
    }


    private List<String> shuffleAnswers(Question question) {
        Random ran = getRandom(question);

        List<String> answers = new ArrayList<>();
        answers.add(question.getCorrectAnswer());
        answers.add(question.getWrongAnswer1());
        answers.add(question.getWrongAnswer2());
        answers.add(question.getWrongAnswer3());

        Collections.shuffle(answers, ran);
        
        return answers;
    }

    private Random getRandom(Question question) {
        Random ran = new Random();
        ran.setSeed(question.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        return ran;
    }
}
