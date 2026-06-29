import { Link, Outlet, useLocation } from 'react-router';
import { 
  LayoutDashboard, 
  Film, 
  Users, 
  Grid3x3, 
  Crown,
  Menu,
  LogOut,
  Receipt
} from 'lucide-react';
import { useState } from 'react';

interface NavItem {
  path: string;
  label: string;
  icon: React.ReactNode;
}

const navItems: NavItem[] = [
  { path: '/', label: 'Trang chủ', icon: <LayoutDashboard size={20} /> },
  { path: '/movies', label: 'Quản lý phim', icon: <Film size={20} /> },
  { path: '/accounts', label: 'Quản lý tài khoản', icon: <Users size={20} /> },
  { path: '/genres', label: 'Quản lý thể loại', icon: <Grid3x3 size={20} /> },
  { path: '/invoices', label: 'Quản lý hóa đơn', icon: <Receipt size={20} /> },
  { path: '/premium', label: 'Quản lý gói Premium', icon: <Crown size={20} /> },
];

export function AdminLayout() {
  const location = useLocation();
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);

  const handleLogout = () => {
    localStorage.removeItem('accessToken');
    localStorage.removeItem('refreshToken');
    localStorage.removeItem('user');
    window.location.href = '/login';
  };

  return (
    <div className="flex h-screen bg-gray-50">
      {/* Sidebar */}
      <aside
        className={`${
          isSidebarOpen ? 'w-64' : 'w-20'
        } bg-gray-900 text-white transition-all duration-300 flex flex-col`}
      >
        {/* Header */}
        <div className="p-4 flex items-center justify-between border-b border-gray-700">
          {isSidebarOpen && (
            <Link to="/" className="flex items-center gap-2 group transition-transform duration-300 hover:scale-[1.02]">
              <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" className="h-7 w-7 filter drop-shadow-[0_2px_8px_rgba(229,9,20,0.2)]">
                <defs>
                  <linearGradient id="redGlowLayout" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stopColor="#ff2a3b" />
                    <stop offset="50%" stopColor="#e50914" />
                    <stop offset="100%" stopColor="#9b000a" />
                  </linearGradient>
                  <linearGradient id="goldGlowLayout" x1="0%" y1="0%" x2="100%" y2="100%">
                    <stop offset="0%" stopColor="#ffe259" />
                    <stop offset="100%" stopColor="#ffa751" />
                  </linearGradient>
                  <filter id="glowLayout" x="-20%" y="-20%" width="140%" height="140%">
                    <feGaussianBlur stdDeviation="8" result="blur" />
                    <feMerge>
                      <feMergeNode in="blur" />
                      <feMergeNode in="SourceGraphic" />
                    </feMerge>
                  </filter>
                </defs>
                <g>
                  <path d="M 370,140 C 320,80 200,80 140,140 C 70,210 70,302 140,372 C 200,432 320,432 370,372 L 320,322 C 290,352 220,352 182,322 C 144,284 144,228 182,190 C 220,160 290,160 320,190 Z" fill="url(#redGlowLayout)" />
                  <path d="M 225,186 C 225,178 234,173 241,177 L 345,247 C 351,251 351,261 345,265 L 241,335 C 234,339 225,334 225,326 Z" fill="url(#goldGlowLayout)" filter="url(#glowLayout)" />
                </g>
              </svg>
              <span className="font-black text-base tracking-wider text-white">
                Cine<span className="text-red-500">Hub</span><span className="text-gray-400 font-normal text-xs ml-1">Admin</span>
              </span>
            </Link>
          )}
          <button
            onClick={() => setIsSidebarOpen(!isSidebarOpen)}
            className="p-2 hover:bg-gray-800 rounded-lg transition-colors"
          >
            <Menu size={20} />
          </button>
        </div>

        {/* Navigation */}
        <nav className="flex-1 p-4">
          <ul className="space-y-2">
            {navItems.map((item) => {
              const isActive = location.pathname === item.path;
              return (
                <li key={item.path}>
                  <Link
                    to={item.path}
                    className={`flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                      isActive
                        ? 'bg-blue-600 text-white'
                        : 'text-gray-300 hover:bg-gray-800 hover:text-white'
                    }`}
                  >
                    {item.icon}
                    {isSidebarOpen && <span>{item.label}</span>}
                  </Link>
                </li>
              );
            })}
          </ul>
        </nav>

        {/* Footer */}
        <div className="p-4 border-t border-gray-700">
          <button 
            onClick={handleLogout}
            className="flex items-center gap-3 px-4 py-3 w-full text-gray-300 hover:bg-gray-800 hover:text-white rounded-lg transition-colors"
          >
            <LogOut size={20} />
            {isSidebarOpen && <span>Đăng xuất</span>}
          </button>
        </div>
      </aside>

      {/* Main Content */}
      <main className="flex-1 overflow-auto">
        <div className="p-8">
          <Outlet />
        </div>
      </main>
    </div>
  );
}