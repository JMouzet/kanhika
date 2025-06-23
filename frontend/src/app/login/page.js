'use client';
import { useState, useEffect } from 'react';
import Link from 'next/link';
import Cookies from 'js-cookie';
import '@/app/login/page.css';

export default function LoginPage() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);
  const API_URL = process.env.NEXT_PUBLIC_API_URL

  useEffect(() => {
    const input = document.querySelector('input[name="username"]');
    if (input && input.value) {
      setUsername(input.value);
    }
  }, []);
  useEffect(() => {
    const input = document.querySelector('input[name="password"]');
    if (input && input.value) {
      setPassword(input.value);
    }
  }, []);

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError(null);

    try {
      const res = await fetch(`${API_URL}/api/auth/login`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, password }),
      });

      if (!res.ok) {
        throw new Error('Invalid credentials');
      }

      const data = await res.json();

      Cookies.set('token', data.token, {
        path: '/',
        secure: true,
        sameSite: 'Strict',
        domain: process.env.NEXT_PUBLIC_FRONTEND_DOMAIN
      });

      window.location.href = '/main';
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="login-container">
      <form className="login-form" onSubmit={handleSubmit}>
        <h1 className="login-title">Login</h1>

        <label htmlFor="username">Username</label>
        <input
          type="text"
          name="username"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <label htmlFor="password">Password</label>
        <input
          type="password"
          name="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />

        <button type="submit">Login</button>
        {error && <p className="login-error">{error}</p>}

        <p className="signup-redirect">
          Don't have an account? <Link href="/signup">Sign up</Link>
        </p>
      </form>
    </div>
  );
}
