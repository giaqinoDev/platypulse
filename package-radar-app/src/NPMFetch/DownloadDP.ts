import z from "zod";

//for zod runtime validation
export const DownloadDPSchema = z.object({
    volume: z.number().nullable(),
    end_date: z.string().nullable(),
    start_date: z.string().nullable()
});

//for Typescript type
export type DownloadDP = z.infer<typeof DownloadDPSchema>;