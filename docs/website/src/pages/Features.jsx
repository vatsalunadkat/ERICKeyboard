import { motion } from 'framer-motion'
import SectionWrapper from '../components/SectionWrapper'
import FeatureCard from '../components/FeatureCard'

const accessibilityDetails = [
    {
        title: 'Colorblind Palettes',
        icon: '🎨',
        description: 'Six carefully designed color palettes for different types of color vision deficiency. Each palette assigns 8 distinct colors to the joystick\'s 8 directional segments, ensuring high contrast and distinguishability.',
        palettes: ['Default', 'Okabe-Ito', 'Deuteranopia', 'Protanopia', 'Tritanopia', 'Pastel'],
    },
    {
        title: 'Dyslexia-Friendly Fonts',
        icon: '📝',
        description: 'Toggle OpenDyslexic and other accessible typefaces that use weighted letter bottoms and unique shapes to reduce visual confusion common with standard fonts. Applied across all keyboard text.',
    },
    {
        title: 'Left-Handed Mode',
        icon: '🤚',
        description: 'Mirror the entire keyboard interface so left-handed users get the same ergonomic experience. The primary selection dial moves to the dominant hand side. One toggle, instant switch.',
    },
    {
        title: 'Motor Accessibility',
        icon: '🎯',
        description: 'Large touch targets with generous spacing. The chord system means no precision tapping - just general directional swipes. Adjustable dead zones let users fine-tune sensitivity, and full physical controller support means users who cannot interact with touchscreens can still type comfortably.',
    },
    {
        title: 'Customizable Layouts',
        icon: '⚙️',
        description: 'Create and save custom chord layouts tailored to your needs. Whether you prefer alphabetical order for easy memorization, frequency-optimized placement for speed, or a completely personal arrangement, ERICKeyboard adapts to you.',
    },
    {
        title: 'Chorded Efficiency',
        icon: '⚡',
        description: 'Every character requires exactly two movements - one on each dial. No reaching, no stretching, and no hunting for keys. This consistent effort reduces repetitive strain and makes typing sustainable over longer sessions.',
    },
    {
        title: 'Word Prediction & Autocorrect',
        icon: '💬',
        description: 'Always-on word suggestions reduce the number of chords needed per sentence. Completions, spelling corrections, and next-word predictions - tap a suggestion to accept it instantly. Fewer movements for every message, especially valuable for users with motor impairments or fatigue. All predictions run fully offline.',
    },
]

export default function Features() {
    return (
        <>
            {/* Hero */}
            <section className="pt-28 pb-16 relative overflow-hidden">
                <div className="absolute inset-0 pointer-events-none">
                    <motion.div
                        className="absolute w-80 h-80 rounded-full bg-mint/20 blur-3xl"
                        style={{ top: '10%', left: '5%' }}
                        animate={{ x: [0, 20, 0] }}
                        transition={{ duration: 8, repeat: Infinity }}
                    />
                    <motion.div
                        className="absolute w-64 h-64 rounded-full bg-peach/20 blur-3xl"
                        style={{ bottom: '0', right: '10%' }}
                        animate={{ y: [0, 15, 0] }}
                        transition={{ duration: 6, repeat: Infinity }}
                    />
                </div>
                <div className="max-w-4xl mx-auto px-6 text-center relative z-10">
                    <motion.span
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="inline-block text-sm font-semibold uppercase tracking-wider text-coral mb-3"
                    >
                        Features
                    </motion.span>
                    <motion.h1
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.1 }}
                        className="text-4xl md:text-5xl font-bold text-text mb-6"
                    >
                        Every feature, in detail
                    </motion.h1>
                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                        className="text-lg text-text-muted max-w-2xl mx-auto"
                    >
                        ERICKeyboard is more than a keyboard - it's a rethinking of how text input should work. Dive deep into each capability.
                    </motion.p>
                </div>
            </section>

            {/* Chord System Deep Dive */}
            <SectionWrapper className="bg-lavender/10">
                <div className="text-center mb-8">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-periwinkle mb-2">The Chord System</span>
                    <h2 className="text-2xl md:text-3xl font-bold text-text mb-3">
                        Two dials, infinite possibilities
                    </h2>
                    <p className="text-text-muted max-w-2xl mx-auto text-sm">
                        Instead of hunting for individual keys, ERICKeyboard combines two directional inputs into a single character.
                        The left dial selects a group, the right dial picks the character. Every letter requires equal effort.
                    </p>
                </div>

                {/* Placeholder for chord system demo GIF */}
                <div className="max-w-lg mx-auto mb-8 flex justify-center">
                    <div className="w-full aspect-video rounded-3xl bg-lavender/20 border-2 border-dashed border-lavender/40 flex items-center justify-center">
                        <p className="text-text-muted text-sm">Chord system demo GIF placeholder</p>
                    </div>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-3 gap-5 max-w-4xl mx-auto">
                    {[
                        { icon: '⚡', title: 'Equal Effort', desc: 'Every character requires exactly two directional swipes. No key is harder to reach than any other.' },
                        { icon: '🧠', title: 'Muscle Memory', desc: 'Consistent motor patterns build fast. Users report significant speed gains after just a few days of practice.' },
                        { icon: '🔄', title: 'Multiple Layouts', desc: 'Logical (A-Z order), Efficiency (frequency-optimized), or create your own custom character mapping.' },
                    ].map((item, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.1 }}
                            className="bg-white/40 rounded-3xl p-6 text-center"
                        >
                            <span className="text-3xl block mb-3">{item.icon}</span>
                            <h3 className="font-semibold text-text mb-2">{item.title}</h3>
                            <p className="text-sm text-text-muted">{item.desc}</p>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* Prediction Engine */}
            <SectionWrapper>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
                    <div>
                        <span className="inline-block text-sm font-semibold uppercase tracking-wider text-sage mb-3">Prediction Engine</span>
                        <h2 className="text-3xl md:text-4xl font-bold text-text mb-6">
                            Smart predictions, zero cloud
                        </h2>
                        <p className="text-text-muted mb-6 leading-relaxed">
                            ERICKeyboard's word prediction engine runs entirely on your device. It combines a Trie-based dictionary
                            with bigram frequency data to offer real-time suggestions and next-word predictions.
                        </p>
                        <div className="space-y-4">
                            {[
                                { label: 'Trie-Based Dictionary', desc: 'Fast prefix matching for instant character-by-character suggestions' },
                                { label: 'Bigram Predictions', desc: 'Context-aware next-word suggestions based on word pair frequency' },
                                { label: 'Smart Autocorrect', desc: 'Automatic error correction with one-tap acceptance' },
                                { label: 'Fully Offline', desc: 'No network requests, ever. Your typing data stays on your device.' },
                            ].map((item, i) => (
                                <motion.div
                                    key={i}
                                    initial={{ opacity: 0, x: -20 }}
                                    whileInView={{ opacity: 1, x: 0 }}
                                    viewport={{ once: true }}
                                    transition={{ delay: i * 0.1 }}
                                    className="flex items-start gap-3"
                                >
                                    <span className="w-6 h-6 rounded-lg bg-mint/50 flex items-center justify-center flex-shrink-0 mt-0.5 text-xs font-bold text-text">&#10003;</span>
                                    <div>
                                        <p className="text-sm font-semibold text-text">{item.label}</p>
                                        <p className="text-sm text-text-muted">{item.desc}</p>
                                    </div>
                                </motion.div>
                            ))}
                        </div>
                    </div>
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                        className="bg-gradient-to-br from-mint/30 to-sage/30 rounded-3xl p-8"
                    >
                        {/* Mock prediction UI */}
                        <div className="bg-white/60 rounded-2xl p-6 mb-4">
                            <p className="text-sm text-text-muted mb-3">You typed:</p>
                            <p className="text-lg font-mono text-text">hel<span className="animate-pulse">|</span></p>
                        </div>
                        <div className="flex gap-2">
                            {['hello', 'help', 'helmet'].map((word, i) => (
                                <motion.div
                                    key={i}
                                    animate={{ y: [0, -3, 0] }}
                                    transition={{ duration: 1.5, repeat: Infinity, delay: i * 0.2 }}
                                    className="flex-1 bg-white/50 rounded-xl py-2 text-center text-sm font-medium text-text shadow-sm"
                                >
                                    {word}
                                </motion.div>
                            ))}
                        </div>
                    </motion.div>
                </div>
            </SectionWrapper>

            {/* Accessibility Deep Dive */}
            <SectionWrapper className="bg-peach/10">
                <div className="text-center mb-10">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-blush mb-2">Accessibility</span>
                    <h2 className="text-2xl md:text-3xl font-bold text-text mb-3">
                        Accessibility is not a feature - it's the foundation
                    </h2>
                    <p className="text-text-muted max-w-2xl mx-auto text-sm">
                        Every design decision in ERICKeyboard considers users with different vision, motor, and cognitive needs.
                    </p>
                </div>

                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mb-10">
                    {accessibilityDetails.map((item, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.1 }}
                            className="bg-white/40 rounded-3xl p-8 shadow-sm"
                        >
                            <span className="text-3xl block mb-4">{item.icon}</span>
                            <h3 className="text-lg font-bold text-text mb-2">{item.title}</h3>
                            <p className="text-sm text-text-muted leading-relaxed mb-4">{item.description}</p>
                            {item.palettes && (
                                <div className="flex flex-wrap gap-2">
                                    {item.palettes.map((p, j) => (
                                        <span key={j} className="text-xs bg-peach/30 text-text-muted px-3 py-1 rounded-full">
                                            {p}
                                        </span>
                                    ))}
                                </div>
                            )}
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* Custom Layouts */}
            <SectionWrapper>
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
                    <motion.div
                        initial={{ opacity: 0, scale: 0.95 }}
                        whileInView={{ opacity: 1, scale: 1 }}
                        viewport={{ once: true }}
                        className="bg-gradient-to-br from-sky/30 to-periwinkle/30 rounded-3xl p-8"
                    >
                        <div className="space-y-3">
                            {['Layout: Logical (A-Z)', 'Layout: Efficiency (Frequency)', 'Layout: Custom ✨'].map((name, i) => (
                                <motion.div
                                    key={i}
                                    whileHover={{ x: 4 }}
                                    className={`bg-white/50 rounded-2xl p-4 flex items-center gap-3 cursor-default ${i === 2 ? 'ring-2 ring-periwinkle/50' : ''}`}
                                >
                                    <div className={`w-4 h-4 rounded-full ${i === 2 ? 'bg-periwinkle' : 'bg-lavender/50'}`} />
                                    <span className="text-sm font-medium text-text">{name}</span>
                                </motion.div>
                            ))}
                        </div>
                    </motion.div>
                    <div>
                        <span className="inline-block text-sm font-semibold uppercase tracking-wider text-periwinkle mb-3">Custom Layouts</span>
                        <h2 className="text-3xl md:text-4xl font-bold text-text mb-6">
                            Your keyboard, your rules
                        </h2>
                        <p className="text-text-muted mb-6 leading-relaxed">
                            Choose from pre-built layouts or create your own custom character mapping. Assign any character to any chord combination.
                            Layouts are validated to ensure every character has a unique position.
                        </p>
                        <div className="space-y-3">
                            <div className="flex items-center gap-3">
                                <span className="text-sage">&#10003;</span>
                                <span className="text-sm text-text-muted"><strong className="text-text">Logical layout:</strong> A-Z alphabetical order for easy learning</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <span className="text-sage">&#10003;</span>
                                <span className="text-sm text-text-muted"><strong className="text-text">Efficiency layout:</strong> Common letters in easy-to-reach positions</span>
                            </div>
                            <div className="flex items-center gap-3">
                                <span className="text-sage">&#10003;</span>
                                <span className="text-sm text-text-muted"><strong className="text-text">Custom layout:</strong> Full control over every character position</span>
                            </div>
                        </div>
                    </div>
                </div>
            </SectionWrapper>

            {/* All Features Grid */}
            <SectionWrapper className="bg-mint/10">
                <div className="text-center mb-16">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-coral mb-3">Everything</span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        The complete feature set
                    </h2>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {[
                        { icon: '🎯', title: 'Dual-Joystick Input', description: 'Two radial dials combine to form character chords with equal effort for every key.', color: 'lavender' },
                        { icon: '🎨', title: 'Multiple Layouts', description: 'Logical (A-Z), Efficiency (frequency-optimized), or create your own custom layout.', color: 'mint' },
                        { icon: '☀️', title: 'Pastel Light Theme', description: 'A soft, pastel light theme with accessibility color palettes for every need.', color: 'peach' },
                        { icon: '🎮', title: 'Controller Support', description: 'DualShock, Xbox, 8BitDo - use analog sticks as joystick dials.', color: 'sky' },
                        { icon: '💬', title: 'Word Prediction', description: 'Offline Trie + bigram engine with 3 real-time suggestions.', color: 'periwinkle' },
                        { icon: '♿', title: 'Accessibility First', description: '6 colorblind palettes, left-handed mode, dyslexia fonts, large targets.', color: 'blush' },
                        { icon: '🔒', title: 'Privacy Focused', description: 'Zero data collection. No internet. Fully offline. Source available.', color: 'lavender' },
                        { icon: '🎯', title: 'Typing Practice', description: 'Built-in typing game with curated quotes for learning chords.', color: 'mint' },
                        { icon: '✏️', title: 'Custom Layouts', description: 'Design your own keyboard layout with full validation.', color: 'peach' },
                        { icon: '⌫', title: 'Accelerating Backspace', description: 'Hold to delete words - matches native keyboard behavior.', color: 'sky' },
                        { icon: '📱', title: 'Cross-Platform', description: 'Android and iOS with shared Kotlin Multiplatform logic.', color: 'periwinkle' },
                        { icon: '🔧', title: 'Source Available', description: 'Inspect, audit, and verify every line of code on GitHub.', color: 'blush' },
                    ].map((f, i) => (
                        <FeatureCard key={i} {...f} index={i} />
                    ))}
                </div>
            </SectionWrapper>
        </>
    )
}
