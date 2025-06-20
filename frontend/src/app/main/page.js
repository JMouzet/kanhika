import Link from 'next/link';
import './page.css';

export default function HomePage() {
  return (
    <div className="home-container">
      <div className="button-grid">
        <Link href="/kanji" className="home-button">
          <span>Start learning</span>
        </Link>
        <Link href="/quiz" className="home-button">
          <span>Test your skills</span>
        </Link>
      </div>
    </div>
  );
}
