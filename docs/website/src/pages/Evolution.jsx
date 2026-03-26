import { motion } from 'framer-motion'
import SectionWrapper from '../components/SectionWrapper'
import TimelineCard from '../components/TimelineCard'

const BASE = import.meta.env.BASE_URL

const timeline = [
    {
        version: 'v0.4.2-alpha',
        date: 'March 21, 2026',
        title: 'Controller Support & Word Prediction',
        milestones: ['Gaming Controllers', 'Word Prediction Engine', 'Accelerating Backspace', 'Collapsible Settings'],
        features: [
            'Full DualShock 4, 8BitDo, and Bluetooth game controller support on Android & iOS',
            'Cross-platform word suggestion engine with up to 3 real-time suggestions',
            'Autocorrect and next-word prediction - all offline, no data sent anywhere',
            'Hold-to-delete accelerating backspace matching native keyboard behavior',
            'Accordion-style collapsible settings menu on both platforms',
            'Translated all code comments to English across 6 source files',
        ],
        media: [
            { src: `${BASE}documentation/demo%20files/v0.4.2-alpha_Android_normal.jpg`, alt: 'Android keyboard' },
            { src: `${BASE}documentation/demo%20files/v0.4.2-alpha_Android_dark_mode_whatsapp.jpg`, alt: 'Dark mode on WhatsApp' },
            { src: `${BASE}documentation/demo%20files/v0.4.2-alpha_Android_colorblind_mode_instagram.jpg`, alt: 'Colorblind mode' },
        ],
        isLatest: true,
    },
    {
        version: 'v0.3.2-alpha',
        date: 'March 14, 2026',
        title: 'Radial Dial UI & iOS Launch',
        milestones: ['Radial Dial UI', 'Live Preview', 'Efficiency Layout', 'iOS Full Support'],
        features: [
            'Completely overhauled joystick interface with radial dials and 8-segment blocks',
            'Live character preview bar showing color-coded characters as you swipe',
            'New frequency-optimized Efficiency layout alongside the Logical layout',
            'iOS radial keyboard UI redesigned to match Android experience',
            'Full iOS onboarding flow with step-by-step color-synced setup',
            'Shared App Group preferences keep iOS host app and keyboard extension in sync',
        ],
        media: [
            { src: `${BASE}documentation/demo%20files/v0.3.2-alpha_Android_pastel.jpg`, alt: 'Android pastel theme' },
            { src: `${BASE}documentation/demo%20files/v0.3.2-alpha_Android_pastel_2.jpg`, alt: 'Android pastel theme 2' },
            { src: `${BASE}documentation/demo%20files/v0.3.2-alpha_iOS.png`, alt: 'iOS keyboard' },
        ],
    },
    {
        version: 'v0.2.1-alpha',
        date: 'March 7, 2026',
        title: 'Kotlin Multiplatform & Redesign',
        milestones: ['KMP Architecture', 'Material 3 Design', 'Onboarding Guide', 'Professional Branding'],
        features: [
            'Cross-platform keyboard logic with Kotlin Multiplatform ready for iOS',
            'Step-by-step onboarding guide with real-time status indicators',
            'Material 3 settings redesign with 4 organized sections',
            'Custom ERICKeyboard logo and consistent branding throughout',
            'Larger joysticks, cleaner layout, and optimized touch targets',
        ],
        media: [
            { src: `${BASE}documentation/demo%20files/v0.2.1a_typing_demo.gif`, alt: 'Typing demo' },
            { src: `${BASE}documentation/demo%20files/v0.2.1a_user_onboarding.gif`, alt: 'User onboarding' },
        ],
    },
    {
        version: 'v0.1.7-alpha',
        date: 'July 8, 2022',
        title: 'The Beginning - Android Prototype',
        milestones: ['First Release', 'Android 12', 'Controller Detection', 'Chord Input'],
        features: [
            'Initial Android keyboard with dual-joystick chord input system',
            'Support for Android 12 with controller detection',
            'Touch-based swipe typing and physical controller typing',
            'Hands-free typing capability demonstrated',
            'Foundation for the entire ERICKeyboard ecosystem',
        ],
        media: [
            { src: `${BASE}documentation/demo%20files/v0.1.7_swipe_input.gif`, alt: 'Swipe input' },
            { src: `${BASE}documentation/demo%20files/v0.1.7_controller.gif`, alt: 'Controller input' },
            { src: `${BASE}documentation/demo%20files/v0.1.7_no_hands%20type.gif`, alt: 'Hands-free typing' },
        ],
    },
]

const planned = [
    {
        version: 'v0.5.0',
        title: 'Planned Next',
        items: ['Multi-language support with extended dictionaries', 'Mini typing game for chord learning and speed practice'],
    },
    {
        version: 'v1.0.0',
        title: 'Production Release',
        items: ['Production-ready stability', 'Cloud settings sync', 'Typing speed analytics', 'App Store and Play Store releases'],
    },
]

export default function Evolution() {
    return (
        <>
            {/* Hero */}
            <section className="pt-28 pb-16 relative overflow-hidden">
                <div className="absolute inset-0 pointer-events-none">
                    <motion.div
                        className="absolute w-96 h-96 rounded-full bg-periwinkle/20 blur-3xl"
                        style={{ top: '0', right: '10%' }}
                        animate={{ y: [0, -20, 0] }}
                        transition={{ duration: 8, repeat: Infinity }}
                    />
                </div>
                <div className="max-w-4xl mx-auto px-6 text-center relative z-10">
                    <motion.span
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        className="inline-block text-sm font-semibold uppercase tracking-wider text-periwinkle mb-3"
                    >
                        Evolution
                    </motion.span>
                    <motion.h1
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.1 }}
                        className="text-4xl md:text-5xl font-bold text-text mb-6"
                    >
                        From prototype to platform
                    </motion.h1>
                    <motion.p
                        initial={{ opacity: 0, y: 20 }}
                        animate={{ opacity: 1, y: 0 }}
                        transition={{ delay: 0.2 }}
                        className="text-lg text-text-muted max-w-2xl mx-auto"
                    >
                        ERICKeyboard started in 2017 as an idea and became an Android prototype in 2022. Today it's a cross-platform keyboard
                        with word prediction, controller support, and deep accessibility features. Here's how we got here.
                    </motion.p>
                </div>
            </section>

            {/* Stats - at the top */}
            <SectionWrapper>
                <div className="grid grid-cols-2 md:grid-cols-4 gap-6">
                    {[
                        { value: '4+', label: 'Alpha Releases' },
                        { value: '2', label: 'Platforms' },
                        { value: '6', label: 'Color Palettes' },
                        { value: '0', label: 'Data Collected' },
                    ].map((stat, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, scale: 0.9 }}
                            whileInView={{ opacity: 1, scale: 1 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.1 }}
                            className="bg-mint/30 rounded-3xl p-6 text-center"
                        >
                            <div className="text-3xl md:text-4xl font-bold text-text mb-1">{stat.value}</div>
                            <div className="text-sm text-text-muted">{stat.label}</div>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>

            {/* Timeline */}
            <SectionWrapper>
                <div className="max-w-3xl mx-auto">
                    {timeline.map((item, i) => (
                        <TimelineCard key={item.version} {...item} index={i} />
                    ))}
                </div>
            </SectionWrapper>

            {/* What's Coming */}
            <SectionWrapper className="bg-lavender/10">
                <div className="text-center mb-12">
                    <span className="inline-block text-sm font-semibold uppercase tracking-wider text-sunflower mb-3">Roadmap</span>
                    <h2 className="text-3xl md:text-4xl font-bold text-text mb-4">
                        What's coming next
                    </h2>
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 gap-6 max-w-3xl mx-auto">
                    {planned.map((p, i) => (
                        <motion.div
                            key={i}
                            initial={{ opacity: 0, y: 20 }}
                            whileInView={{ opacity: 1, y: 0 }}
                            viewport={{ once: true }}
                            transition={{ delay: i * 0.15 }}
                            className="bg-white/40 border border-lavender/30 rounded-3xl p-8 border-dashed"
                        >
                            <span className="text-xs font-bold uppercase tracking-wider text-text-muted bg-sunflower/20 px-3 py-1 rounded-full">
                                {p.version}
                            </span>
                            <h3 className="text-lg font-bold text-text mt-4 mb-3">{p.title}</h3>
                            <ul className="space-y-2">
                                {p.items.map((item, j) => (
                                    <li key={j} className="text-sm text-text-muted flex items-start gap-2">
                                        <span className="text-sunflower mt-0.5">&#9675;</span>
                                        <span>{item}</span>
                                    </li>
                                ))}
                            </ul>
                        </motion.div>
                    ))}
                </div>
            </SectionWrapper>
        </>
    )
}
