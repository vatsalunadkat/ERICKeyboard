import { Link } from 'react-router-dom'

export default function Footer() {
    return (
        <footer className="bg-lavender/20 border-t border-lavender/30">
            <div className="max-w-6xl mx-auto px-6 py-16">
                <div className="grid grid-cols-1 md:grid-cols-4 gap-10">
                    {/* Brand */}
                    <div className="md:col-span-1">
                        <div className="flex items-center gap-2 mb-4">
                            <img src={`${import.meta.env.BASE_URL}images/erick-logo.png`} alt="ERICKeyboard logo" className="w-9 h-9 rounded-xl shadow-md" />
                            <span className="font-bold text-lg text-text">ERICKeyboard</span>
                        </div>
                        <p className="text-sm text-text-muted leading-relaxed">
                            Ergonomic Radial Inclusive Controller Keyboard. A new way to type - designed for everyone.
                        </p>
                    </div>

                    {/* Navigation */}
                    <div>
                        <h4 className="font-semibold text-text mb-4 text-sm uppercase tracking-wider">Navigate</h4>
                        <div className="flex flex-col gap-2">
                            <Link to="/" className="text-sm text-text-muted hover:text-text transition-colors">Home</Link>
                            <Link to="/features" className="text-sm text-text-muted hover:text-text transition-colors">Features</Link>
                            <Link to="/evolution" className="text-sm text-text-muted hover:text-text transition-colors">Evolution</Link>
                            <Link to="/privacy" className="text-sm text-text-muted hover:text-text transition-colors">Privacy</Link>
                            <a href={`${import.meta.env.BASE_URL}docs/v1/index.html`} className="text-sm text-text-muted hover:text-text transition-colors">Old Website (v1)</a>
                        </div>
                    </div>

                    {/* Resources */}
                    <div>
                        <h4 className="font-semibold text-text mb-4 text-sm uppercase tracking-wider">Resources</h4>
                        <div className="flex flex-col gap-2">
                            <a href="https://github.com/vatsalunadkat/ERICKeyboard" target="_blank" rel="noopener noreferrer" className="text-sm text-text-muted hover:text-text transition-colors">GitHub ↗</a>
                            <a href="https://github.com/vatsalunadkat/ERICKeyboard/releases" target="_blank" rel="noopener noreferrer" className="text-sm text-text-muted hover:text-text transition-colors">Releases ↗</a>
                            <a href="https://github.com/vatsalunadkat/ERICKeyboard/blob/main/LICENSE" target="_blank" rel="noopener noreferrer" className="text-sm text-text-muted hover:text-text transition-colors">License ↗</a>
                        </div>
                    </div>

                    {/* Developer */}
                    <div>
                        <h4 className="font-semibold text-text mb-4 text-sm uppercase tracking-wider">Developer</h4>
                        <div className="flex flex-col gap-2">
                            <a href="https://github.com/vatsalunadkat" target="_blank" rel="noopener noreferrer" className="text-sm text-text-muted hover:text-text transition-colors">GitHub Profile ↗</a>
                            <a href="https://www.linkedin.com/in/vatsalunadkat" target="_blank" rel="noopener noreferrer" className="text-sm text-text-muted hover:text-text transition-colors">LinkedIn ↗</a>
                        </div>
                    </div>
                </div>

                <div className="mt-12 pt-8 border-t border-lavender/30 text-center">
                    <p className="text-sm text-text-muted">
                        &copy; 2026 ERICKeyboard - Ergonomic Radial Inclusive Controller Keyboard. All rights reserved.
                    </p>
                    <p className="text-xs text-text-muted/60 mt-2">
                        Developed by Vatsal Unadkat
                    </p>
                </div>
            </div>
        </footer>
    )
}
