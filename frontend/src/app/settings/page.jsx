'use client';

import { useEffect, useState } from 'react';
import Cookies from 'js-cookie';
import styles from './page.module.css';
import { useUser } from "@/contexts/userContext";

export default function EditAccountPage() {
  const [userData, setUserData] = useState({ username: '', email: '' });
  const [oldUserData, setOldUserData] = useState({ username: '', email: '' });
  const [passwordData, setPasswordData] = useState({ current: '', new: '' });
  const [loading, setLoading] = useState(true);
  const [successMessage, setSuccessMessage] = useState('');
  const [errorMessage, setErrorMessage] = useState('');
  const { refreshUser } = useUser();

  useEffect(() => {
    const fetchUserData = async () => {
      try {
        const token = Cookies.get('token');
        const res = await fetch('http://localhost:8080/api/users/me', {
          headers: { Authorization: `Bearer ${token}` }
        });
        const data = await res.json();
        setUserData({ username: data.username, email: data.email });
        setOldUserData({ username: data.username, email: data.email });
      } catch {
        setSuccessMessage('Failed to load user info');
      } finally {
        setLoading(false);
      }
    };

    fetchUserData();
  }, []);

  const handleUsernameSubmit = async (e) => {
    e.preventDefault();

    if (userData.username === oldUserData.username) {
      return;
    }
    if (userData.username.match(/^\s*$/)) {
      return;
    }

    if (userData.username.length > 32) {
      setSuccessMessage('');
      setErrorMessage('Username must be less than 32 characters');
      return;
    }

    try {
      const token = Cookies.get('token');
      const res = await fetch('http://localhost:8080/api/users/me/username', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(userData)
      });

      if (!res.ok) {
        const data = await res.text();
        setSuccessMessage('');
        setErrorMessage(data || 'Failed to update information');
        return;
      }
      await refreshUser();
      oldUserData.username = userData.username;
      setErrorMessage('');
      setSuccessMessage('Information updated successfully');
    } catch {
      setSuccessMessage('');
      setErrorMessage('Failed to update information');
    }
  };

  const handleEmailSubmit = async (e) => {
    e.preventDefault();

    if (userData.email === oldUserData.email) {
      return;
    }
    if (userData.email.match(/^\s*$/)) {
      return;
    }

    if (userData.email.length > 255) {
      setSuccessMessage('');
      setErrorMessage('Email must be less than 255 characters');
      return;
    }
    
    try {
      const token = Cookies.get('token');
      const res = await fetch('http://localhost:8080/api/users/me/email', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify(userData)
      });

      if (!res.ok) {
        const data = await res.text();
        setSuccessMessage('');
        setErrorMessage(data || 'Failed to update information');
        return;
      }
      oldUserData.email = userData.email;
      setErrorMessage('');
      setSuccessMessage('Information updated successfully');
    } catch {
      setErrorMessage('Failed to update information');
    }
  };

  const handlePasswordSubmit = async (e) => {
    e.preventDefault();

    if (passwordData.current.match(/^\s*$/) || passwordData.new.match(/^\s*$/)) {
      return;
    }

    if (passwordData.new.length < 8 || passwordData.new.length > 128) {
      setSuccessMessage('');
      setErrorMessage('Password must be between 8 and 128 characters');
      return;
    }

    try {
      const token = Cookies.get('token');
      const res = await fetch('http://localhost:8080/api/users/me/password', {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({
          oldPassword: passwordData.current,
          newPassword: passwordData.new
        })
      });

      if (!res.ok) {
        const data = await res.text();
        setSuccessMessage('');
        setErrorMessage(data || 'Failed to update password');
        return;
      }
      setErrorMessage('');
      setSuccessMessage('Password updated successfully');
      setPasswordData({ current: '', new: '' });
    } catch {
      setErrorMessage('Failed to update password');
    }
  };

  if (loading) return <div className={styles.container}><div className={styles.message}>Loading...</div></div>;

  return (
    <div className={styles.container}>
      <h1 className={styles.title}>Edit Account</h1>
      {successMessage && <p className={styles.success}>{successMessage}</p>}
      {errorMessage && <p className={styles.error}>{errorMessage}</p>}

      <form onSubmit={handleUsernameSubmit} className={styles.form}>
        <label className={styles.label}>
          Username:
          <input
            className={styles.input}
            type="text"
            value={userData.username}
            onChange={e => setUserData({ ...userData, username: e.target.value })}
            required
          />
        </label>
        <button className={styles.button} type="submit">Change Username</button>
      </form>
      <form onSubmit={handleEmailSubmit} className={styles.form}>
        <label className={styles.label}>
          Email:
          <input
            className={styles.input}
            type="email"
            value={userData.email}
            onChange={e => setUserData({ ...userData, email: e.target.value })}
            required
          />
        </label>
        <button className={styles.button} type="submit">Change Email</button>
      </form>

      <hr className={styles.separator} />

      <form onSubmit={handlePasswordSubmit} className={styles.form}>
        <label className={styles.label}>
          Current Password:
          <input
            className={styles.input}
            type="password"
            value={passwordData.current}
            onChange={e => setPasswordData({ ...passwordData, current: e.target.value })}
            required
          />
        </label>
        <label className={styles.label}>
          New Password:
          <input
            className={styles.input}
            type="password"
            value={passwordData.new}
            onChange={e => setPasswordData({ ...passwordData, new: e.target.value })}
            required
          />
        </label>
        <button className={styles.button} type="submit">Change Password</button>
      </form>
    </div>
  );
}
