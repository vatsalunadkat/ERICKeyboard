const sections = [
    {
        title: 'Data Collection',
        content: 'ERICKeyboard does not collect, store, or transmit any personal data, typed text, passwords, or usage statistics. Your privacy is not just a feature - it is a core design principle.',
    },
    {
        title: 'Keyboard Input & Data Processing',
        content: 'All text processing occurs entirely on your device. Keystrokes are processed locally by the keyboard\'s chord engine and immediately passed to the active application. No text is logged, stored, or sent externally. ERICKeyboard does not use "Full Access" or any network capability - it operates without any internet connectivity.',
    },
    {
        title: 'Settings Storage',
        content: 'Your preferences (layout choice, theme, font, accessibility options) are stored locally on your device using platform-standard storage (Android DataStore / iOS UserDefaults). These settings never leave your device and are not backed up to any external server.',
    },
    {
        title: 'Network Permissions',
        content: 'ERICKeyboard requests no internet or network permissions. The app operates fully offline. No data is transmitted to or received from any server at any time.',
    },
    {
        title: 'Third-Party Services',
        content: 'ERICKeyboard uses no third-party analytics, advertising, crash reporting, tracking SDKs, or data processing services of any kind.',
    },
    {
        title: 'Data Sharing',
        content: 'ERICKeyboard does not share any data with third parties. There is no data to share because no data is collected.',
    },
    {
        title: 'Data Retention & Deletion',
        content: 'Since ERICKeyboard does not collect or store any personal data, there is no data to retain or delete. Your locally stored preferences are automatically removed when you uninstall the app.',
    },
    {
        title: 'Children\'s Privacy (COPPA & GDPR-K)',
        content: 'ERICKeyboard does not knowingly collect any information from children under 13 (or under 16 in applicable jurisdictions). No information is collected from any user of any age. The app contains no in-app purchases, advertisements, or external links that could expose children to harmful content.',
    },
    {
        title: 'Security',
        content: 'Because ERICKeyboard collects no data and requires no network access, there is no risk of data breach or unauthorized access to user information through the app. All keyboard processing happens in an isolated on-device environment.',
    },
    {
        title: 'International Users (GDPR, CCPA)',
        content: 'ERICKeyboard is fully compliant with the General Data Protection Regulation (GDPR), California Consumer Privacy Act (CCPA), and similar privacy regulations worldwide. Since no personal data is collected, processed, or stored, there is no data subject to these regulations. Users have no data to request, modify, or delete.',
    },
    {
        title: 'Apple App Store Specific (App Tracking Transparency)',
        content: 'ERICKeyboard does not track users across other companies\' apps or websites. ERICKeyboard does not use the Advertising Identifier (IDFA) or any other device identifiers for tracking purposes. The app does not participate in any ad networks or attribution services.',
    },
    {
        title: 'Google Play Specific (Data Safety)',
        content: 'In accordance with Google Play\'s Data Safety section requirements: ERICKeyboard collects no data. No data is shared with third parties. No data is processed. The app does not use any Android permissions related to data collection (no internet, no contacts, no location, no storage beyond app-local preferences).',
    },
    {
        title: 'Source Available',
        content: 'ERICKeyboard is source available. You can inspect, audit, and verify every line of code.',
        link: { href: 'https://github.com/vatsalunadkat/ERICKeyboard', text: 'github.com/vatsalunadkat/ERICKeyboard' },
    },
    {
        title: 'Contact',
        content: 'If you have questions or concerns about this privacy policy, please open an issue on our GitHub Issues page.',
        link: { href: 'https://github.com/vatsalunadkat/ERICKeyboard/issues', text: 'GitHub Issues' },
    },
    {
        title: 'Changes to This Policy',
        content: 'Any changes to this privacy policy will be posted on this page with an updated effective date. We encourage you to review this page periodically. Continued use of the app after changes constitutes acceptance of the updated policy.',
    },
]

export default function Privacy() {
    return (
        <>
            <section className="pt-28 pb-8">
                <div className="max-w-3xl mx-auto px-6 text-center">
                    <h1 className="text-4xl md:text-5xl font-bold text-text mb-4">
                        Privacy Policy
                    </h1>
                    <p className="text-text-muted">
                        Effective Date: March 17, 2026
                    </p>
                    <p className="text-text-muted mt-4 max-w-2xl mx-auto leading-relaxed">
                        This privacy policy describes how ERICKeyboard ("the App," "we," "our") handles user data.
                        This policy applies to the ERICKeyboard app on all platforms, including Google Play (Android) and the Apple App Store (iOS).
                    </p>
                </div>
            </section>

            <section className="py-20 md:py-28">
                <div className="max-w-3xl mx-auto px-6 space-y-6">
                    {sections.map((section, i) => (
                        <div
                            key={i}
                            className="bg-white/40 rounded-2xl p-6 shadow-sm"
                        >
                            <h2 className="text-lg font-bold text-text mb-2">{section.title}</h2>
                            <p className="text-sm text-text-muted leading-relaxed">
                                {section.content}
                                {section.link && (
                                    <>
                                        {' '}
                                        <a
                                            href={section.link.href}
                                            target="_blank"
                                            rel="noopener noreferrer"
                                            className="text-periwinkle hover:underline"
                                        >
                                            {section.link.text} ↗
                                        </a>
                                    </>
                                )}
                            </p>
                        </div>
                    ))}
                </div>
            </section>
        </>
    )
}
