import { useEffect, useRef, useState } from "react";
import platypusSplash from "../../assets/HomeSplashScreenGraphic/PlatypulseSplashAsset.svg";
import "./SplashGraphic.css";

// THIS COMPONENT IS VIBE CODED (AI GEN)
interface PackageParticle {
    id: number;
    name: string;
    driftX: number;
    driftY: number;
    duration: number;
    rotation: number;
    spin: number;
    scale: number;
}

const PACKAGE_NAMES = [
    "react",
    "vite",
    "axios",
    "lodash",
    "express",
    "typescript",
    "tailwindcss",
    "@reduxjs/toolkit",
    "@tanstack/react-query",
    "@mui/material",
    "@prisma/client",
    "@nestjs/core",
    "socket.io",
    "jsonwebtoken",
    "playwright",
    "eslint",
    "prettier",
    "mongoose",
    "date-fns",
    "zod",
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

                // Initial angle
                rotation: (Math.random() - 0.5) * 30,

                // Amount to spin while floating
                spin: (Math.random() - 0.5) * 180,

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
                                "--spin": `${particle.spin}deg`,
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