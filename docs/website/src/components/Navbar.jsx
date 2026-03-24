import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { motion, AnimatePresence } from 'framer-motion'

const navLinks = [
    { to: '/', label: 'Home' },
    { to: '/features', label: 'Features' },
    { to: '/evolution', label: 'Evolution' },
    { to: '/privacy', label: 'Privacy' },
]

export default function Navbar() {
    const [mobileOpen, setMobileOpen] = useState(false)
    const { pathname } = useLocation()

    return (
        <nav className="fixed top-0 left-0 right-0 z-50 glass bg-cream/80 border-b border-lavender/30">
            <div className="max-w-6xl mx-auto px-6 h-16 flex items-center justify-between">
                {/* Logo */}
                <Link to="/" className="flex items-center gap-2 group" onClick={() => setMobileOpen(false)}>
                    <img src={`${import.meta.env.BASE_URL}images/erick-logo.png`} alt="ERICKeyboard logo" className="w-9 h-9 rounded-xl shadow-md group-hover:shadow-lg transition-shadow" />
                    <span className="font-bold text-lg text-text tracking-tight">ERICKeyboard</span>
                </Link>

                {/* Desktop nav */}
                <div className="hidden md:flex items-center gap-1">
                    {navLinks.map(({ to, label }) => (
                        <Link
                            key={to}
                            to={to}
                            className={`px-4 py-2 rounded-xl text-sm font-medium transition-all duration-200 ${pathname === to
                                ? 'bg-lavender/60 text-text'
                                : 'text-text-muted hover:bg-lavender/30 hover:text-text'
                                }`}
                        >
                            {label}
                        </Link>
                    ))}
                    <a
                        href="https://github.com/vatsalunadkat/ERICKeyboard"
                        target="_blank"
                        rel="noopener noreferrer"
                        className="px-4 py-2 rounded-xl text-sm font-medium text-text-muted hover:bg-lavender/30 hover:text-text transition-all duration-200"
                    >
                        GitHub ↗
                    </a>
                </div>

                {/* Mobile toggle */}
                <div className="flex md:hidden items-center gap-2">
                    <button
                        onClick={() => setMobileOpen(!mobileOpen)}
                        className="w-9 h-9 rounded-xl bg-lavender/40 flex items-center justify-center cursor-pointer"
                        aria-label="Toggle navigation"
                    >
                        <div className="flex flex-col gap-1">
                            <motion.span
                                animate={mobileOpen ? { rotate: 45, y: 5 } : { rotate: 0, y: 0 }}
                                className="block w-4 h-0.5 bg-text rounded-full"
                            />
                            <motion.span
                                animate={mobileOpen ? { opacity: 0 } : { opacity: 1 }}
                                className="block w-4 h-0.5 bg-text rounded-full"
                            />
                            <motion.span
                                animate={mobileOpen ? { rotate: -45, y: -5 } : { rotate: 0, y: 0 }}
                                className="block w-4 h-0.5 bg-text rounded-full"
                            />
                        </div>
                    </button>
                </div>
            </div>

            {/* Mobile menu */}
            <AnimatePresence>
                {mobileOpen && (
                    <motion.div
                        initial={{ height: 0, opacity: 0 }}
                        animate={{ height: 'auto', opacity: 1 }}
                        exit={{ height: 0, opacity: 0 }}
                        transition={{ duration: 0.25 }}
                        className="md:hidden overflow-hidden bg-cream/95 glass border-b border-lavender/30"
                    >
                        <div className="px-6 py-4 flex flex-col gap-1">
                            {navLinks.map(({ to, label }) => (
                                <Link
                                    key={to}
                                    to={to}
                                    onClick={() => setMobileOpen(false)}
                                    className={`px-4 py-3 rounded-xl text-sm font-medium transition-all ${pathname === to
                                        ? 'bg-lavender/60 text-text'
                                        : 'text-text-muted hover:bg-lavender/30'
                                        }`}
                                >
                                    {label}
                                </Link>
                            ))}
                            <a
                                href="https://github.com/vatsalunadkat/ERICKeyboard"
                                target="_blank"
                                rel="noopener noreferrer"
                                onClick={() => setMobileOpen(false)}
                                className="px-4 py-3 rounded-xl text-sm font-medium text-text-muted hover:bg-lavender/30 transition-all"
                            >
                                GitHub ↗
                            </a>
                        </div>
                    </motion.div>
                )}
            </AnimatePresence>
        </nav>
    )
}
