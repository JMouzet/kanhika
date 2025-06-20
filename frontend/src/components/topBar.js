'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import './topBar.css';
import UserMenu from './userMenu';
import { useEffect } from 'react';

export default function Topbar() {
  const pathname = usePathname();
  const isAuthPage =
    pathname === '/' || pathname === '/login' || pathname === '/signup';

  return (
    <header className="topbar">
      <div className="topbar-logo">
        <Link href="/">Kanhika</Link>
      </div>

      {!isAuthPage && 1 == 0 && (
        <input
          type="text"
          className="topbar-search"
          placeholder="Search kanji or user..."
        />
      )}
      {!isAuthPage && (
        <div className="topbar-icons">
          <UserMenu />
        </div>
      )}
    </header>
  );
}
