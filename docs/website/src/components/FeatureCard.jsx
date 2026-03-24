import { motion } from 'framer-motion'

export default function FeatureCard({ icon, title, description, color = 'lavender', index = 0 }) {
    const colorMap = {
        lavender: 'bg-lavender/40',
        mint: 'bg-mint/40',
        peach: 'bg-peach/40',
        sky: 'bg-sky/40',
        periwinkle: 'bg-periwinkle/40',
        blush: 'bg-blush/30',
    }

    return (
        <motion.div
            initial={{ opacity: 0, y: 30 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.5, delay: index * 0.08 }}
            whileHover={{ y: -6, transition: { duration: 0.2 } }}
            className={`${colorMap[color] || colorMap.lavender} rounded-3xl p-7 shadow-sm hover:shadow-lg transition-shadow duration-300`}
        >
            <div className="text-3xl mb-4">{icon}</div>
            <h3 className="text-lg font-semibold text-text mb-2">{title}</h3>
            <p className="text-sm text-text-muted leading-relaxed">{description}</p>
        </motion.div>
    )
}
