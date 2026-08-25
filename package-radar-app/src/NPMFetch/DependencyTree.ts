import z from "zod";
import { DependencySchema } from "./Dependency";

export const DependencyTreeSchema = z.object({
    tree_depth: z.number(),
    dependencies: z.array(DependencySchema).nullable(),
    package_name: z.string().nullable()
});

export type DependencyTree = z.infer<typeof DependencyTreeSchema>;

