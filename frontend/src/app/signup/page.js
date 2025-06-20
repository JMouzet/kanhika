'use client';
import { useState, useEffect } from 'react';
import Link from 'next/link';
import './page.css';
import Cookies from 'js-cookie';

export default function SignUpPage() {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    const input = document.querySelector('input[name="username"]');
    if (input && input.value) {
      setUsername(input.value);
    }
  }, []);
  useEffect(() => {
    const input = document.querySelector('input[name="email"]');
    if (input && input.value) {
      setEmail(input.value);
    }
  }, []);
  useEffect(() => {
    const input = document.querySelector('input[name="password"]');
    if (input && input.value) {
      setPassword(input.value);
    }
  }, []);

  const token = Cookies.get('token');
  if (token) {
    return null;
  }

  const handleSubmit = async (e) => {
    e.preventDefault();

    setError(null);

    if (username.match(/^\s*$/)) {
      setError('Username is empty');
      return;
    }
    if (email.match(/^\s*$/)) {
      setError('Email is empty');
      return;
    }
    if (password.match(/^\s*$/)) {
      setError('Password is empty');
      return;
    }
    if (username.length > 32) {
      setError('Username must be less than 32 characters');
      return;
    }
    if (email.length > 255) {
      setError('Email must be less than 255 characters');
      return;
    }
    if (password.length < 8 || password.length > 128) {
      setError('Password must be between 8 and 128 characters');
      return;
    }

    try {
      const res = await fetch('http://localhost:8080/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({ username, email, password }),
      });

      if (!res.ok) {
        const data = await res.text();
        throw new Error(data || 'Register failed');
      }

      const data = await res.json();

      Cookies.set('token', data.token, {
        path: '/',
        secure: false,
        sameSite: 'Strict',
      });

      window.location.href = '/main';
    } catch (err) {
      setError(err.message);
    }
  };

  return (
    <div className="signup-container">
      <form className="signup-form" onSubmit={handleSubmit}>
        <h1 className="signup-title">Sign up</h1>

        <label htmlFor="username">Username</label>
        <input
          type="text"
          name="username"
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          required
        />

        <label htmlFor="email">Email address</label>
        <input
          type="text"
          name="email"
          placeholder="Email address"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
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

        <button type="submit">Sign up</button>
        {error && <p className="signup-error">{error}</p>}

        <p className="login-redirect">
          Already have an account? <Link href="/login">Log in</Link>
        </p>
      </form>
    </div>
  );
}
