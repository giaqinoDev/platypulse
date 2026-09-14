import { DependencySchema } from "./Dependency"
import { DependencyTreeSchema } from "./DependencyTree";
import { DownloadDPSchema } from "./DownloadDP"
import { z } from "zod";

export const NpmFetchResponseSchema = z.object({
    report_id: z.uuid(),
    package_id: z.uuid(),
    can_analyze: z.boolean(),
    overall: z.number(),
    activity: z.number(),
    maintenance: z.number(),
    adoption: z.number(),
    risk: z.number(),
    raw_signals: z.object({
        git_hub_reg: z.object({
            repo_info: z.object({
                archived: z.boolean(),
                repo_age: z.number(),
                pushed_at: z.string(),
                created_at: z.string(),
                updated_at: z.string(),
                forks_count: z.number(),
                stars_count: z.number(),
                open_issues_count: z.number(),
                closed_issues_count: z.number(),
                days_since_last_push: z.number()
            }).nullable(),
            commit_info: z.object({
                commits_last_30_days: z.number(),
                commits_last_90_days: z.number(),
                commit_timeline_volume: z.record(z.string(), z.number()),
            }).nullable(),
            contributors_info: z.object({
                total_commits: z.number(),
                top_commits_ratio: z.number(),
                contributors_count: z.number(),
                top_contributor_commits: z.number(),
            }).nullable()
        }).nullable().optional(),
        package_info: z.object({
            pkge_name: z.string(),
            description: z.string().nullable(),
            latest_version: z.string().nullable(),
            latest_version_info: z.object({
                author: z.union([z.object({
                    name: z.string().optional(),
                    email: z.string().nullable().optional()
                }), z.string()]).nullable(),
                license: z.string().nullable().optional(),
                homepage: z.string().nullable().optional(),
                keywords: z.array(z.string()).nullable(),
                repository: z.object({
                    url: z.string().nullable().optional(),
                    type: z.string().nullable().optional(),
                    directory: z.string().nullable().optional()
                }).nullable(),
                npm_version: z.string().nullable(),
                dependencies: z.array(DependencySchema).nullable(),
                node_version: z.string().nullable(),
                dependency_tree: DependencyTreeSchema.nullable().optional()
                // dependency_tree: z.object({
                //     tree_depth: z.number(),
                //     dependencies: z.array(DependencySchema).nullable(),
                //     package_name: z.string().nullable()
                // }).nullable().optional()
            }).nullable(),
            versions_release_info: z.record(z.string(), z.string()).nullable(),
        }),
        downloads_timeline: z.array(DownloadDPSchema).nullable(),
        dependents_count: z.object({
            dependent_repo_count: z.number().nullable().optional(),
            dependent_packages_count: z.number().nullable().optional()
        })
    }).nullable()
}).nullable();

export type NpmFetchResponse = z.infer<typeof NpmFetchResponseSchema>;