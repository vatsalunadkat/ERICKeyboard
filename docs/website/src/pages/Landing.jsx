import { motion } from 'framer-motion'
import { Link } from 'react-router-dom'
import SectionWrapper from '../components/SectionWrapper'
import FeatureCard from '../components/FeatureCard'

const features = [
    { icon: '🎯', title: 'Dual-Joystick Input', description: 'Two radial dials combine to form character chords. Every letter requires equal effort - no more tiny keys.', color: 'lavender' },
    { icon: '💬', title: 'Smart Prediction', description: 'Offline word suggestions, autocorrect, and next-word prediction - powered by Trie + bigram engine. No data leaves your device.', color: 'mint' },
    { icon: '♿', title: 'Accessibility First', description: 'Colorblind palettes, dyslexia-friendly fonts, left-handed mode, and large touch targets for motor accessibility.', color: 'peach' },
    { icon: '🎮', title: 'Controller Support', description: 'Plug in a DualShock, Xbox, or 8BitDo controller and type using analog sticks - great for consoles and TVs.', color: 'sky' },
    { icon: '🔒', title: 'Privacy by Design', description: 'Zero data collection, no internet permissions, fully offline. Source available so you can verify every line.', color: 'periwinkle' },
    { icon: '✏️', title: 'Custom Layouts', description: 'Design your own keyboard layout that fits the way you think and type. Choose Logical, Efficiency, or build your own.', color: 'blush' },
]

const accessibilityFeatures = [
    { icon: '🎨', label: '6 Colorblind Palettes', description: 'Protanopia, Deuteranopia, Tritanopia, and more' },
    { icon: '📝', label: 'Dyslexia-Friendly Fonts', description: 'OpenDyslexic and other accessible typefaces' },
    { icon: '🤚', label: 'Left-Handed Mode', description: 'Mirror the entire interface instantly' },
    { icon: '🎯', label: 'Large Touch Targets', description: 'Designed for motor accessibility needs' },
]

const personas = [
    { name: 'Motor Disability', role: 'User with limited finger dexterity', desc: 'A person with limited hand mobility could use ERICKeyboard with a gaming controller\'s analog sticks, avoiding the need to precisely tap dozens of small on-screen keys. The two large joystick targets require only broad directional movements.', icon: '🕹️' },
    { name: 'Color Vision Deficiency', role: 'User with deuteranopia (green-blind)', desc: 'A person with deuteranopia could switch to the dedicated green-blind palette, where all 8 joystick segments use colors specifically chosen to remain distinguishable without red-green discrimination.', icon: '🎨' },
    { name: 'Dyslexia', role: 'User who struggles with standard fonts', desc: 'A person with dyslexia could enable the OpenDyslexic font option so that all keyboard labels use weighted, asymmetric letterforms that reduce letter-swapping confusion during typing.', icon: '📖' },
    { name: 'Left-Handed User', role: 'Left-hand dominant typist', desc: 'A left-handed person could enable left-handed mode to mirror the dial layout, placing the primary character-group selector under their dominant thumb for faster, more natural input.', icon: '✋' },
    { name: 'Console / TV User', role: 'Typing on a TV or gaming console', desc: 'A person using a smart TV or gaming console could connect a standard gamepad and type with ERICKeyboard\'s dual-stick chord system - far faster and more comfortable than navigating an on-screen grid keyboard with a D-pad.', icon: '🎮' },
    { name: 'Repetitive Strain Injury', role: 'User seeking ergonomic alternatives', desc: 'A person experiencing RSI symptoms could benefit from ERICKeyboard\'s equal-effort character input, where every letter requires the same two broad movements - eliminating the uneven finger stretching that aggravates strain injuries on traditional keyboards.', icon: '⚡' },
    { name: 'Autism Spectrum', role: 'User who prefers logical, predictable layouts', desc: 'A person on the autism spectrum could use ERICKeyboard\'s logical alphabetical layout (A-Z ordered), where character positions follow a predictable pattern rather than the seemingly arbitrary QWERTY arrangement, reducing cognitive load.', icon: '🧩' },
    { name: 'Privacy-Conscious User', role: 'User who avoids data-collecting keyboards', desc: 'A privacy-focused user could switch to ERICKeyboard knowing that no keystrokes, passwords, or personal data are ever logged, transmitted, or shared - the app runs fully offline with zero network permissions and is source available for verification.', icon: '🔒' },
]

export default function Landing() {
    return (
        <>
            {/* Hero Section */}
            <section className="relative min-h-screen flex items-center justify-center overflow-hidden pt-16">
                {/* Background blobs */}
                <div className="absolute inset-0 overflow-hidden pointer-events-none">
                    <motion.div
                        className="absolute w-96 h-96 rounded-full bg-lavender/30 blur-3xl"
                        style={{ top: '10%', left: '10%' }}
                        animate={{ x: [0, 30, 0], y: [0, -20, 0] }}
                        transition={{ duration: 8, repeat: Infinity }}
                    />
                    <motion.div
                        className="absolute w-80 h-80 rounded-full bg-mint/30 blur-3xl"
                        style={{ bottom: '20%', right: '10%' }}
                        animate={{ x: [0, -30, 0], y: [0, 20, 0] }}
                        transition={{ duration: 10, repeat: Infinity }}
                    />
                    <motion.div
                        className="absolute w-64 h-64 rounded-full bg-peach/20 blur-3xl"
                        style={{ top: '50%', left: '50%', transform: 'translate(-50%, -50%)' }}
                        animate={{ scale: [1, 1.2, 1] }}
                        transition={{ duration: 6, repeat: Infinity }}
                    />
                </div>

                <div className="relative z-10 max-w-4xl mx-auto px-6 text-center">
                    <motion.div
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8, delay: 0.1 }}
                    >
                        <div className="inline-flex items-center gap-2 bg-lavender/40 rounded-full px-5 py-2 mb-8">
                            <span className="w-2 h-2 rounded-full bg-sage animate-pulse" />
                            <span className="text-sm font-medium text-text-muted">Now in Alpha - Android & iOS</span>
                        </div>
                    </motion.div>

                    <motion.h1
                        initial={{ opacity: 0, y: 30 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8, delay: 0.2 }}
                        className="text-5xl md:text-7xl font-bold text-text leading-tight mb-6"
                    >
                        A new way to{' '}
                        <span className="bg-gradient-to-r from-periwinkle to-coral bg-clip-text text-transparent">
                            type
                        </span>
                    </motion.h1>

                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8, delay: 0.35 }}
                        className="text-lg md:text-xl text-text-muted max-w-2xl mx-auto mb-10 leading-relaxed"
                    >
                        ERICKeyboard replaces dozens of tiny keys with two intuitive joystick dials.
                        Swipe to combine directions into character chords - fast, accessible, and designed for everyone.
                    </motion.p>

                    <motion.div
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ duration: 0.8, delay: 0.5 }}
                        className="flex flex-wrap justify-center gap-4"
                    >
                        <a
                            href="#"
                            className="inline-flex items-center gap-2 bg-text text-cream px-7 py-3.5 rounded-2xl font-semibold text-sm shadow-lg hover:shadow-xl hover:-translate-y-0.5 transition-all duration-200"
                        >
                            ▶ Google Play - Coming April 2026
                        </a>
                        <a
                            href="#"
                            className="inline-flex items-center gap-2 bg-white/60 text-text px-7 py-3.5 rounded-2xl font-semibold text-sm shadow-md hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 border border-lavender/30"
                        >
                            App Store - Coming May 2026
                        </a>
                    </motion.div>
                </div>
            </section>

            {/* How It Works */}
            <SectionWrapper className="bg-lavender/10">
                <div className="text-center mb-16">
                    <motion.span
                        initial={{ opacity: 0 }}
                        whileInView={{ opacity: 1 }}
                        viewport={{ once: true }}
                        className="inline-block text-sm font-semibold uppercase tracking-wider text-periwinkle mb-3"
                    >
                        How It Works
                    </motion.span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        Three movements. One character.
                    </h2>
                    <p className="text-text-muted max-w-xl mx-auto">
                        Combine two directional swipes to produce any character. It's simpler than it sounds.
                    </p>
                </div>

                {/* Placeholder for demo GIF */}
                <div className="mb-16 flex justify-center">
                    <div className="w-full max-w-lg aspect-video rounded-3xl bg-lavender/20 border-2 border-dashed border-lavender/40 flex items-center justify-center">
                        <p className="text-text-muted text-sm">Demo GIF placeholder</p>
                    </div>
                </div>

                {/* Steps */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
                    {[
                        { num: '1', title: 'Left Dial', desc: 'Swipe the left joystick to select a character group (e.g. A-E).', color: 'bg-lavender/50' },
                        { num: '2', title: 'Right Dial', desc: 'Swipe the right joystick to pick the specific character within the group.', color: 'bg-mint/50' },
                        { num: '3', title: 'Output', desc: 'Both directions combine into a chord - the character appears instantly with real-time word predictions.', color: 'bg-peach/50' },
                    ].map((step, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 30 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.15 }}
                            className={`${step.color} rounded-3xl p-8 text-center`}
                        >
                            <div className="w-12 h-12 rounded-2xl bg-white/60 flex items-center justify-center mx-auto mb-4 text-xl font-bold text-text">
                                {step.num}
                            </div>
                            <h3 className="text-lg font-semibold text-text mb-2">{step.title}</h3>
                            <p className="text-sm text-text-muted">{step.desc}</p>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* Demo GIFs */}
            <SectionWrapper>
                <div className="text-center mb-12">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-sage mb-3">See It In Action</span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        Touch, controller, or hands-free
                    </h2>
                    <p className="text-text-muted max-w-xl mx-auto">
                        ERICKeyboard adapts to however you interact with your device.
                    </p>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {[
                        { src: `${import.meta.env.BASE_URL}documentation/demo%20files/v0.1.7_swipe_input.gif`, label: 'Touch-based swipe input', color: 'bg-lavender/30' },
                        { src: `${import.meta.env.BASE_URL}documentation/demo%20files/v0.1.7_controller.gif`, label: 'Physical controller input', color: 'bg-mint/30' },
                        { src: `${import.meta.env.BASE_URL}documentation/demo%20files/v0.1.7_no_hands_type.gif`, label: 'Hands-free typing', color: 'bg-peach/30' },
                    ].map((item, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, scale: 0.95 }}
                            whileInView={{ opacity: 1, scale: 1 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.1 }}
                            whileHover={{ y: -4 }}
                            className={`${item.color} rounded-3xl p-6 flex flex-col items-center shadow-sm hover:shadow-md transition-shadow`}
                        >
                            <div className="w-full aspect-[9/16] max-w-[200px] rounded-2xl overflow-hidden bg-white/40 mb-4">
                                <img src={item.src} alt={item.label} loading="lazy" className="w-full h-full object-cover" />
                            </div>
                            <p className="text-sm font-medium text-text">{item.label}</p>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* Features */}
            <SectionWrapper className="bg-mint/10">
                <div className="text-center mb-16">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-coral mb-3">Features</span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        Built for speed, accessibility, and privacy
                    </h2>
                    <p className="text-text-muted max-w-xl mx-auto">
                        Every feature is crafted with care - from the chord engine to the prediction system.
                    </p>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {features.map((f, i) => (
                        <FeatureCard key={i} {...f} index={i} />
                    ))}
                </div>
                <div className="text-center mt-12">
                    <Link
                        to="/features"
                        className="inline-flex items-center gap-2 bg-white/60 text-text px-7 py-3.5 rounded-2xl font-semibold text-sm shadow-md hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 border border-lavender/30"
                    >
                        Explore all features →
                    </Link>
                </div>
            </SectionWrapper>

            {/* Who ERICKeyboard is built for */}
            <SectionWrapper>
                <div className="text-center mb-12">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-blush mb-3">Accessibility</span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        Who ERICKeyboard is built for
                    </h2>
                    <p className="text-text-muted max-w-2xl mx-auto">
                        ERICKeyboard is designed for a wide range of users. Below are illustrative scenarios showing how different people could benefit from the keyboard.
                    </p>
                </div>
                <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
                    {personas.map((p, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.06 }}
                            whileHover={{ y: -4 }}
                            className="bg-peach/20 rounded-3xl p-6 shadow-sm hover:shadow-md transition-shadow"
                        >
                            <span className="text-3xl block mb-3">{p.icon}</span>
                            <h4 className="font-semibold text-text mb-1">{p.name}</h4>
                            <p className="text-xs text-text-muted/70 mb-2">{p.role}</p>
                            <p className="text-xs text-text-muted leading-relaxed">{p.desc}</p>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* Accessibility Features */}
            <SectionWrapper className="bg-peach/10">
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-16 items-center">
                    <div>
                        <span className="inline-block text-sm font-semibold uppercase tracking-wider text-blush mb-3">Built-in Accessibility</span>
                        <h2 className="text-3xl md:text-4xl font-bold text-text mb-6">
                            Designed for everyone, not just some
                        </h2>
                        <p className="text-text-muted mb-8 leading-relaxed">
                            ERICKeyboard was built accessibility-first. Not as an afterthought - as a foundation. Every design decision considers users with different vision, motor, and cognitive needs.
                        </p>
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                            {accessibilityFeatures.map((item, i) => (
                                <motion.div
                                    key={i}
                                    initial={{ opacity: 0, x: -20 }}
                                    whileInView={{ opacity: 1, x: 0 }}
                                    viewport={{ once: true }}
                                    transition={{ delay: i * 0.1 }}
                                    className="bg-peach/30 rounded-2xl p-4"
                                >
                                    <div className="text-xl mb-2">{item.icon}</div>
                                    <p className="text-sm font-semibold text-text">{item.label}</p>
                                    <p className="text-xs text-text-muted mt-1">{item.description}</p>
                                </motion.div>
                            ))}
                        </div>
                    </div>
                    <div className="flex justify-center">
                        <motion.div
                            initial={{ opacity: 0, scale: 0.9 }}
                            whileInView={{ opacity: 1, scale: 1 }}
                            viewport={{ once: true }}
                            className="bg-gradient-to-br from-peach/40 to-blush/40 rounded-3xl p-10 max-w-sm w-full"
                        >
                            <div className="space-y-4">
                                {[
                                    { name: 'Default', colors: ['#E60012', '#F39800', '#FFF100', '#009944', '#0068B7', '#1D2088', '#920783', '#000000'] },
                                    { name: 'Okabe-Ito', colors: ['#E69F00', '#56B4E9', '#009E73', '#F0E442', '#0072B2', '#D55E00', '#CC79A7', '#000000'] },
                                    { name: 'Deuteranopia', colors: ['#0072B2', '#E69F00', '#56B4E9', '#F0E442', '#CC3311', '#009988', '#EE7733', '#000000'] },
                                    { name: 'Protanopia', colors: ['#0077BB', '#33BBEE', '#009988', '#EE7733', '#CC3311', '#EE3377', '#BBBBBB', '#000000'] },
                                    { name: 'Tritanopia', colors: ['#CC3311', '#0077BB', '#EECC66', '#33BBEE', '#EE3377', '#009988', '#BBBBBB', '#000000'] },
                                    { name: 'Pastel', colors: ['#F4A6B0', '#F6C9A0', '#FDE9A0', '#A8DFC0', '#A0C4E8', '#C4A8D8', '#D8A8C8', '#8B8B8B'] },
                                ].map((palette, i) => (
                                    <motion.div
                                        key={i}
                                        initial={{ opacity: 0 }}
                                        whileInView={{ opacity: 1 }}
                                        viewport={{ once: true }}
                                        transition={{ delay: 0.3 + i * 0.08 }}
                                        className="flex items-center gap-3"
                                    >
                                        <div className="flex gap-0.5">
                                            {palette.colors.map((c, j) => (
                                                <div key={j} className="w-4 h-4 rounded-sm" style={{ backgroundColor: c }} />
                                            ))}
                                        </div>
                                        <span className="text-sm text-text font-medium">{palette.name}</span>
                                    </motion.div>
                                ))}
                            </div>
                        </motion.div>
                    </div>
                </div>
            </SectionWrapper>

            {/* Controller Support */}
            <SectionWrapper className="bg-sky/10">
                <div className="text-center mb-12">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-sunflower mb-3">Controller Support</span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        Type with your game controller
                    </h2>
                    <p className="text-text-muted max-w-2xl mx-auto">
                        Connect a DualShock 4, Xbox, or 8BitDo controller via Bluetooth. Use the analog sticks as joystick dials - perfect for TVs, consoles, and hands-free typing.
                    </p>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
                    {[
                        { name: 'DualShock 4', emoji: '🎮', desc: 'Full analog stick support with haptic feedback' },
                        { name: 'Xbox Controller', emoji: '🕹️', desc: 'Bluetooth-connected Xbox controllers work seamlessly' },
                        { name: '8BitDo & Others', emoji: '🎲', desc: 'Any HID-compatible Bluetooth gamepad' },
                    ].map((c, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.1 }}
                            whileHover={{ y: -4 }}
                            className="bg-sky/30 rounded-3xl p-8 text-center shadow-sm hover:shadow-md transition-all"
                        >
                            <span className="text-4xl block mb-4">{c.emoji}</span>
                            <h3 className="font-semibold text-text mb-2">{c.name}</h3>
                            <p className="text-sm text-text-muted">{c.desc}</p>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* CTA / Source Available */}
            <SectionWrapper>
                <motion.div
                    initial={{ opacity: 0, scale: 0.95 }}
                    whileInView={{ opacity: 1, scale: 1 }}
                    viewport={{ once: true }}
                    className="bg-gradient-to-br from-lavender/50 to-periwinkle/30 rounded-3xl p-10 md:p-16 text-center"
                >
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        Built in the open
                    </h2>
                    <p className="text-text-muted max-w-xl mx-auto mb-8 leading-relaxed">
                        ERICKeyboard is built with Kotlin Multiplatform. Inspect the code, suggest features, or contribute directly.
                        Free for personal, educational, and accessibility use.
                    </p>
                    <div className="flex flex-wrap justify-center gap-4">
                        <a
                            href="https://github.com/vatsalunadkat/ERICKeyboard"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="inline-flex items-center gap-2 bg-text text-cream px-7 py-3.5 rounded-2xl font-semibold text-sm shadow-lg hover:shadow-xl hover:-translate-y-0.5 transition-all duration-200"
                        >
                            View on GitHub ↗
                        </a>
                        <Link
                            to="/evolution"
                            className="inline-flex items-center gap-2 bg-white/60 text-text px-7 py-3.5 rounded-2xl font-semibold text-sm shadow-md hover:shadow-lg hover:-translate-y-0.5 transition-all duration-200 border border-lavender/30"
                        >
                            See the journey →
                        </Link>
                    </div>
                </motion.div>
            </SectionWrapper>
        </>
    )
}
