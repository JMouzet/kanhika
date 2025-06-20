import Image from 'next/image';
import styles from './page.module.css';
import Cookies from 'js-cookie';

export default function Home() {
  const token = Cookies.get('token');
  if (token) {
    return null;
  }

  return (
    <div className={styles.page}>
      Welcome to Kanhika!
      <div className={styles.ctas}>
        <a className={styles.primary} href="/signup">
          Sign Up
        </a>
        <a className={styles.secondary} href="/login">
          Login
        </a>
      </div>
    </div>
  );
}
