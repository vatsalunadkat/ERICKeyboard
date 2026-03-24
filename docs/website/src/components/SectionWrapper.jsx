import { motion } from 'framer-motion'

export default function SectionWrapper({ children, className = '', id }) {
    return (
        <motion.section
            id={id}
            initial={{ opacity: 0, y: 40 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: '-80px' }}
            transition={{ duration: 0.6, ease: 'easeOut' }}
            className={`py-20 md:py-28 ${className}`}
        >
            <div className="max-w-6xl mx-auto px-6">
                {children}
            </div>
        </motion.section>
    )
}
