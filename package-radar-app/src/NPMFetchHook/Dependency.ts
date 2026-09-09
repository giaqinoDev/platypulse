import z from "zod";

export type Dependency = {
  name: string,
  attributes: {
    dependency_version: string | null,
    deprecated: boolean
  }| null,
  children: Dependency[]|null
};

//for zod runtime validation
export const DependencySchema: z.ZodType<Dependency> = z.object({
  deprecated: z.boolean(),
  get dependencies() {
    return z.array(DependencySchema).nullable();
  },
  dependency_name: z.string(),
  dependency_version: z.string().nullable()
}).transform((data) => {
  return {
    name: data.dependency_name,
    attributes: {
      dependency_version: data.dependency_version,
      deprecated: data.deprecated
    },
    children: data.dependencies
  };
});

//for Typescript type
// export type Dependency = z.infer<typeof DependencySchema>;