import "./SnapShots.css"
import { HiOutlineDocumentReport } from "react-icons/hi";


interface SnapShotsProps {
    fetchedSnapShots: {
        snap_shot_id: string;
        overall_score: number;
        created_at: Date;
    }[]|null
}

function SnapShots({ fetchedSnapShots }: SnapShotsProps){
    if(!fetchedSnapShots){
        return;
    }

    return(
        <div>
            {fetchedSnapShots?.map((snapshot) => (
                <div key={snapshot.snap_shot_id} className="snap-shot-entry">
                    <HiOutlineDocumentReport className="report-icon"/>
                    <p className="snap-shot-detail" id="creation-date-time">Created {snapshot.created_at.toDateString()}, {snapshot.created_at.toLocaleTimeString()}</p>
                    <p className="snap-shot-detail" id="overall-score">{snapshot.overall_score}%</p>
                </div>
            ))}
        </div>
    );
}

export default SnapShots;