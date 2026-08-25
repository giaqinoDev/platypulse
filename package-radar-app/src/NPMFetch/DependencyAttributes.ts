import z from "zod";

export const DependencyAttributesSchema = z.object({
    deprecated: z.string().nullable(),
    dependency_version: z.string().nullable()
}).nullable()

export type DependencyAttributes = z.infer<typeof DependencyAttributesSchema>;