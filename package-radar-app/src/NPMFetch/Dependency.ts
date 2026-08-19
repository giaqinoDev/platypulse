import z from "zod";

//for zod runtime validation
export const DependencySchema = z.object({
  deprecated: z.boolean(),
  get dependencies() {
    return z.array(DependencySchema).nullable();
  },
  dependency_name: z.string(),
  dependency_version: z.string().nullable()
});

//for Typescript type
export type Dependency = z.infer<typeof DependencySchema>;