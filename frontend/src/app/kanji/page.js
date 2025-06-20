'use client';

import { useState, useEffect } from 'react';
import '@/app/kanji/page.css';
import styles from './page.module.css';
import Link from 'next/link';
import Cookies from 'js-cookie';

export default function LearnPage() {
  const [search, setSearch] = useState('');
  const [gradeFilter, setGradeFilter] = useState(null);
  const [jlptFilter, setJlptFilter] = useState(null);
  const [kanjiList, setKanjiList] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const token = Cookies.get('token');

  useEffect(() => {
    const fetchKanjiData = async () => {
      try {
        const params = new URLSearchParams();
        if (gradeFilter) params.append('grade', gradeFilter);
        if (jlptFilter) params.append('jlpt', jlptFilter);
        params.append('page', currentPage || 1);

        const response = await fetch(
          `http://localhost:8080/api/kanjis/search${
            search ? '/' + search : ''
          }?${params.toString()}`,
          {
            method: 'GET',
            headers: {
              'Content-Type': 'application/json',
              Authorization: `Bearer ${token}`,
            },
          }
        );
        if (!response.ok) throw new Error('Failed to fetch kanji');

        const data = await response.json();
        setKanjiList(data.kanjis);
        setTotalPages(data.totalPages);
      } catch (err) {
        console.error('Error fetching kanji:', err);
        setKanjiList([]);
      }
    };

    fetchKanjiData();
  }, [search, gradeFilter, jlptFilter, currentPage]);

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Learn Kanji</h1>

      <div className={styles.filters}>
        <div>
          <label>Grade: </label>
          {[1, 2, 3, 4, 5, 6, 8].map((g) => (
            <button
              key={g}
              className={gradeFilter === g ? styles.selected : ''}
              onClick={() => setGradeFilter(g === gradeFilter ? null : g)}
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
              className={jlptFilter === j ? styles.selected : ''}
              onClick={() => setJlptFilter(j === jlptFilter ? null : j)}
            >
              {j}
            </button>
          ))}
        </div>
      </div>

      <input
        type="text"
        placeholder="Search..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        className={styles.search}
      />

      <table className={styles.table}>
        <thead>
          <tr>
            <th>Kanji</th>
            <th>Grade</th>
            <th>JLPT</th>
            <th>Meaning</th>
          </tr>
        </thead>
        <tbody>
          {kanjiList.map((k, index) => (
            <tr key={index}>
              <td className="fixed">
                <Link href={`/kanji/${k.kanji}`}>{k.kanji}</Link>
              </td>
              <td className="fixed">
                <Link href={`/kanji/${k.kanji}`}>
                  {k.grade == 0 ? '-' : k.grade}
                </Link>
              </td>
              <td className="fixed">
                <Link href={`/kanji/${k.kanji}`}>
                  {k.jlpt == 0 ? '-' : k.jlpt}
                </Link>
              </td>
              <td>
                <Link href={`/kanji/${k.kanji}`}>{k.meanings[0]}</Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      <div className={styles.pagination}>
        <button
          onClick={() => setCurrentPage((p) => Math.max(p - 1, 1))}
          disabled={currentPage === 1}
        >
          Prev
        </button>
        <span>
          Page {currentPage} / {totalPages || 1}
        </span>
        <button
          onClick={() => setCurrentPage((p) => Math.min(p + 1, totalPages))}
          disabled={currentPage === totalPages}
        >
          Next
        </button>
      </div>
    </div>
  );
}
