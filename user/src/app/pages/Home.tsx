import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router';
import { Play, ChevronLeft, ChevronRight, Sparkles, Loader2, Brain } from 'lucide-react';
import { motion, AnimatePresence } from 'motion/react';
import Header from '../components/Header';
import MovieCard from '../components/MovieCard';
import MovieRow from '../components/MovieRow';
import Footer from '../components/Footer';
import { movieService } from '../services/movieService';
import { historyService } from '../services/historyService';
import { useAuth } from '../contexts/AuthContext';
import { toMovieList, toMovie } from '../utils/movieAdapter';
import type { Movie } from '../data/mockData';

const containerVariants = {
  hidden: { opacity: 0 },
  show: {
    opacity: 1,
    transition: {
      staggerChildren: 0.04
    }
  }
} as const;

const itemVariants = {
  hidden: { opacity: 0, y: 20, scale: 0.95 },
  show: {
    opacity: 1,
    y: 0,
    scale: 1,
    transition: { type: 'spring', stiffness: 120, damping: 14 }
  }
} as const;

export default function Home() {
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [featuredIndex, setFeaturedIndex] = useState(0);
  const [direction, setDirection] = useState(1);
  const [movies, setMovies] = useState<Movie[]>([]);
  const [continueWatching, setContinueWatching] = useState<Array<Movie & { progress: number; episodeId?: number; currentEpisode?: number; watchTime?: number }>>([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [recommendations, setRecommendations] = useState<Movie[]>([]);
  const [loading, setLoading] = useState(true);

  // Fetch all movies từ API
  useEffect(() => {
    movieService.getAllMovies()
      .then(data => setMovies(toMovieList(data)))
      .catch(console.error)
      .finally(() => setLoading(false));
  }, []);

  // Fetch watch history nếu đã đăng nhập
  useEffect(() => {
    if (!isAuthenticated) {
      setContinueWatching([]);
      setLoadingHistory(false);
      return;
    }
    setLoadingHistory(true);
    historyService.getHistory().then(history => {
      // Lấy phim tương ứng từ danh sách movies đã fetch
      movieService.getAllMovies().then(allMovies => {
        const uniqueMovies = new Map<number, Movie & { progress: number; watchDate: string; episodeId?: number; currentEpisode?: number; watchTime?: number }>();

        history.forEach(wh => {
          // Khớp theo movieId (vừa bổ sung vào API) hoặc fallback theo Title
          const apiMovie = allMovies.find(m => m.id === wh.movieId || m.title === wh.movieTitle);
          if (!apiMovie) return;

          const progress = wh.watchTime && apiMovie.duration
            ? Math.round((wh.watchTime / (apiMovie.duration! * 60)) * 100)
            : 0;

          const movieWithProgress = {
            ...toMovie(apiMovie),
            progress: Math.min(progress, 100),
            watchDate: wh.watchDate,
            episodeId: wh.episodeId,
            currentEpisode: wh.currentEpisode,
            watchTime: wh.watchTime
          };

          // Nếu phim này đã có trong Map, chỉ giữ lại bản ghi có ngày xem mới nhất
          const existing = uniqueMovies.get(apiMovie.id);
          if (!existing || new Date(wh.watchDate) > new Date(existing.watchDate)) {
            uniqueMovies.set(apiMovie.id, movieWithProgress);
          }
        });

        // Chuyển Map thành mảng và sắp xếp theo ngày xem mới nhất
        const sorted = Array.from(uniqueMovies.values())
          .sort((a, b) => new Date(b.watchDate).getTime() - new Date(a.watchDate).getTime());

        setContinueWatching(sorted);
      }).catch(console.error)
      .finally(() => setLoadingHistory(false));
    }).catch((err) => {
      console.error(err);
      setContinueWatching([]);
      setLoadingHistory(false);
    });
  }, [isAuthenticated]);

  // Fetch recommendations từ Collaborative Filtering API
  useEffect(() => {
    if (!isAuthenticated) {
      setRecommendations([]);
      return;
    }
    movieService.getRecommendations()
      .then(data => setRecommendations(toMovieList(data)))
      .catch(() => setRecommendations([]));
  }, [isAuthenticated]);

  // Phân loại phim (dựa trên country và genres từ API)
  const featuredMovies = [...movies].sort((a, b) => Number(b.id) - Number(a.id)).slice(0, 4); // 4 phim mới nhất làm banner
  const trendingMovies = movies.slice(0, 12);
  const koreanMovies = movies.filter(m => m.country === 'KR' || m.country === 'Hàn Quốc');
  const chineseMovies = movies.filter(m => m.country === 'CN' || m.country === 'Trung Quốc');
  const usukMovies = movies.filter(m => ['US', 'GB', 'Mỹ', 'Anh'].includes(m.country));
  const animationMovies = movies.filter(m =>
    m.categories.some(c => c.includes('animation') || c.includes('hoạt-hình') || c.includes('hoat-hinh'))
  );
  const seriesMovies = movies.filter(m => m.type === 'series');
  const tvShowMovies = movies.filter(m => m.type === 'tv_show');

  const currentFeatured = featuredMovies[featuredIndex];

  useEffect(() => {
    if (featuredMovies.length <= 1) return;
    const timer = setInterval(() => {
      setDirection(1);
      setFeaturedIndex(prev => (prev + 1) % featuredMovies.length);
    }, 5000);
    return () => clearInterval(timer);
  }, [featuredMovies.length]);

  const nextFeatured = () => {
    setDirection(1);
    setFeaturedIndex(prev => (prev + 1) % featuredMovies.length);
  };
  const prevFeatured = () => {
    setDirection(-1);
    setFeaturedIndex(prev => (prev - 1 + featuredMovies.length) % featuredMovies.length);
  };

  const cwRef = useRef<HTMLDivElement>(null);
  const [canScrollCWLeft, setCanScrollCWLeft] = useState(false);
  const [canScrollCWRight, setCanScrollCWRight] = useState(true);

  const checkCWScroll = () => {
    const el = cwRef.current;
    if (!el) return;
    setCanScrollCWLeft(el.scrollLeft > 0);
    setCanScrollCWRight(el.scrollLeft < el.scrollWidth - el.clientWidth - 10);
  };

  useEffect(() => {
    checkCWScroll();
    const el = cwRef.current;
    if (el) {
      el.addEventListener('scroll', checkCWScroll);
      return () => el.removeEventListener('scroll', checkCWScroll);
    }
  }, [continueWatching]);

  const scrollCW = (dir: 'left' | 'right') => {
    const el = cwRef.current;
    if (!el) return;
    const amount = el.clientWidth * 0.8;
    el.scrollBy({ left: dir === 'left' ? -amount : amount, behavior: 'smooth' });
  };

  const slideVariants = {
    enter: (d: number) => ({ x: d > 0 ? '100%' : '-100%', opacity: 0 }),
    center: { x: 0, opacity: 1 },
    exit: (d: number) => ({ x: d > 0 ? '-100%' : '100%', opacity: 0 }),
  };

  // Loading skeleton
  if (loading) {
    return (
      <div className="min-h-screen bg-[#0a0a0a] text-white flex flex-col">
        <Header />
        <div className="flex flex-1 items-center justify-center">
          <div className="flex flex-col items-center gap-4 text-gray-400">
            <Loader2 className="h-12 w-12 animate-spin text-red-600" />
            <p>Đang tải phim...</p>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-[#0a0a0a] text-white">
      <Header />

      {/* Featured Banner */}
      {featuredMovies.length > 0 && (
        <section className="relative h-[80vh] overflow-hidden">
          <AnimatePresence initial={false} custom={direction} mode="popLayout">
            <motion.div
              key={featuredIndex}
              custom={direction}
              variants={slideVariants}
              initial="enter"
              animate="center"
              exit="exit"
              transition={{ duration: 0.6, ease: [0.4, 0, 0.2, 1] }}
              className="absolute inset-0"
            >
              <img
                src={currentFeatured?.backdrop || currentFeatured?.poster}
                alt={currentFeatured?.title}
                className="h-full w-full object-cover object-center"
                onError={(e) => { (e.target as HTMLImageElement).style.display = 'none'; }}
              />
              {/* Gradient overlays - mạnh hơn để text dễ đọc */}
              <div className="absolute inset-0 bg-gradient-to-r from-[#0a0a0a] via-[#0a0a0a]/80 to-[#0a0a0a]/20" />
              <div className="absolute inset-0 bg-gradient-to-t from-[#0a0a0a] via-transparent to-[#0a0a0a]/40" />
            </motion.div>
          </AnimatePresence>

          <div className="container relative z-10 mx-auto flex h-full items-end px-4 pb-20">
            <AnimatePresence mode="wait">
              <motion.div
                key={featuredIndex}
                initial={{ opacity: 0, y: 30 }}
                animate={{ opacity: 1, y: 0 }}
                exit={{ opacity: 0, y: -20 }}
                transition={{ duration: 0.5, delay: 0.1 }}
                className="flex items-start gap-6"
              >
                {/* === POSTER BÊN TRÁI (ngang hàng với tên phim) === */}
                <motion.div
                  initial={{ opacity: 0, scale: 0.9, y: 20 }}
                  animate={{ opacity: 1, scale: 1, y: 0 }}
                  transition={{ duration: 0.5, delay: 0.15, type: 'spring', stiffness: 100, damping: 15 }}
                  className="hidden sm:block flex-shrink-0"
                >
                  <div className="relative w-36 md:w-44 rounded-xl overflow-hidden shadow-2xl ring-2 ring-white/15 group">
                    <img
                      src={currentFeatured?.poster}
                      alt={currentFeatured?.title}
                      className="w-full h-52 md:h-64 object-cover group-hover:scale-105 transition-transform duration-500"
                      onError={(e) => {
                        (e.target as HTMLImageElement).parentElement!.style.display = 'none';
                      }}
                    />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/30 to-transparent" />
                    <div className="absolute inset-0 ring-inset ring-1 ring-white/10 rounded-xl" />
                  </div>
                </motion.div>

                {/* === THÔNG TIN PHIM === */}
                <div className="space-y-3 max-w-xl">
                  <motion.h1
                    initial={{ opacity: 0, y: 20 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.2 }}
                    className="text-4xl md:text-5xl font-bold drop-shadow-lg leading-tight"
                  >
                    {currentFeatured?.title}
                  </motion.h1>
                  {currentFeatured?.englishTitle && (
                    <motion.p
                      initial={{ opacity: 0, y: 15 }}
                      animate={{ opacity: 1, y: 0 }}
                      transition={{ duration: 0.5, delay: 0.25 }}
                      className="text-lg text-gray-300"
                    >
                      {currentFeatured.englishTitle}
                    </motion.p>
                  )}
                  <motion.p
                    initial={{ opacity: 0, y: 15 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.3 }}
                    className="text-sm md:text-base text-gray-300 line-clamp-3"
                  >
                    {currentFeatured?.description}
                  </motion.p>
                  <motion.div
                    initial={{ opacity: 0, y: 15 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ duration: 0.5, delay: 0.35 }}
                    className="flex items-center gap-3 text-sm flex-wrap"
                  >
                    {currentFeatured?.imdbRating && (
                      <span className="flex items-center gap-1 bg-yellow-600 px-3 py-1 rounded font-semibold text-black">
                        IMDb {currentFeatured.imdbRating}
                      </span>
                    )}
                    {currentFeatured?.year > 0 && (
                      <span className="text-gray-300">{currentFeatured.year}</span>
                    )}
                    {currentFeatured?.duration > 0 && (
                      <span className="text-gray-300">{currentFeatured.duration} phút</span>
                    )}
                  </motion.div>
                  <motion.div
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    transition={{ duration: 0.5, delay: 0.4, type: 'spring', stiffness: 200, damping: 12 }}
                    className="pt-1"
                  >
                    <button
                      onClick={() => navigate(`/movie/${currentFeatured?.id}`)}
                      className="flex h-14 w-14 items-center justify-center rounded-full bg-red-600 hover:bg-red-700 transition-all hover:scale-110 shadow-lg shadow-red-600/30"
                    >
                      <Play className="h-6 w-6 fill-white" />
                    </button>
                  </motion.div>
                </div>
              </motion.div>
            </AnimatePresence>

            {/* Mini Thumbnails - góc phải dưới */}
            <div className="absolute bottom-8 right-8 flex gap-2">
              {featuredMovies.slice(0, 4).map((movie, idx) => (
                <button
                  key={movie.id}
                  onClick={() => {
                    setDirection(idx > featuredIndex ? 1 : -1);
                    setFeaturedIndex(idx);
                  }}
                  className={`relative h-20 w-32 overflow-hidden rounded-lg border-2 transition-all ${idx === featuredIndex
                      ? 'border-red-600 scale-110 shadow-lg shadow-red-600/20'
                      : 'border-gray-700/50 opacity-60 hover:opacity-100'
                    }`}
                >
                  <img
                    src={movie.poster || movie.backdrop}
                    alt={movie.title}
                    className="h-full w-full object-cover"
                    onError={(e) => {
                      const img = e.target as HTMLImageElement;
                      img.style.display = 'none';
                      (img.parentElement as HTMLElement).style.background = '#1f2937';
                    }}
                  />
                  <div className="absolute inset-0 bg-gradient-to-t from-black/70 to-transparent" />
                  <p className="absolute bottom-1 left-1 right-1 text-[10px] text-white font-medium line-clamp-2 leading-tight">
                    {movie.title}
                  </p>
                </button>
              ))}
            </div>
          </div>

          {featuredMovies.length > 1 && (
            <>
              <button
                onClick={prevFeatured}
                className="absolute left-4 top-1/2 -translate-y-1/2 z-20 flex h-12 w-12 items-center justify-center rounded-full bg-black/60 text-white border border-white/10 shadow-lg backdrop-blur-md opacity-0 hover:opacity-100 group-hover:opacity-100 scale-90 hover:scale-110 transition-all duration-300 hover:bg-red-600 hover:border-red-500 cursor-pointer"
                style={{ transitionTimingFunction: 'cubic-bezier(0.34, 1.56, 0.64, 1)' }}
              >
                <ChevronLeft className="h-6 w-6" />
              </button>
              <button
                onClick={nextFeatured}
                className="absolute right-4 top-1/2 -translate-y-1/2 z-20 flex h-12 w-12 items-center justify-center rounded-full bg-black/60 text-white border border-white/10 shadow-lg backdrop-blur-md opacity-0 hover:opacity-100 group-hover:opacity-100 scale-90 hover:scale-110 transition-all duration-300 hover:bg-red-600 hover:border-red-500 cursor-pointer"
                style={{ transitionTimingFunction: 'cubic-bezier(0.34, 1.56, 0.64, 1)' }}
              >
                <ChevronRight className="h-6 w-6" />
              </button>
            </>
          )}
        </section>
      )}

      {/* Continue Watching */}
      {isAuthenticated && (loadingHistory || continueWatching.length > 0) && (
        <section className="relative py-8">
          <div className="absolute inset-0 bg-gradient-to-b from-[#0a0a0a] via-[#0f0f0f] to-[#0a0a0a]" />
          <div className="container relative mx-auto px-4">
            <div className="flex items-center gap-3 mb-4">
              <h2 className="text-2xl font-bold">Tiếp tục xem</h2>
              {loadingHistory && (
                <Loader2 className="h-5 w-5 animate-spin text-red-600" />
              )}
            </div>

            {loadingHistory ? (
              // Skeleton Loading placeholder cards
              <div className="flex gap-4 overflow-x-hidden py-3">
                {[1, 2, 3, 4, 5].map((i) => (
                  <div key={i} className="flex-shrink-0 w-[160px] sm:w-[180px] md:w-[200px] space-y-3 animate-pulse">
                    <div className="aspect-[2/3] w-full rounded-xl bg-gray-900/80 border border-gray-800/60" />
                    <div className="space-y-2 px-1">
                      <div className="h-4 w-3/4 rounded bg-gray-800" />
                      <div className="h-3 w-1/2 rounded bg-gray-800" />
                    </div>
                  </div>
                ))}
              </div>
            ) : (
              <div className="relative group/row">
                {/* Left Arrow */}
                {canScrollCWLeft && (
                  <button
                    onClick={() => scrollCW('left')}
                    className="absolute left-4 top-1/2 -translate-y-1/2 z-20 flex h-12 w-12 items-center justify-center rounded-full bg-black/60 text-white border border-white/10 shadow-lg backdrop-blur-md opacity-0 scale-90 group-hover/row:opacity-100 group-hover/row:scale-100 transition-all duration-300 hover:bg-red-600 hover:border-red-500 hover:scale-110 cursor-pointer"
                    style={{ transitionTimingFunction: 'cubic-bezier(0.34, 1.56, 0.64, 1)' }}
                  >
                    <ChevronLeft className="h-6 w-6" />
                  </button>
                )}

                {/* Scrollable Row with Staggered Framer Motion */}
                <motion.div
                  ref={cwRef}
                  variants={containerVariants}
                  initial="hidden"
                  whileInView="show"
                  viewport={{ once: true, margin: "-80px" }}
                  className="flex gap-4 overflow-x-auto py-3 scrollbar-hide"
                  style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
                >
                  {continueWatching.map((movie) => (
                    <motion.div
                      key={movie.id}
                      variants={itemVariants}
                      className="flex-shrink-0 w-[160px] sm:w-[180px] md:w-[200px]"
                    >
                      <MovieCard
                        movie={movie}
                        watchUrl={movie.episodeId ? `/watch/${movie.id}?episode=${movie.episodeId}` : `/watch/${movie.id}`}
                        progress={movie.progress}
                        currentEpisode={movie.currentEpisode}
                      />
                    </motion.div>
                  ))}
                </motion.div>

                {/* Right Arrow */}
                {canScrollCWRight && (
                  <button
                    onClick={() => scrollCW('right')}
                    className="absolute right-4 top-1/2 -translate-y-1/2 z-20 flex h-12 w-12 items-center justify-center rounded-full bg-black/60 text-white border border-white/10 shadow-lg backdrop-blur-md opacity-0 scale-90 group-hover/row:opacity-100 group-hover/row:scale-100 transition-all duration-300 hover:bg-red-600 hover:border-red-500 hover:scale-110 cursor-pointer"
                    style={{ transitionTimingFunction: 'cubic-bezier(0.34, 1.56, 0.64, 1)' }}
                  >
                    <ChevronRight className="h-6 w-6" />
                  </button>
                )}
              </div>
            )}
          </div>
        </section>
      )}

      {/* Gợi ý cho bạn — Collaborative Filtering */}
      {recommendations.length > 0 && (
        <section className="relative py-8 overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-b from-[#0a0a0a] via-[#0d1117] to-[#0a0a0a]" />
          <div className="absolute top-1/2 left-1/4 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[250px] bg-cyan-600/5 rounded-full blur-3xl" />
          <div className="absolute top-1/2 right-1/4 translate-x-1/2 -translate-y-1/2 w-[400px] h-[200px] bg-purple-600/5 rounded-full blur-3xl" />
          <div className="container relative mx-auto px-4">
            <div className="flex items-center mb-5 gap-3">
              <div className="flex items-center justify-center w-9 h-9 rounded-lg bg-gradient-to-br from-cyan-500/20 to-purple-500/20 border border-cyan-500/20">
                <Brain className="h-5 w-5 text-cyan-400" />
              </div>
              <h2 className="text-2xl font-bold bg-gradient-to-r from-cyan-400 to-purple-400 bg-clip-text text-transparent">
                Gợi ý cho bạn
              </h2>
              <span className="text-[10px] font-medium bg-cyan-500/10 text-cyan-400 px-2 py-0.5 rounded-full border border-cyan-500/20 uppercase tracking-wider">
                AI Recommendation
              </span>
            </div>
            <ScrollRow movies={recommendations} />
          </div>
        </section>
      )}

      {/* Tất cả phim */}
      {trendingMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-b from-[#0a0a0a] via-[#0d0d0d] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="Phim Mới Cập Nhật" movies={trendingMovies} />
          </div>
        </div>
      )}

      {/* Phim series */}
      {seriesMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-b from-[#0a0a0a] via-[#0d0d0d] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="Phim Bộ" movies={seriesMovies} />
          </div>
        </div>
      )}

      {/* TV Show */}
      {tvShowMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-teal-950/5 via-[#0a0a0a] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="TV Show" movies={tvShowMovies} titleColor="text-teal-400" showViewAll />
          </div>
        </div>
      )}

      {/* Hàn Quốc */}
      {koreanMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-pink-950/5 via-[#0a0a0a] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="Phim Hàn Quốc" movies={koreanMovies} titleColor="text-pink-500" showViewAll />
          </div>
        </div>
      )}

      {/* Trung Quốc */}
      {chineseMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-orange-950/5 via-[#0a0a0a] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="Phim Trung Quốc" movies={chineseMovies} titleColor="text-orange-500" showViewAll />
          </div>
        </div>
      )}

      {/* Âu Mỹ */}
      {usukMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-blue-950/5 via-[#0a0a0a] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="Phim Âu Mỹ" movies={usukMovies} titleColor="text-blue-500" showViewAll />
          </div>
        </div>
      )}

      {/* Hoạt hình */}
      {animationMovies.length > 0 && (
        <div className="relative">
          <div className="absolute inset-0 bg-gradient-to-r from-purple-950/5 via-[#0a0a0a] to-[#0a0a0a]" />
          <div className="relative">
            <MovieRow title="Kho Phim Hoạt Hình" movies={animationMovies} titleColor="text-purple-500" showViewAll />
          </div>
        </div>
      )}

      {/* Gợi ý (tất cả phim còn lại) */}
      {movies.length > 12 && (
        <section className="relative py-8 overflow-hidden">
          <div className="absolute inset-0 bg-gradient-to-b from-[#0a0a0a] via-[#110a0a] to-[#0a0a0a]" />
          <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[600px] h-[300px] bg-red-600/5 rounded-full blur-3xl" />
          <div className="container relative mx-auto px-4">
            <div className="flex items-center mb-4 gap-2">
              <Sparkles className="h-6 w-6 text-yellow-400" />
              <h2 className="text-2xl font-bold text-yellow-400">Khám phá thêm</h2>
            </div>
            <ScrollRow movies={movies.slice(12)} />
          </div>
        </section>
      )}

      <Footer />
    </div>
  );
}

function ScrollRow({ movies: movieList }: { movies: Movie[] }) {
  const ref = useRef<HTMLDivElement>(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(true);

  const checkScroll = () => {
    const el = ref.current;
    if (!el) return;
    setCanScrollLeft(el.scrollLeft > 0);
    setCanScrollRight(el.scrollLeft < el.scrollWidth - el.clientWidth - 10);
  };

  useEffect(() => {
    checkScroll();
    const el = ref.current;
    if (el) {
      el.addEventListener('scroll', checkScroll);
      return () => el.removeEventListener('scroll', checkScroll);
    }
  }, [movieList]);

  const scroll = (dir: 'left' | 'right') => {
    const el = ref.current;
    if (!el) return;
    const amount = el.clientWidth * 0.8;
    el.scrollBy({ left: dir === 'left' ? -amount : amount, behavior: 'smooth' });
  };

  return (
    <div className="relative group/row">
      {/* Left Arrow */}
      {canScrollLeft && (
        <button
          onClick={() => scroll('left')}
          className="absolute left-4 top-1/2 -translate-y-1/2 z-20 flex h-12 w-12 items-center justify-center rounded-full bg-black/60 text-white border border-white/10 shadow-lg backdrop-blur-md opacity-0 scale-90 group-hover/row:opacity-100 group-hover/row:scale-100 transition-all duration-300 hover:bg-red-600 hover:border-red-500 hover:scale-110 cursor-pointer"
          style={{ transitionTimingFunction: 'cubic-bezier(0.34, 1.56, 0.64, 1)' }}
        >
          <ChevronLeft className="h-6 w-6" />
        </button>
      )}

      {/* Scrollable Row with Staggered Framer Motion */}
      <motion.div
        ref={ref}
        variants={containerVariants}
        initial="hidden"
        whileInView="show"
        viewport={{ once: true, margin: "-80px" }}
        className="flex gap-4 overflow-x-auto py-3 scrollbar-hide"
        style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
      >
        {movieList.map((movie) => (
          <motion.div
            key={movie.id}
            variants={itemVariants}
            className="flex-shrink-0 w-[160px] sm:w-[180px] md:w-[200px]"
          >
            <MovieCard movie={movie} />
          </motion.div>
        ))}
      </motion.div>

      {/* Right Arrow */}
      {canScrollRight && (
        <button
          onClick={() => scroll('right')}
          className="absolute right-4 top-1/2 -translate-y-1/2 z-20 flex h-12 w-12 items-center justify-center rounded-full bg-black/60 text-white border border-white/10 shadow-lg backdrop-blur-md opacity-0 scale-90 group-hover/row:opacity-100 group-hover/row:scale-100 transition-all duration-300 hover:bg-red-600 hover:border-red-500 hover:scale-110 cursor-pointer"
          style={{ transitionTimingFunction: 'cubic-bezier(0.34, 1.56, 0.64, 1)' }}
        >
          <ChevronRight className="h-6 w-6" />
        </button>
      )}
    </div>
  );
}
