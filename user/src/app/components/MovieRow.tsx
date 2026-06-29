import { useRef, useState, useEffect } from 'react';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { motion } from 'motion/react';
import MovieCard from './MovieCard';
import { Movie } from '../data/mockData';

interface MovieRowProps {
  title: string;
  movies: Movie[];
  titleColor?: string;
  showViewAll?: boolean;
}

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

export default function MovieRow({ title, movies, titleColor = 'text-white', showViewAll = false }: MovieRowProps) {
  const scrollRef = useRef<HTMLDivElement>(null);
  const [canScrollLeft, setCanScrollLeft] = useState(false);
  const [canScrollRight, setCanScrollRight] = useState(true);

  const checkScroll = () => {
    const el = scrollRef.current;
    if (!el) return;
    setCanScrollLeft(el.scrollLeft > 0);
    setCanScrollRight(el.scrollLeft < el.scrollWidth - el.clientWidth - 10);
  };

  useEffect(() => {
    checkScroll();
    const el = scrollRef.current;
    if (el) {
      el.addEventListener('scroll', checkScroll);
      return () => el.removeEventListener('scroll', checkScroll);
    }
  }, [movies]);

  const scroll = (direction: 'left' | 'right') => {
    const el = scrollRef.current;
    if (!el) return;
    const amount = el.clientWidth * 0.8;
    el.scrollBy({ left: direction === 'left' ? -amount : amount, behavior: 'smooth' });
  };

  return (
    <section className="container mx-auto px-4 py-8">
      <div className="flex items-center mb-4">
        <h2 className={`text-2xl font-bold ${titleColor}`}>{title}</h2>
        {showViewAll && <span className="ml-3 text-sm text-gray-400 cursor-pointer hover:text-white">Xem toàn bộ</span>}
      </div>
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
          ref={scrollRef}
          variants={containerVariants}
          initial="hidden"
          whileInView="show"
          viewport={{ once: true, margin: "-80px" }}
          className="flex gap-4 overflow-x-auto scrollbar-hide py-3"
          style={{ scrollbarWidth: 'none', msOverflowStyle: 'none' }}
        >
          {movies.map((movie) => (
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
    </section>
  );
}