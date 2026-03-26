import { motion } from 'framer-motion'

export default function TimelineCard({ version, date, title, features, milestones, media, index = 0, isLatest = false }) {
    const colors = ['bg-lavender/40', 'bg-mint/40', 'bg-peach/40', 'bg-sky/40']
    const dotColors = ['bg-periwinkle', 'bg-sage', 'bg-coral', 'bg-sunflower']

    return (
        <motion.div
            initial={{ opacity: 0, x: index % 2 === 0 ? -40 : 40 }}
            whileInView={{ opacity: 1, x: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.6, delay: index * 0.1 }}
            className="relative flex gap-6 md:gap-10"
        >
            {/* Timeline line and dot */}
            <div className="flex flex-col items-center">
                <motion.div
                    whileHover={{ scale: 1.3 }}
                    className={`w-5 h-5 rounded-full ${dotColors[index % dotColors.length]} shadow-md flex-shrink-0 z-10 ${isLatest ? 'ring-4 ring-periwinkle/30' : ''}`}
                />
                <div className="w-0.5 flex-1 bg-lavender/40" />
            </div>

            {/* Content card */}
            <div className={`${colors[index % colors.length]} rounded-3xl p-6 md:p-8 mb-8 flex-1 shadow-sm hover:shadow-md transition-shadow`}>
                <div className="flex flex-wrap items-center gap-3 mb-3">
                    <span className="text-xs font-bold uppercase tracking-wider text-text-muted bg-white/60 px-3 py-1 rounded-full">
                        {version}
                    </span>
                    <span className="text-xs text-text-muted">{date}</span>
                    {isLatest && (
                        <span className="text-xs font-bold text-white bg-periwinkle px-3 py-1 rounded-full">Latest</span>
                    )}
                </div>
                <h3 className="text-xl font-bold text-text mb-3">{title}</h3>

                {milestones && milestones.length > 0 && (
                    <div className="flex flex-wrap gap-2 mb-4">
                        {milestones.map((m, i) => (
                            <span key={i} className="text-xs font-medium bg-white/50 text-text-muted px-3 py-1 rounded-full">
                                {m}
                            </span>
                        ))}
                    </div>
                )}

                <ul className="space-y-2">
                    {features.map((f, i) => (
                        <li key={i} className="text-sm text-text-muted flex items-start gap-2">
                            <span className="text-sage mt-0.5 flex-shrink-0">&#10003;</span>
                            <span>{f}</span>
                        </li>
                    ))}
                </ul>

                {/* Demo media */}
                {media && media.length > 0 && (
                    <div className="mt-6 grid grid-cols-2 sm:grid-cols-3 gap-3">
                        {media.map((item, i) => (
                            <div key={i} className="rounded-2xl overflow-hidden bg-white/40 shadow-sm">
                                <img src={item.src} alt={item.alt} loading="lazy" className="w-full h-auto object-contain" />
                                <p className="text-xs text-text-muted text-center py-1.5">{item.alt}</p>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </motion.div>
    )
}
