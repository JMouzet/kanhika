'use client';

import { useEffect, useState } from 'react';
import { useParams } from 'next/navigation';
import styles from './page.module.css';
import Cookies from 'js-cookie';
import './page.css';
import Link from 'next/link';
import { useUser } from "@/contexts/userContext";

export default function KanjiPage() {
  const { kanji } = useParams();
  const [dataKanji, setDataKanji] = useState(null);
  const [dataComments, setDataComments] = useState([]);
  const [error, setError] = useState(false);
  const [newComment, setNewComment] = useState('');
  const [isSending, setIsSending] = useState(false);
  const [editingId, setEditingId] = useState(null);
  const [editedMessage, setEditedMessage] = useState('');
  const myUser = useUser();
  const token = Cookies.get('token');

  useEffect(() => {
    const fetchKanji = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/kanjis/${kanji}`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}}`,
          },
        });
        if (!res.ok) throw new Error();
        const json = await res.json();
        setDataKanji(json);
      } catch (e) {
        setError(true);
      }
    };

    const fetchComments = async () => {
      try {
        const res = await fetch(`http://localhost:8080/api/kanjis/${kanji}/comments`, {
          method: 'GET',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
        });
        if (!res.ok) throw new Error();
        const comments = await res.json();
        comments.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
        setDataComments(comments);
      } catch (e) {
        console.error('Failed to fetch comments:', e);
      }
    };

    if (kanji) {
      fetchKanji();
      fetchComments();
    }
  }, [kanji]);

  const handleSendComment = async () => {
    if (!newComment.trim()) return;

    setIsSending(true);
    try {
      const token = Cookies.get('token');
      const res = await fetch(`http://localhost:8080/api/kanjis/${kanji}/comments`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ message: newComment })
      });

      if (!res.ok) throw new Error('Comment post failed');
      const added = await res.json();
      setDataComments([added, ...dataComments]);
      setNewComment('');
    } catch (err) {
      alert('Failed to post comment: ', err.message);
    } finally {
      setIsSending(false);
    }
  };

  const handleVote = async (commentId, value) => {
    try {
      const token = Cookies.get('token');
      let route;
      switch (value) {
        case 1:
          route = `http://localhost:8080/api/comments/${commentId}/vote/up`;
          break;
        case -1:
          route = `http://localhost:8080/api/comments/${commentId}/vote/down`;
          break;
        default:
          route = `http://localhost:8080/api/comments/${commentId}/vote`;
          break;
      }
      const res = await fetch(route, {
        method: value === 0 ? 'DELETE' : 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ vote: value })
      });

      if (!res.ok) throw new Error();
      const updatedComments = dataComments.map(comment => {
        if (comment.id === commentId) {
          return {
            ...comment,
            vote: comment.vote + (value - comment.userVote),
            userVote: value
          };
        }
        return comment;
      });
      setDataComments(updatedComments);
    } catch {
      alert('Failed to vote');
    }
  };

  const handleEdit = (id, message) => {
    setEditingId(id);
    setEditedMessage(message);
  };

  const saveEdit = async (id) => {
    try {
      const token = Cookies.get('token');
      const res = await fetch(`http://localhost:8080/api/comments/${id}`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ message: editedMessage })
      });

      if (!res.ok) throw new Error();
      if (res.status === 204) {
        setEditingId(null);
        setEditedMessage('');
        return;
      }
      
      const updated = await res.json();

      setDataComments(dataComments.map(c => c.id === id ? { ...c, message: updated.message, updatedAt: updated.updatedAt } : c));
      setEditingId(null);
      setEditedMessage('');
    } catch {
      alert('Failed to edit comment');
    }
  };

  const handleDelete = async (id) => {
    if (!confirm("Are you sure you want to delete this comment?")) return;

    try {
      const token = Cookies.get('token');
      const res = await fetch(`http://localhost:8080/api/comments/${id}`, {
        method: 'DELETE',
        headers: {
          'Authorization': `Bearer ${token}`
        }
      });

      if (!res.ok) throw new Error();
      setDataComments(dataComments.filter(c => c.id !== id));
    } catch {
      alert('Failed to delete comment');
    }
  };

  if (error) return <div className={styles.container}><p className={styles.message}>Kanji not found.</p></div>;
  if (!dataKanji) return <div className={styles.container}><p className={styles.message}>Loading...</p></div>;

  return (
    <div className={styles.container}>
      <h1 className={styles.kanji}>{dataKanji.kanji}</h1>

      <div className={styles.details}>
        <p><b>Grade:</b> {dataKanji.grade}</p>
        <p><b>JLPT:</b> N{dataKanji.jlpt}</p>
        <p><b>Strokes:</b> {dataKanji.strokes}</p>

        <p><b>Meanings:</b> {dataKanji.meanings.join(', ')}</p>

        <p><b>On-readings:</b> {dataKanji.onReadingsKana.join(', ')} ({dataKanji.onReadingsRoma.join(', ')})</p>
        <p><b>Kun-readings:</b> {dataKanji.kunReadingsKana.join(', ')} ({dataKanji.kunReadingsRoma.join(', ')})</p>
      </div>

      <hr className={styles.separator} />

      <div className={styles.comments}>
        <h2>Comments</h2>

        <div className={styles.commentForm}>
          <textarea
            value={newComment}
            onChange={(e) => setNewComment(e.target.value)}
            placeholder="Write a comment..."
            rows={3}
          />
          <button onClick={handleSendComment} disabled={isSending}>
            {isSending ? 'Sending...' : 'Send'}
          </button>
        </div>

        {dataComments.length === 0 ? (
          <p>No comments yet.</p>
        ) : (
          dataComments.map((comment) => (
            <div key={comment.id} className={styles.comment}>
              <div className={styles.commentHeader}>
                <b>
                  <Link href={`/users/${comment.username}`} className={styles.commentUsername}>
                    {comment.username}
                  </Link>
                  </b>
                <span className={styles.commentDate}>
                  <i>
                    {comment.createdAt != comment.updatedAt ? '(Edited) ' : ' '}
                  </i>
                  {new Date(comment.createdAt).toLocaleDateString()}
                  {' '}
                  {new Date(comment.createdAt).toLocaleTimeString()}
                </span>
              </div>
              {editingId === comment.id ? (
                <>
                  <textarea
                    value={editedMessage}
                    onChange={(e) => setEditedMessage(e.target.value)}
                    className={styles.editBox}
                  />
                  <button
                    className={styles.saveButton}
                    onClick={() => saveEdit(comment.id)}
                  >
                    Save
                  </button>
                  <button
                    className={styles.cancelButton}
                    onClick={() => setEditingId(null)}
                  >
                    Cancel
                  </button>
                </>
              ) : (
                <p className={styles.commentMessage}>{comment.message}</p>
              )}
              <div className={styles.commentActions}>
                <div className={styles.commentVotes}>
                  <button
                    className={comment.userVote === 1 ? styles.activeUpvote : styles.inactiveUpvote}
                    onClick={() => handleVote(comment.id, comment.userVote === 1 ? 0 : 1)}
                  >
                    ▲
                  </button>
                  {comment.vote}
                  <button
                    className={comment.userVote === -1 ? styles.activeDownvote : styles.inactiveDownvote}
                    onClick={() => handleVote(comment.id, comment.userVote === -1 ? 0 : -1)}
                  >
                    ▼
                  </button>
                </div>

                {myUser.username === comment.username && (
                  <>
                    <div className={styles.commentEdit}>
                      <button
                        className={styles.editButton}
                        onClick={() => handleEdit(comment.id, comment.message)}
                      >
                        Edit
                      </button>
                      <button
                        className={styles.deleteButton}
                        onClick={() => handleDelete(comment.id)}
                      >
                        Delete
                      </button>
                    </div>
                  </>
                )}
              </div>
            </div>
          ))
        )}
      </div>
    </div>
  );
}
