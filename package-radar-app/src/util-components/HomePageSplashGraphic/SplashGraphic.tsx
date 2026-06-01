import { useEffect, useRef, useState } from "react";
import platypusSplash from "../../assets/HomeSplashScreenGraphic/Platypulse Asets (2).svg";
import "./SplashGraphic.css";

interface PackageParticle {
    id: number;
    name: string;
    driftX: number;
    driftY: number;
    duration: number;
    rotation: number;
    scale: number;
}

const PACKAGE_NAMES = [
    "react",
    "typescript",
    "vite",
    "spring-boot",
    "express",
    "postgresql",
    "docker",
    "tailwindcss",
    "axios",
    "nextjs",
    "redis",
    "flask",
    "django",
    "webpack",
    "lodash",
    "mongoose"
];

function SplashGraphic() {
    const [particles, setParticles] = useState<PackageParticle[]>([]);
    const particleId = useRef(0);

    useEffect(() => {
        const spawnParticle = () => {
            const particle: PackageParticle = {
                id: particleId.current++,
                name:
                    PACKAGE_NAMES[
                        Math.floor(Math.random() * PACKAGE_NAMES.length)
                    ],
                driftX: (Math.random() - 0.5) * 350,
                driftY: -(250 + Math.random() * 200),
                duration: 4 + Math.random() * 3,
                rotation: (Math.random() - 0.5) * 30,
                scale: 0.8 + Math.random() * 0.4
            };

            setParticles(prev => [...prev, particle]);

            setTimeout(() => {
                setParticles(prev =>
                    prev.filter(p => p.id !== particle.id)
                );
            }, particle.duration * 1000);
        };

        const interval = setInterval(spawnParticle, 2000); //Controls spawn rate ~ 1 every 2 sec

        return () => clearInterval(interval);
    }, []);

    return (
        <div className="splash-container">
            <img
                src={platypusSplash}
                alt="Platypulse"
                className="platypus-image"
            />

            {/* Position this over the box opening */}
            <div className="particle-emitter">
                {particles.map(particle => (
                    <div
                        key={particle.id}
                        className="package-particle"
                        style={
                            {
                                "--drift-x": `${particle.driftX}px`,
                                "--drift-y": `${particle.driftY}px`,
                                "--duration": `${particle.duration}s`,
                                "--rotation": `${particle.rotation}deg`,
                                "--scale": particle.scale
                            } as React.CSSProperties
                        }
                    >
                        📦 {particle.name}
                    </div>
                ))}
            </div>
        </div>
    );
}

export default SplashGraphic;