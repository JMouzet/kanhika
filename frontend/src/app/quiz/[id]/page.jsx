'use client';

import { useEffect, useState } from 'react';
import { useParams, useRouter } from 'next/navigation';
import Cookies from 'js-cookie';
import styles from './page.module.css';

export default function QuizPage() {
  const { id } = useParams();
  const router = useRouter();
  const [question, setQuestion] = useState(null);
  const [feedback, setFeedback] = useState(null);
  const [selected, setSelected] = useState(null);
  const [loading, setLoading] = useState(true);
  const API_URL = process.env.NEXT_PUBLIC_API_URL

  const token = Cookies.get('token');

  const fetchQuestion = async () => {
    try {
      const res = await fetch(`${API_URL}/api/quizzes/${id}/questions`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      const data = await res.json();
      setQuestion(data);
      setFeedback(null);
      setSelected(null);
      setLoading(false);
    } catch {
      alert("Failed to fetch question");
      router.push('/quiz');
    }
  };

  useEffect(() => {
    fetchQuestion();
  }, []);

  const handleAnswer = async (choice) => {
    setSelected(choice);

    try {
      const res = await fetch(`${API_URL}/api/quizzes/${id}/questions/${question.id}`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ answerNo: choice })
      });

      const data = await res.json();
      console.log("Feedback reçu:", data);
      console.log("Question:", question);
      setFeedback(data);

      if (data.endOfQuiz) {
        setTimeout(() => {
          router.push('/quiz');
        }, 2000);
      } else {
        setTimeout(fetchQuestion, 1500);
      }
    } catch {
      alert("Failed to send answer");
    }
  };

  if (loading || !question) return <div className={styles.container}><p className={styles.loading}>Loading...</p></div>;

  if (feedback) {
    console.log("Selected:", selected);
    console.log("Feedback.rightAnswer:", feedback.rightAnswer);
    [0, 1, 2, 3].forEach(i => {
      console.log(`Answer${i}:`, question[`answer${i}`]);
    });
  }

  const match = question.question.match(/\[(.*?)\]/);
  const bracketText = match ? match[1] : null;

  return (
    <div className={styles.container}>
      <div className={styles.card}>
        <h2 className={styles.title}>{question.question.replace(/\s*\[.*?\]\s*/, '')}</h2>

        {bracketText && (
          <div className={styles.bracketText}>
            {bracketText}
          </div>
        )}

        <div className={styles.choices}>
          {[0, 1, 2, 3].map((i) => {
            const value = question[`answer${i}`];
            
            const isCorrect = feedback && value === feedback.rightAnswer;
            const isIncorrect = feedback && value !== feedback.rightAnswer;
            
            return (
              <button
                key={i}
                className={`
                  ${styles.choice}
                  ${isCorrect ? styles.correct : ''}
                  ${isIncorrect ? styles.incorrect : ''}
                `}
                disabled={!!feedback}
                onClick={() => handleAnswer(i)}
              >
                {value}
              </button>
            );
          })}
        </div>

        {feedback && (
          <div className={styles.feedback}>
            {feedback.answerIs ? "Correct!" : "Incorrect."}
            <div>
              Score: {feedback.scored} / {feedback.outOf}
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
