import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import ScrollToTop from './components/ScrollToTop'
import Landing from './pages/Landing'
import Evolution from './pages/Evolution'
import Features from './pages/Features'
import Privacy from './pages/Privacy'

export default function App() {
  return (
    <>
      <ScrollToTop />
      <div className="min-h-screen bg-cream">
        <Navbar />
        <main>
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route path="/evolution" element={<Evolution />} />
            <Route path="/features" element={<Features />} />
            <Route path="/privacy" element={<Privacy />} />
          </Routes>
        </main>
        <Footer />
      </div>
    </>
  )
}
