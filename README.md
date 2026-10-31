# Platypulse

**Platypulse** is an open-source package health monitoring platform designed to help developers evaluate the reliability, maintenance activity, adoption, and potential risks of packages in the npm ecosystem.

By aggregating data from multiple sources and applying a weighted scoring model, Platypulse provides developers with a consolidated overview of package health to help inform dependency selection and management decisions.

Live Demo: https://platypulse-frontend.onrender.com/home

## Key Features

- **Package Health Scoring:** Generates an aggregate health score (0–100) based on four categories: Activity, Maintenance, Adoption, and Risk.
- **Multi-Source Data Aggregation:** Collects package metadata, download statistics, repository activity, contributor information, and dependency data from external APIs.
- **Dependency Analysis:** Examines package dependencies, including dependency depth and deprecated dependencies, to help identify potential maintenance and security risks.
- **Historical Reporting:** Stores timestamped health reports, allowing users to review previous scores and track changes in package health over time.
- **Detailed Health Reports:** Provides category-level score breakdowns alongside the underlying package signals used to calculate each score.
- **Rate Limiting:** Implements API request throttling to manage resource consumption and protect backend endpoints.

## Project Architecture

Platypulse follows a **React frontend / Spring Boot backend architecture**, with PostgreSQL providing persistent storage.

### Data Pipeline

1. **Data Collection:** Retrieves package information from the npm Registry, npm Downloads API, GitHub API, and Libraries.io.
2. **Signal Extraction:** Normalizes raw API responses into a unified set of package health metrics.
3. **Health Scoring:** Applies weighted scoring algorithms to calculate individual category scores and an aggregate package health score.
4. **Persistence:** Stores generated reports, normalized signals, and raw API responses in PostgreSQL.
5. **Presentation:** Exposes REST endpoints consumed by the React frontend to display package reports and historical snapshots.

### High-Level Infrastructure

| Component | Technology | Responsibility |
|---|---|---|
| Frontend | React, TypeScript, Vite | User interface and data visualization |
| Backend | Java, Spring Boot | REST API, data aggregation, signal extraction, and scoring |
| Database | PostgreSQL | Package metadata, health reports, and historical snapshots |
| External APIs | npm, GitHub, Libraries.io | Package and repository data collection |

## Technology Stack

**Frontend**
- React 19
- TypeScript
- Vite
- React Router
- Recharts
- React D3 Tree / XYFlow
- Zod

**Backend**
- Java 25
- Spring Boot 4.1
- Spring WebMVC & WebFlux
- Spring Data JPA / Hibernate
- Reactor (`Mono`) for asynchronous API workflows
- Jackson for JSON processing
- MapStruct for entity mapping
- Lombok
- Bucket4j for rate limiting

**Database & Infrastructure**
- PostgreSQL
- Maven
- REST APIs
- GitHub API, npm Registry, Libraries.io

## Scoring Model

Platypulse evaluates package health across four weighted dimensions:

| Category | Weight | Signals Evaluated |
|---|---:|---|
| Activity | 15% | Commit frequency, commit recency, release recency |
| Maintenance | 30% | Issue responsiveness, contributor distribution, release cadence |
| Adoption | 35% | Weekly downloads, dependent packages, GitHub community activity |
| Risk | 20% | Archived repositories, dependency depth, deprecated dependencies |

Each category receives an individual score, which contributes to the final weighted aggregate score.

---
