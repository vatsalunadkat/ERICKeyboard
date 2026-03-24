# ERICK Website

A modern React SPA that promotes the ERICK (Ergonomic Radial Inclusive Controller Keyboard) mobile app.

## Tech Stack

- **React 19** — UI framework
- **Vite 8** — Build tool
- **Tailwind CSS v4** — Utility-first styling
- **Framer Motion** — Animations
- **React Router** — SPA routing (HashRouter for GitHub Pages)

## Project Structure

```
website/
├── src/
│   ├── components/      # Shared UI components
│   │   ├── Navbar.jsx
│   │   ├── Footer.jsx
│   │   ├── FeatureCard.jsx
│   │   ├── TimelineCard.jsx
│   │   ├── SectionWrapper.jsx
│   │   ├── JoystickDemo.jsx
│   │   └── ScrollToTop.jsx
│   ├── context/         # React context providers
│   │   └── ThemeContext.jsx
│   ├── pages/           # Route pages
│   │   ├── Landing.jsx
│   │   ├── Evolution.jsx
│   │   ├── Features.jsx
│   │   └── Privacy.jsx
│   ├── App.jsx          # Root component with routing
│   ├── main.jsx         # Entry point
│   └── index.css        # Tailwind + custom theme
├── public/
│   ├── 404.html         # SPA redirect for GitHub Pages
│   └── .nojekyll        # Bypass Jekyll processing
├── index.html           # HTML template
├── vite.config.js       # Vite configuration
└── package.json
```

## Pages

| Route | Page | Description |
|-------|------|-------------|
| `/` | Landing | Hero, how it works, features, accessibility, controller, CTA |
| `/features` | Features | Deep dive: chord system, prediction, accessibility, layouts |
| `/evolution` | Evolution | Timeline of app development from v0.1 to current |
| `/privacy` | Privacy | Full privacy policy (unchanged from original) |

## Development

```bash
cd docs/website
npm install
npm run dev
```

Opens at `http://localhost:5173`

## Build

```bash
npm run build
```

Outputs to `docs/` (parent directory) which GitHub Pages serves.

## Deployment

### Automatic (GitHub Actions)

A workflow at `.github/workflows/deploy-website.yml` automatically builds and deploys when changes are pushed to `docs/website/` on the `main` branch.

### Manual

1. Run `npm run build` from the `docs/website` directory
2. Commit the updated `docs/index.html` and `docs/assets/` folder
3. Push to `main` branch
4. GitHub Pages serves from the `docs/` folder

## GitHub Pages Setup

1. Go to Repository Settings → Pages
2. Set Source to **Deploy from a branch**
3. Set Branch to `main` and folder to `/docs`
4. Save

## Features

- **Dark mode** — Toggle with system preference detection
- **Responsive** — Mobile-first design
- **Animations** — Smooth Framer Motion transitions
- **Interactive demo** — Animated joystick visualization
- **Pastel palette** — Headspace-inspired calm aesthetic
- **Accessibility** — Semantic HTML, ARIA labels, keyboard navigation

If you are developing a production application, we recommend using TypeScript with type-aware lint rules enabled. Check out the [TS template](https://github.com/vitejs/vite/tree/main/packages/create-vite/template-react-ts) for information on how to integrate TypeScript and [`typescript-eslint`](https://typescript-eslint.io) in your project.
