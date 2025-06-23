'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import Cookies from 'js-cookie';
import styles from './page.module.css';

export default function QuizHomePage() {
  const [grade, setGrade] = useState(null);
  const [jlpt, setJlpt] = useState(null);
  const [mode, setMode] = useState('both');
  const [existingQuiz, setExistingQuiz] = useState(null);
  const [questionCount, setQuestionCount] = useState(10);
  const router = useRouter();
  const token = Cookies.get('token');
  const API_URL = process.env.NEXT_PUBLIC_API_URL

  useEffect(() => {
    const fetchQuiz = async () => {
      try {
        const res = await fetch(`${API_URL}/api/quizzes`, {
          method: 'GET',
          headers: { Authorization: `Bearer ${token}` },
        });
        if (res.ok) {
          const quiz = await res.json();
          if (quiz.id > 0) {
            setExistingQuiz(quiz);
          }
        }
      } catch (err) {
        console.error('No quiz in progress');
      }
    };

    fetchQuiz();
  }, []);

  const handleStart = async () => {
    const questions_type = mode === 'translation' ? 'meaning' : mode === 'reading' ? 'reading' : 'mix';
    const questions_number = questionCount;
    const difficulty_type = grade ? 'grade' : jlpt ? 'jlpt' : 'all';
    const difficulty_number = grade || jlpt || 0;

    try {
      const res = await fetch(`${API_URL}/api/quizzes`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          questions_type,
          questions_number,
          difficulty_type,
          difficulty_number
        })
      });

      if (!res.ok) throw new Error();

      const { id } = await res.json();
      router.push(`/quiz/${id}`);
    } catch (err) {
      alert('Failed to start quiz');
    }
  };

  const handleResume = () => {
    if (existingQuiz?.id) {
      router.push(`/quiz/${existingQuiz.id}`);
    }
  };

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h1 className={styles.title}>Quiz</h1>

        {existingQuiz ? (
          <div className={styles.resumeBox}>
            <p>You have an unfinished quiz.</p>
            <button onClick={handleResume}>Resume Quiz</button>
          </div>
        ) : (
          <>
            <div className={styles.form}>
              <div className={styles.filters}>
                <div>
                  <label>Grade: </label>
                  {[1, 2, 3, 4, 5, 6, 8].map((g) => (
                    <button
                      key={g}
                      className={grade === g ? styles.selected : ''}
                      onClick={() => {
                        if (grade === g) {
                          setGrade(null);
                        } else {
                          setGrade(g);
                          setJlpt(null);
                        }
                      }}
                    >
                      {g}
                    </button>
                  ))}
                </div>

                <div>
                  <label>JLPT: </label>
                  {[5, 4, 3, 2, 1].map((j) => (
                    <button
                      key={j}
                      className={jlpt === j ? styles.selected : ''}
                      onClick={() => {
                        if (jlpt === j) {
                          setJlpt(null);
                        } else {
                          setJlpt(j);
                          setGrade(null);
                        }
                      }}
                    >
                      {j}
                    </button>
                  ))}
                </div>

                <div>
                  <label>Mode: </label>
                  {["Translation", "Reading", "Both"].map((m) => (
                    <button
                      key={m}
                      className={mode === m.toLowerCase() ? styles.selected : ''}
                      onClick={() => setMode(m.toLowerCase())}
                    >
                      {m}
                    </button>
                  ))}
                </div>

                <div>
                  <label>
                    Number of questions: {questionCount}
                    <input
                      type="range"
                      min="5"
                      max="30"
                      step="5"
                      value={questionCount}
                      onChange={(e) => setQuestionCount(parseInt(e.target.value))}
                      className={styles.slider}
                      style={{
                        background: `linear-gradient(to right, hotpink 0%, hotpink ${((questionCount - 5) / 25) * 100}%, #eee ${((questionCount - 5) / 25) * 100}%, #eee 100%)`
                      }}
                    />
                  </label>
                </div>
              </div>
            </div>

            <button className={styles.button} onClick={handleStart}>Start New Quiz</button>
          </>
        )}
      </div>
    </div>
  );
}
