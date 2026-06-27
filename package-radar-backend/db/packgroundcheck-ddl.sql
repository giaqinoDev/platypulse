create extension if not exists pgcrypto; -- For UUID generation

create table ecosystem(
	id integer generated always as identity primary key, --create an auto incrementing column called id and use it as a primary key
	eco varchar(50) not null unique,
	language varchar(50) not null
);

create table packages(
	id UUID primary key default gen_random_uuid(),
	eco_id integer not null references ecosystem(id) on delete cascade,
	package_name varchar(255) not null,
	unique (eco_id, package_name)
);

create table package_health_snapshots(
	id UUID primary key default gen_random_uuid(),
	package_id UUID not null references packages(id) on delete cascade,
	
	aggregate_score numeric(5,2) not null check (aggregate_score >= 0 and aggregate_score <= 100),
	activity_score numeric(5,2) not null check (activity_score >= 0 and activity_score <= 100),
	maintenance_score numeric(5,2) not null check (maintenance_score >= 0 and maintenance_score <= 100),
	adoption_score numeric(5,2) not null check (adoption_score >= 0 and adoption_score <= 100),
	risk_score numeric(5,2) not null check (risk_score >= 0 and risk_score <= 100),

	last_scan timestamptz not null,
	created_at timestamptz not null default now(),
	updated_at timestamptz not null default now(),
	algorithm_v text not null
);

create table raw_signals(
	id UUID primary key default gen_random_uuid(),
	health_score_id UUID unique not null references package_health_snapshots(id) on delete cascade,
	fetched_at timestamptz not null default now(),
	raw_data jsonb not null
);

create table universal_signals(
	id UUID primary key default gen_random_uuid(),
	health_score_id UUID unique not null references package_health_snapshots(id) on delete cascade,
	calculated_at timestamptz not null default now(),

	--Time Based:
	days_since_last_release DOUBLE PRECISION,
    days_since_last_commit DOUBLE PRECISION,
    commits_last_90_days DOUBLE PRECISION,
    commits_last_30_days DOUBLE PRECISION,

    --Releases:
    releases_per_year DOUBLE PRECISION,
    total_releases DOUBLE PRECISION,

    --Popularity:
    downloads_per_week DOUBLE PRECISION,
    stars DOUBLE PRECISION,
    forks DOUBLE PRECISION,
    dependent_repos DOUBLE PRECISION,
    dependent_packages DOUBLE PRECISION,

    --Issues:
    open_issues DOUBLE PRECISION,
    closed_issues DOUBLE PRECISION,

    --Contributors:
    contributors_count DOUBLE PRECISION,
    commits_count DOUBLE PRECISION,
    top_contributor_commit_count DOUBLE PRECISION,

    --Dependencies:
    direct_dependencies_count DOUBLE PRECISION,
    direct_deprecated_dependencies DOUBLE PRECISION,
    dependencies_depth DOUBLE PRECISION,

    --Repo Info:
    repo_age_days DOUBLE PRECISION,
    is_archived boolean
);

--Indexing for quick lookup
create index index_package_lookup on packages(eco_id, package_name);
create index index_package_health_snapshot on package_health_snapshots(package_id, created_at desc);