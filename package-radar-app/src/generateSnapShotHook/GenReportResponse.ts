import z from "zod";

export const GenReportResponseSchema = z.uuid();

export type generated_report_id = z.infer<typeof GenReportResponseSchema>;