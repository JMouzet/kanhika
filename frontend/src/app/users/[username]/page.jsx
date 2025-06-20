"use client";

import React, { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import styles from "./page.module.css";
import Cookies from "js-cookie";
import { useUser } from "@/contexts/userContext";

export default function UserPage() {
  const { username } = useParams();
  const myUser = useUser();
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isFollowing, setIsFollowing] = useState(false);
  const [isBlocking, setIsBlocking] = useState(false);
  const [isEditing, setIsEditing] = useState(false);
  const [editedBio, setEditedBio] = useState("");
  const token = Cookies.get("token");

  // Fetch user data
  useEffect(() => {
    if (!username) return;
    setLoading(true);
    fetch(`http://localhost:8080/api/users/get/${username}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    }
    )
      .then((res) => res.json())
      .then((data) => {
        setUser(data);
        setLoading(false);
      })
      .catch(() => setLoading(false));
  }, [username, token]);
  
  // Check if the user is following this profile
  useEffect(() => {
    if (!user) return;
    fetch(`http://localhost:8080/api/users/following`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        data.forEach(follow => {
          if (follow.username === user.username) {
            setIsFollowing(true);
          }
        });
      });
  }, [user, token]);

  // Check if the user is blocking this profile
  useEffect(() => {
    if (!user) return;
    fetch(`http://localhost:8080/api/users/block`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => {
        data.forEach(block => {
          if (block.username === user.username) {
            setIsBlocking(true);
          }
        });
      });
  }, [user, token]);

  // Handle follow/unfollow
  const handleFollow = async () => {
    await fetch(`http://localhost:8080/api/users/follow/${user.username}`, {
      method: isFollowing ? "DELETE" : "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error("Failed to follow/unfollow user");
        }
        setIsFollowing(!isFollowing);
      });
  };

  // Handle block/unblock
  const handleBlock = async () => {
    await fetch(`http://localhost:8080/api/users/block/${user.username}`, {
      method: isBlocking ? "DELETE" : "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => {
        if (!res.ok) {
          throw new Error("Failed to block/unblock user");
        }
        setIsBlocking(!isBlocking);
        setIsFollowing(false);
      });
  };

  // Handle edit box
  const handleEdit = (bio) => {
    setIsEditing(true);
    setEditedBio(bio);
  };

  // Handle saving bio
  const saveEdit = async () => {
    try {
      const token = Cookies.get('token');
      const res = await fetch(`http://localhost:8080/api/users/me/bio`, {
        method: 'PATCH',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ bio: editedBio })
      });

      if (!res.ok) throw new Error();
      
      const updated = await res.json();

      setUser((prev) => ({
        ...prev,
        bio: updated.bio
      }));
      setIsEditing(false);
      setEditedBio('');
    } catch {
      alert('Failed to edit comment');
    }
  };

  const avatarUrl = user
  ? `https://api.dicebear.com/8.x/identicon/svg?seed=${user.username}`
  : '/spinner.svg';


  if (loading) return <div className={styles.container}><div className={styles.message}>Loading...</div></div>;
  if (!user) return <div className={styles.container}><div className={styles.message}>User not found</div></div>;

  return (
    <div className={styles.container}>
      <div className={styles.profileCard}>
        <img src={avatarUrl} className={styles.avatar} />
        <h1 className={styles.username}>@{user.username}</h1>
        {user.role === "ADMIN" && (
          <div className={styles.adminBadge}>Admin</div>
        )}
        {isEditing ? (
          <>
            <textarea
              value={editedBio}
              onChange={(e) => setEditedBio(e.target.value)}
              className={styles.editBox}
            />
            <button
              className={styles.saveButton}
              onClick={() => saveEdit()}
            >
              Save
            </button>
            <button
              className={styles.cancelButton}
              onClick={() => setIsEditing(false)}
            >
              Cancel
            </button>
          </>
        ) : (user.bio && (
          <>
            <p className={styles.bio}>{user.bio}</p>
          </>
        ))}
        {user.username === myUser.username && !isEditing && (
          <>
            <div>
              <button
                className={styles.editButton}
                onClick={() => handleEdit(user.bio)}
              >
                Edit bio
              </button>
            </div>
          </>
        )}

        <div className={styles.stats}>
          <div>
            <b>EXP</b>
            <p>{user.exp}</p>
          </div>
          <div>
            <b>Streak</b>
            <p>{user.flame} days</p>
          </div>
        </div>
        {user.username !== myUser.username && (
          <div className={styles.actions}>
            <button
              className={styles.button}
              onClick={handleFollow}
              disabled={isBlocking}
            >
              {isFollowing ? "Unfollow" : "Follow"}
            </button>
            <button className={styles.blockButton} onClick={handleBlock}>
              {isBlocking ? "Unblock" : "Block"}
            </button>
          </div>
        )}

        <p className={styles.dateJoined}>
          Joined on {new Date(user.createdAt).toLocaleDateString()}
        </p>
      </div>
    </div>
  );
}
