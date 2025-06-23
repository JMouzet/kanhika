import { NextResponse } from 'next/server';

const PUBLIC_ROUTES = ['/', '/login', '/signup'];
const ALWAYS_ALLOW = ['/_next', '/favicon.ico', '/robots.txt', '/api'];

export async function middleware(request) {
  const { pathname } = request.nextUrl;
  const token = request.cookies.get('token')?.value;
  const API_URL = process.env.NEXT_PUBLIC_API_URL

  if (ALWAYS_ALLOW.some((prefix) => pathname.startsWith(prefix))) {
    return NextResponse.next();
  }

  if (PUBLIC_ROUTES.includes(pathname)) {
    if (token) {
      return NextResponse.redirect(new URL('/main', request.url));
    }
    return NextResponse.next();
  }

  if (!token) {
    return NextResponse.redirect(new URL('/login', request.url));
  }

  try {
    const verifyRes = await fetch(`${API_URL}/api/users/me`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    if (!verifyRes.ok) {
      const response = NextResponse.redirect(new URL('/login', request.url));

      response.cookies.set({
        name: 'token',
        value: '',
        path: '/',
        expires: new Date(0),
      });

      return response;
    }

    return NextResponse.next();
  } catch (error) {
    console.error('Auth verification failed:', error);
    const response = NextResponse.redirect(new URL('/login', request.url));

    response.cookies.set({
      name: 'token',
      value: '',
      path: '/',
      expires: new Date(0),
    });

    return response;
  }
}

export const config = {
  matcher: [
    /**
     * Match all request paths except for the ones starting with:
     * - api (API routes) * - _next/static (static files)
     * - _next/image (image optimization files)
     *  - favicon.ico (favicon file)
     */
    '/((?!api|_next/static|_next/image|favicon.ico).*)',
  ],
};
