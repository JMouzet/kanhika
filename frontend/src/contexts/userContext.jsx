'use client';
import { createContext, useContext, useEffect, useState } from 'react';
import Cookies from 'js-cookie';

const UserContext = createContext();

export function UserProvider({ children }) {
  const [username, setUsername] = useState(null);
  const [bio, setBio] = useState(null);
  const [email, setEmail] = useState(null);
  const [exp, setExp] = useState(null);
  const [flame, setFlame] = useState(null);
  const [role, setRole] = useState(null);
  const [creationDate, setCreationDate] = useState(null);

  const token = Cookies.get('token');

  const refreshUser = async () => {
    try {
      await fetchUser();
    } catch {
      setUser(null);
    }
  };

  const fetchUser = async () => {
    if (!token) return;
    const res = await fetch('http://localhost:8080/api/users/me', {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const data = await res.json();
    setUsername(data.username);
    setBio(data.bio);
    setEmail(data.email);
    setExp(data.exp);
    setFlame(data.flame);
    setRole(data.role);
    setCreationDate(data.creationDate);
  }

  useEffect(() => {
    fetchUser();
  }, []);

  return (
    <UserContext.Provider value={{ username, refreshUser }}>
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  return useContext(UserContext);
}
