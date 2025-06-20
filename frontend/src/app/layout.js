import { Kosugi_Maru } from 'next/font/google';
import './globals.css';
import TopBar from '@/components/topBar';
import { UserProvider } from '@/contexts/userContext';

const kosugiMaru = Kosugi_Maru({
  subsets: ['latin'],
  weight: '400',
  display: 'swap',
  variable: '--font-kosugi-maru',
});

export const metadata = {
  title: 'Kanhika',
  description: 'Learn Japanese the fun way!',
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={`${kosugiMaru.variable}`}>
        <UserProvider>
          <TopBar />
          {children}
        </UserProvider>
      </body>
    </html>
  );
}
