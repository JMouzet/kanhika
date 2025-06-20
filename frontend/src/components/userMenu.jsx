import React, { useState, useEffect, useRef } from 'react';
import { useRouter } from 'next/navigation';
import './userMenu.css';
import { useUser } from '@/contexts/userContext';

export default function UserMenu() {
  const [open, setOpen] = useState(false);
  const menuRef = useRef(null);
  const router = useRouter();

  const { username } = useUser();

  const toggleMenu = () => setOpen((prev) => !prev);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (menuRef.current && !menuRef.current.contains(event.target)) {
        setOpen(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const handleLogout = () => {
    document.cookie = 'token=; Max-Age=0; path=/';
    router.push('/login');
  };

  const goToProfile = () => {
    router.push(`/users/${username}`);
    setOpen(false);
  };

  const goToSettings = () => {
    router.push(`/settings`);
    setOpen(false);
  };

  const avatarUrl = username
    ? `https://api.dicebear.com/8.x/identicon/svg?seed=${username}`
    : '/spinner.svg';

  return (
    <div className="user-menu-wrapper" ref={menuRef}>
      <button className="user-icon" onClick={toggleMenu}>
        <img
          src={avatarUrl}
          className="user-avatar"
        />
      </button>
      {open && (
        <div className="user-menu">
          <div className="user-details">
            <b>Logged as</b>
            <div className="user-username">@{username}</div>
          </div>
          <hr />
          <button className="user-button" onClick={goToProfile}>
            View Profile
          </button>
          <button className="settings-button" onClick={goToSettings}>
            Settings
          </button>
          <button className="logout-button" onClick={handleLogout}>
            Logout
          </button>
        </div>
      )}
    </div>
  );
}
