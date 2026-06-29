import { Link, useNavigate } from 'react-router';
import { Play, Trash2 } from 'lucide-react';
import { motion } from 'motion/react';
import { CardContent } from './ui/card';
import { Badge } from './ui/badge';
import { Button } from './ui/button';
import { Movie } from '../data/mockData';

interface MovieCardProps {
  movie: Movie;
  watchUrl?: string;
  onDelete?: () => void;
  progress?: number;
  currentEpisode?: number;
}

export default function MovieCard({ movie, watchUrl, onDelete, progress, currentEpisode }: MovieCardProps) {
  const navigate = useNavigate();
  
  const subtitle = movie.subtitleType
    ? movie.subtitleType === 'vietsub'    ? { type: 'Vietsub',      color: 'bg-gray-600/90' }
      : movie.subtitleType === 'longtieng' ? { type: 'Lồng tiếng',   color: 'bg-green-700/90' }
      :                                      { type: 'Thuyết minh',  color: 'bg-blue-700/90' }
    : null;

  const handleWatchClick = (e: React.MouseEvent) => {
    if (watchUrl) {
      e.preventDefault();
      e.stopPropagation();
      navigate(watchUrl);
    }
  };

  const handleDeleteClick = (e: React.MouseEvent) => {
    if (onDelete) {
      e.preventDefault();
      e.stopPropagation();
      onDelete();
    }
  };

  return (
    <motion.div
      whileHover={{
        scale: 1.05,
        y: -4,
        borderColor: "rgba(220, 38, 38, 0.4)",
        boxShadow: "0 20px 25px -5px rgba(220, 38, 38, 0.15), 0 10px 10px -5px rgba(220, 38, 38, 0.08)"
      }}
      transition={{ type: 'spring', stiffness: 400, damping: 22 }}
      className="group relative overflow-hidden rounded-xl border border-gray-800/60 bg-gray-900/50 backdrop-blur-sm flex flex-col h-full"
    >
      <Link to={`/movie/${movie.id}`} className="flex flex-col h-full">
        <div className="relative aspect-[2/3] overflow-hidden flex-shrink-0">
          <img
            src={movie.poster}
            alt={movie.title}
            className="h-full w-full object-cover transition-transform duration-500 group-hover:scale-110"
          />
          
          {/* Subtle vignette always visible */}
          <div className="absolute inset-0 bg-gradient-to-t from-black/40 via-transparent to-transparent" />
          
          {/* Overlay on hover */}
          <div className="absolute inset-0 bg-black/60 opacity-0 transition-opacity duration-300 group-hover:opacity-100 flex items-center justify-center">
            {watchUrl ? (
              <Button 
                onClick={handleWatchClick}
                size="lg" 
                className="gap-2 bg-red-600 hover:bg-red-700 shadow-lg shadow-red-600/30"
              >
                <Play className="h-5 w-5" />
                Xem tiếp
              </Button>
            ) : (
              <Button size="lg" className="gap-2 bg-red-600 hover:bg-red-700 shadow-lg shadow-red-600/30">
                <Play className="h-5 w-5" />
                Xem ngay
              </Button>
            )}
          </div>

          {/* Delete Button */}
          {onDelete && (
            <button
              onClick={handleDeleteClick}
              className="absolute top-2 right-2 p-1.5 rounded-full bg-black/60 opacity-0 group-hover:opacity-100 transition-opacity hover:bg-red-600/80 z-10"
              title="Xóa khỏi lịch sử"
            >
              <Trash2 className="h-3.5 w-3.5 text-white" />
            </button>
          )}

          {/* Subtitle Badge */}
          {subtitle && (
            <div className="absolute top-2 left-2">
              <Badge className={`${subtitle.color} text-white backdrop-blur-sm text-[10px] px-1.5 py-0.5`}>
                {subtitle.type}
              </Badge>
            </div>
          )}

          {/* IMDb Rating Badge on image */}
          {movie.imdbRating && !onDelete && (
            <div className="absolute top-2 right-2 flex items-center gap-1 rounded bg-yellow-500/90 backdrop-blur-sm px-1.5 py-0.5">
              <span className="text-[10px] font-bold text-black">IMDb</span>
              <span className="text-[10px] font-bold text-black">{movie.imdbRating.toFixed(1)}</span>
            </div>
          )}
        </div>

        <CardContent className="p-3 flex flex-col flex-1 justify-between">
          <div className="space-y-1">
            <h3 className="line-clamp-1 font-bold text-sm text-gray-200 group-hover:text-white transition-colors">{movie.title}</h3>
            {movie.englishTitle && (
              <p className="line-clamp-1 text-xs text-gray-500 mt-0.5">{movie.englishTitle}</p>
            )}
            
            {/* Metadata row */}
            <div className="mt-1.5 flex items-center gap-1.5 text-xs text-gray-400 flex-wrap">
              <span>{movie.year}</span>
              <span className="w-1 h-1 rounded-full bg-gray-700" />
              <span>{movie.type === 'movie' ? 'Phim lẻ' : movie.type === 'tv_show' ? 'TV Show' : 'Phim bộ'}</span>
              {movie.duration > 0 && (
                <>
                  <span className="w-1 h-1 rounded-full bg-gray-700" />
                  <span>{movie.duration} phút</span>
                </>
              )}
            </div>
          </div>

          {/* Progress bar and watch history text */}
          {progress !== undefined && (
            <div className="mt-3 space-y-1.5 w-full">
              <div className="relative h-1 w-full overflow-hidden rounded-full bg-gray-800">
                <div 
                  className="h-full bg-red-600 transition-all shadow-sm shadow-red-600/50" 
                  style={{ width: `${progress}%` }} 
                />
              </div>
              <div className="flex items-center justify-between text-[10px] text-gray-500">
                <span>{movie.type === 'movie' ? 'Phim lẻ' : currentEpisode ? `Tập ${currentEpisode}` : 'Phim bộ'}</span>
                <span>Đã xem {progress}%</span>
              </div>
            </div>
          )}
        </CardContent>
      </Link>
    </motion.div>
  );
}
