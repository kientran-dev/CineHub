import { useState } from 'react';
import { Loader2, Lock, User } from 'lucide-react';
import api from '../services/api';

export default function Login() {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const handleLogin = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      const response = await api.post('/auth/login', {
        username,
        password,
      });

      const { accessToken, refreshToken, user } = response.data;
      
      // Admin privilege check
      if (!user.roles || !user.roles.includes('ROLE_ADMIN')) {
        setError('Tài khoản của bạn không có đặc quyền Quản Trị Viên (Admin).');
        setLoading(false);
        return;
      }
      
      localStorage.setItem('accessToken', accessToken);
      localStorage.setItem('refreshToken', refreshToken);
      localStorage.setItem('user', JSON.stringify(user));

      window.location.href = '/';
      
    } catch (err: any) {
      if (err.response && err.response.status === 401) {
        setError('Tài khoản hoặc mật khẩu không chính xác.');
      } else if (err.response && err.response.status === 403) {
        setError('Tài khoản không có quyền truy cập.');
      } else {
        setError('Đã xảy ra lỗi hệ thống, vui lòng thử lại sau.');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
      <div className="max-w-md w-full bg-white p-6 sm:p-8 rounded-xl shadow-lg border border-gray-100">
        <div className="text-center mb-8">
          <div className="flex justify-center mb-4">
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" className="h-16 w-16 filter drop-shadow-[0_4px_12px_rgba(229,9,20,0.15)]">
              <defs>
                <linearGradient id="redGlowLogin" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#ff2a3b" />
                  <stop offset="50%" stopColor="#e50914" />
                  <stop offset="100%" stopColor="#9b000a" />
                </linearGradient>
                <linearGradient id="goldGlowLogin" x1="0%" y1="0%" x2="100%" y2="100%">
                  <stop offset="0%" stopColor="#ffe259" />
                  <stop offset="100%" stopColor="#ffa751" />
                </linearGradient>
                <filter id="glowLogin" x="-20%" y="-20%" width="140%" height="140%">
                  <feGaussianBlur stdDeviation="8" result="blur" />
                  <feMerge>
                    <feMergeNode in="blur" />
                    <feMergeNode in="SourceGraphic" />
                  </feMerge>
                </filter>
              </defs>
              <g>
                <path d="M 370,140 C 320,80 200,80 140,140 C 70,210 70,302 140,372 C 200,432 320,432 370,372 L 320,322 C 290,352 220,352 182,322 C 144,284 144,228 182,190 C 220,160 290,160 320,190 Z" fill="url(#redGlowLogin)" />
                <path d="M 225,186 C 225,178 234,173 241,177 L 345,247 C 351,251 351,261 345,265 L 241,335 C 234,339 225,334 225,326 Z" fill="url(#goldGlowLogin)" filter="url(#glowLogin)" />
              </g>
            </svg>
          </div>
          <h2 className="text-2xl font-black text-gray-900 tracking-wider">
            Cine<span className="text-red-600">Hub</span> <span className="text-gray-400 font-medium text-lg ml-1">Admin</span>
          </h2>
          <p className="text-gray-500 mt-2">Vui lòng đăng nhập để tiếp tục</p>
        </div>

        {error && (
          <div className="mb-6 p-4 bg-red-50 border-l-4 border-red-500 text-red-700 text-sm rounded">
            {error}
          </div>
        )}

        <form onSubmit={handleLogin} className="space-y-6">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Tên đăng nhập</label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="text"
                required
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                placeholder="Nhập username..."
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">Mật khẩu</label>
            <div className="relative">
              <Lock className="absolute left-3 top-1/2 -translate-y-1/2 text-gray-400 w-5 h-5" />
              <input
                type="password"
                required
                className="w-full pl-10 pr-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500 transition-colors"
                placeholder="••••••••"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={loading}
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full bg-blue-600 text-white rounded-lg py-3 font-semibold hover:bg-blue-700 focus:ring-4 focus:ring-blue-200 transition-all disabled:opacity-70 flex justify-center items-center gap-2"
          >
            {loading ? (
              <Loader2 className="w-5 h-5 animate-spin" />
            ) : (
              'Đăng nhập'
            )}
          </button>
        </form>
      </div>
    </div>
  );
}
