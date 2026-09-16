import z from "zod";

export const SnapShotSchema = z.object(
    {
        snap_shot_id: z.uuid(),
        overall_score: z.number(),
        created_at: z.coerce.date() //coerce telsl zod that if the response is a valid date string, convert it to js date
    }
)

export const SnapShotsTimelineSchema = z.array(SnapShotSchema);

export type SnapShotTimeline = z.infer<typeof SnapShotsTimelineSchema>;